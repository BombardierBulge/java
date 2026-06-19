package pl.pwr.imageapp.processor;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelImageProcessor {

    //skalowanie
    public static BufferedImage scale(BufferedImage src, int width, int height) {
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = dest.createGraphics();
        g2d.drawImage(src, 0, 0, width, height, null);
        g2d.dispose();
        return dest;
    }

    //obroty
    public static BufferedImage rotate(BufferedImage src, boolean left) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(h, w, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                if (left) {
                    dest.setRGB(y, w - 1 - x, rgb); // W lewo
                } else {
                    dest.setRGB(h - 1 - y, x, rgb); // W prawo
                }
            }
        }
        return dest;
    }

    //negatyw
    public static BufferedImage processNegativeParallel(BufferedImage src) throws InterruptedException {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        int chunkSize = h / 4;

        for (int i = 0; i < 4; i++) {
            final int startY = i * chunkSize;
            final int endY = (i == 3) ? h : (i + 1) * chunkSize;

            executor.submit(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < w; x++) {
                        int rgb = src.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        r = 255 - r;
                        g = 255 - g;
                        b = 255 - b;

                        int newRgb = (0xFF << 24) | (r << 16) | (g << 8) | b;
                        dest.setRGB(x, y, newRgb);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        return dest;
    }

    //progowanie
    public static BufferedImage processThresholdParallel(BufferedImage src, int threshold) throws InterruptedException {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        int chunkSize = h / 4;

        for (int i = 0; i < 4; i++) {
            final int startY = i * chunkSize;
            final int endY = (i == 3) ? h : (i + 1) * chunkSize;

            executor.submit(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < w; x++) {
                        int rgb = src.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        // Konwersja na odcienie szarosci (Luminancja)
                        int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                        int finalColor = (gray > threshold) ? 255 : 0;

                        int newRgb = (0xFF << 24) | (finalColor << 16) | (finalColor << 8) | finalColor;
                        dest.setRGB(x, y, newRgb);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        return dest;
    }

    //konturowanie
    public static BufferedImage processContourParallel(BufferedImage src) throws InterruptedException {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        int chunkSize = h / 4;

        for (int i = 0; i < 4; i++) {
            final int startY = i * chunkSize;
            final int endY = (i == 3) ? h : (i + 1) * chunkSize;

            executor.submit(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < w; x++) {
                        //obramowanie - ustawienie krawędzi na czarne
                        if (x == 0 || y == 0 || x == w - 1 || y == h - 1) {
                            dest.setRGB(x, y, 0xFF000000);
                            continue;
                        }

                        // róznice pixeli
                        int rgb = src.getRGB(x, y);
                        int rgbRight = src.getRGB(x + 1, y);
                        int rgbBottom = src.getRGB(x, y + 1);

                        int gray = (int) (0.299 * ((rgb >> 16) & 0xFF) + 0.587 * ((rgb >> 8) & 0xFF) + 0.114 * (rgb & 0xFF));
                        int grayRight = (int) (0.299 * ((rgbRight >> 16) & 0xFF) + 0.587 * ((rgbRight >> 8) & 0xFF) + 0.114 * (rgbRight & 0xFF));
                        int grayBottom = (int) (0.299 * ((rgbBottom >> 16) & 0xFF) + 0.587 * ((rgbBottom >> 8) & 0xFF) + 0.114 * (rgbBottom & 0xFF));

                        int diffX = Math.abs(gray - grayRight);
                        int diffY = Math.abs(gray - grayBottom);
                        int edge = Math.min(255, diffX + diffY);

                        int newRgb = (0xFF << 24) | (edge << 16) | (edge << 8) | edge;
                        dest.setRGB(x, y, newRgb);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        return dest;
    }
}