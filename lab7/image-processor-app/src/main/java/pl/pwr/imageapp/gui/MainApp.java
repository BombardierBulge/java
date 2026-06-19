package pl.pwr.imageapp.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.pwr.imageapp.logger.AppLogger;
import pl.pwr.imageapp.processor.ParallelImageProcessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainApp extends Application {

    private StackPane rootPane;
    private ImageView originalImageView;
    private ImageView processedImageView;
    
    private Button btnLoad;
    private Button btnScale;
    private Button btnRotLeft;
    private Button btnRotRight;
    private ComboBox<String> comboOperations;
    private Button btnExecute;
    private Button btnSave;
    
    //obrazy w formacie BufferedImage
    private BufferedImage originalBI;
    private BufferedImage processedBI;
    
    private boolean operationsPerformed = false;
    private String currentFileName = "";

    @Override
    public void start(Stage primaryStage) {
        //logi
        AppLogger.info("Uruchomiono aplikacje okienkowa.");

        primaryStage.setTitle("PWr Image Processor");
        rootPane = new StackPane();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #800000; -fx-background-radius: 5;");
        Label titleLabel = new Label("PWr Image Processor v1.0");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label logoLabel = new Label(" Politechnika Wroclawska ");
        logoLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: white; -fx-border-radius: 3;");
        header.getChildren().addAll(titleLabel, spacer, logoLabel);
        mainLayout.setTop(header);

        VBox controls = new VBox(10);
        controls.setPadding(new Insets(15, 10, 15, 0));
        controls.setPrefWidth(220);

        btnLoad = new Button("Wczytaj obraz");
        btnLoad.setMaxWidth(Double.MAX_VALUE);
        Label labelRot = new Label("Obracanie:");
        btnRotLeft = new Button(" ⟲ 90° ");
        btnRotRight = new Button(" ⟳ 90° ");
        HBox rotateBox = new HBox(10, btnRotLeft, btnRotRight);
        btnRotLeft.setMaxWidth(Double.MAX_VALUE);
        btnRotRight.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRotLeft, Priority.ALWAYS);
        HBox.setHgrow(btnRotRight, Priority.ALWAYS);
        btnScale = new Button("Skaluj obraz");
        btnScale.setMaxWidth(Double.MAX_VALUE);
        comboOperations = new ComboBox<>();
        comboOperations.getItems().addAll("Negatyw", "Progowanie", "Konturowanie");
        comboOperations.setValue(null);
        comboOperations.setMaxWidth(Double.MAX_VALUE);

        btnExecute = new Button("Wykonaj operacje");
        btnExecute.setMaxWidth(Double.MAX_VALUE);
        btnExecute.setStyle("-fx-background-color: #4ade80; -fx-text-fill: black; -fx-font-weight: bold;");
        btnSave = new Button("Zapisz obraz");
        btnSave.setMaxWidth(Double.MAX_VALUE);

        setControlsDisabled(true);

        controls.getChildren().addAll(
                new Label("Zrodlo pliku:"), btnLoad, new Separator(),
                labelRot, rotateBox, btnScale, new Separator(),
                new Label("Przetwarzanie (Wielowatkowe):"), comboOperations, btnExecute, new Separator(),
                btnSave
        );
        mainLayout.setLeft(controls);
        HBox imageArea = new HBox(20);
        imageArea.setPadding(new Insets(15));
        imageArea.setAlignment(Pos.CENTER);

        VBox origBox = new VBox(5);
        origBox.setAlignment(Pos.CENTER);
        Label origLabel = new Label("Brak pliku");
        originalImageView = new ImageView();
        originalImageView.setFitWidth(290);
        originalImageView.setFitHeight(290);
        originalImageView.setPreserveRatio(true);
        origBox.getChildren().addAll(origLabel, originalImageView);

        VBox procBox = new VBox(5);
        procBox.setAlignment(Pos.CENTER);
        Label procLabel = new Label("Podglad zmian");
        processedImageView = new ImageView();
        processedImageView.setFitWidth(290);
        processedImageView.setFitHeight(290);
        processedImageView.setPreserveRatio(true);
        procBox.getChildren().addAll(procLabel, processedImageView);

        imageArea.getChildren().addAll(origBox, procBox);
        HBox.setHgrow(origBox, Priority.ALWAYS);
        HBox.setHgrow(procBox, Priority.ALWAYS);
        mainLayout.setCenter(imageArea);
        HBox footer = new HBox();
        footer.setPadding(new Insets(5));
        footer.setAlignment(Pos.CENTER);
        Label authorLabel = new Label("Autor: Bartosz Żurawski | Indeks: 280127 | Platformy Programistyczne");
        authorLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
        footer.getChildren().add(authorLabel);
        mainLayout.setBottom(footer);
        btnLoad.setOnAction(e -> handleLoadImage(primaryStage, origLabel));
        btnRotLeft.setOnAction(e -> handleRotate(true));
        btnRotRight.setOnAction(e -> handleRotate(false));
        btnScale.setOnAction(e -> handleScale(primaryStage));
        btnExecute.setOnAction(e -> handleExecuteOperation(primaryStage));
        btnSave.setOnAction(e -> handleSaveImage(primaryStage));
        rootPane.getChildren().add(mainLayout);
        primaryStage.setScene(new Scene(rootPane, 900, 550));
        primaryStage.show();
    }

    private void setControlsDisabled(boolean disabled) {
        btnRotLeft.setDisable(disabled);
        btnRotRight.setDisable(disabled);
        btnScale.setDisable(disabled);
        comboOperations.setDisable(disabled);
        btnExecute.setDisable(disabled);
        btnSave.setDisable(disabled);
    }

    private void handleLoadImage(Stage stage, Label origLabel) {
        AppLogger.info("Uzytkownik wywolal okno wczytywania pliku.");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz obraz (.jpg)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Obrazy JPG", "*.jpg", "*.JPG"));
        
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String name = selectedFile.getName().toLowerCase();
            if (!name.endsWith(".jpg")) {
                showToast("Niedozwolony format pliku");
                AppLogger.warn("Proba wczytania niepoprawnego formatu: " + selectedFile.getName());
                return;
            }

            try {
                //czyszczenie poprzednich danych
                originalBI = null;
                processedBI = null;
                originalImageView.setImage(null);
                processedImageView.setImage(null);
                operationsPerformed = false;

                //wczyt
                originalBI = ImageIO.read(selectedFile);
                //kopia
                processedBI = ParallelImageProcessor.scale(originalBI, originalBI.getWidth(), originalBI.getHeight());

                originalImageView.setImage(SwingFXUtils.toFXImage(originalBI, null));
                processedImageView.setImage(SwingFXUtils.toFXImage(processedBI, null));
                
                currentFileName = selectedFile.getName();
                origLabel.setText("Oryginal: " + currentFileName);
                
                setControlsDisabled(false);
                showToast("Pomyslnie zaladowano plik");
                AppLogger.info("Pomyslnie zaladowano plik obrazu: " + currentFileName);
            } catch (Exception ex) {
                showToast("Nie udalo sie zaladowac pliku");
                AppLogger.error("Blad ladowania pliku: " + ex.getMessage());
            }
        }
    }

    private void handleRotate(boolean left) {
        if (processedBI == null) return;
        AppLogger.info("Uzytkownik wywolal obrot obrazu w " + (left ? "lewo" : "prawo"));
        try {
            processedBI = ParallelImageProcessor.rotate(processedBI, left);
            processedImageView.setImage(SwingFXUtils.toFXImage(processedBI, null));
            operationsPerformed = true;
        } catch (Exception ex) {
            AppLogger.error("Blad podczas obracania obrazu: " + ex.getMessage());
        }
    }

    private void handleScale(Stage parentStage) {
        AppLogger.info("Uzytkownik otworzyl okno modalne skalowania.");
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Skalowanie obrazu");

        VBox layout = new VBox(8);
        layout.setPadding(new Insets(15));

        Label lblW = new Label("Szerokosc (0-3000 px):");
        TextField txtW = new TextField(String.valueOf(processedBI.getWidth()));
        Label errW = new Label(); errW.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        applyNumericConstraint(txtW);

        Label lblH = new Label("Wysokosc (0-3000 px):");
        TextField txtH = new TextField(String.valueOf(processedBI.getHeight()));
        Label errH = new Label(); errH.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        applyNumericConstraint(txtH);

        Button btnRestore = new Button("Przywroc oryginalne wymiary");
        btnRestore.setOnAction(e -> {
            txtW.setText(String.valueOf(originalBI.getWidth()));
            txtH.setText(String.valueOf(originalBI.getHeight()));
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button btnConfirm = new Button("Zmien rozmiar");
        Button btnCancel = new Button("Anuluj");
        buttons.getChildren().addAll(btnCancel, btnConfirm);

        layout.getChildren().addAll(lblW, txtW, errW, lblH, txtH, errH, btnRestore, buttons);

        btnCancel.setOnAction(e -> {
            txtW.clear(); txtH.clear();
            modal.close();
        });

        btnConfirm.setOnAction(e -> {
            errW.setText(""); errH.setText("");
            boolean valid = true;

            if (txtW.getText().trim().isEmpty()) { errW.setText("Pole jest wymagane"); valid = false; }
            if (txtH.getText().trim().isEmpty()) { errH.setText("Pole jest wymagane"); valid = false; }

            if (!valid) return;

            int newW = Integer.parseInt(txtW.getText());
            int newH = Integer.parseInt(txtH.getText());

            try {
                processedBI = ParallelImageProcessor.scale(processedBI, newW, newH);
                processedImageView.setImage(SwingFXUtils.toFXImage(processedBI, null));
                operationsPerformed = true;
                showToast("Zmieniono rozmiar obrazu");
                AppLogger.info("Przeskalowano obraz na wymiary: " + newW + "x" + newH);
                modal.close();
            } catch (Exception ex) {
                AppLogger.error("Blad skalowania: " + ex.getMessage());
            }
        });

        modal.setScene(new Scene(layout, 320, 280));
        modal.showAndWait();
    }

    private void handleExecuteOperation(Stage parentStage) {
        String selectedOp = comboOperations.getValue();
        if (selectedOp == null) {
            showToast("Nie wybrano operacji do wykonania");
            AppLogger.warn("Kliknieto 'Wykonaj' bez wyboru operacji z listy.");
            return;
        }

        AppLogger.info("Rozpoczeto wykonywanie operacji: " + selectedOp);
        try {
            if (selectedOp.equals("Negatyw")) {
                processedBI = ParallelImageProcessor.processNegativeParallel(processedBI);
                processedImageView.setImage(SwingFXUtils.toFXImage(processedBI, null));
                operationsPerformed = true;
                showToast("Negatyw zostal wygenerowany pomyslnie!");
                AppLogger.info("Pomyslnie wykonano operacje Negatyw.");
            } else if (selectedOp.equals("Konturowanie")) {
                processedBI = ParallelImageProcessor.processContourParallel(processedBI);
                processedImageView.setImage(SwingFXUtils.toFXImage(processedBI, null));
                operationsPerformed = true;
                showToast("Konturowanie zostalo przeprowadzone pomyslnie!");
                AppLogger.info("Pomyslnie wykonano operacje Konturowanie.");
            } else if (selectedOp.equals("Progowanie")) {
                openThresholdModal(parentStage);
            }
        } catch (Exception ex) {
            showToast("Nie udalo sie wykonac operacji: " + selectedOp);
            AppLogger.error("Blad krytyczny wykonania operacji " + selectedOp + ": " + ex.getMessage());
        }
    }

    private void openThresholdModal(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Parametry progowania");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label label = new Label("Wpisz wartosc progu (0-255):");
        TextField txtThresh = new TextField("128");
        
        //ograniczenie progowania 
        txtThresh.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtThresh.setText(newVal.replaceAll("[^\\d]", ""));
            } else if (!newVal.isEmpty()) {
                if (Integer.parseInt(newVal) > 255) txtThresh.setText(oldVal);
            }
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button btnRun = new Button("Wykonaj progowanie");
        Button btnCancel = new Button("Anuluj");
        buttons.getChildren().addAll(btnCancel, btnRun);

        layout.getChildren().addAll(label, txtThresh, buttons);

        btnCancel.setOnAction(e -> modal.close());

        btnRun.setOnAction(e -> {
            if (txtThresh.getText().isEmpty()) return;
            int thresh = Integer.parseInt(txtThresh.getText());
            try {
                //wywołanie
                processedBI = ParallelImageProcessor.processThresholdParallel(processedBI, thresh);
                processedImageView.setImage(SwingFXUtils.toFXImage(processedBI, null));
                operationsPerformed = true;
                showToast("Progowanie zostalo przeprowadzone pomyslnie!");
                //log
                AppLogger.info("Pomyslnie wykonano Progowanie z progiem: " + thresh);
                modal.close();
            } catch (Exception ex) {
                showToast("Nie udalo sie wykonac progowania.");
                AppLogger.error("Blad progowania: " + ex.getMessage());
            }
        });

        modal.setScene(new Scene(layout, 300, 150));
        modal.showAndWait();
    }

    private void handleSaveImage(Stage parentStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(parentStage);
        modal.setTitle("Zapisz plik");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        if (!operationsPerformed) {
            Label alert = new Label("Na pliku nie zostaly wykonane zadne operacje!");
            alert.setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold; -fx-background-color: #fef3c7; -fx-padding: 5; -fx-background-radius: 3;");
            layout.getChildren().add(alert);
        }

        Label prompt = new Label("Podaj nazwe pliku (bez .jpg):");
        TextField txtName = new TextField(currentFileName.replace(".jpg", "").replace(".JPG", ""));
        
        txtName.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > 100) txtName.setText(oldV);
        });

        Label errLabel = new Label(); errLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button btnSaveFile = new Button("Zapisz");
        Button btnCancel = new Button("Anuluj");
        buttons.getChildren().addAll(btnCancel, btnSaveFile);

        layout.getChildren().addAll(prompt, txtName, errLabel, buttons);

        btnCancel.setOnAction(e -> modal.close());

        btnSaveFile.setOnAction(e -> {
            String inputName = txtName.getText().trim();
            if (inputName.length() < 3) {
                errLabel.setText("Wpisz co najmniej 3 znaki");
                return;
            }

            File picturesDir = new File(System.getProperty("user.home"), "Pictures");
            if (!picturesDir.exists()) picturesDir.mkdirs();

            File targetFile = new File(picturesDir, inputName + ".jpg");

            if (targetFile.exists()) {
                showToast("Plik " + targetFile.getName() + " juz istnieje w systemie. Podaj inna nazwe pliku!");
                return;
            }

            try {
                ImageIO.write(processedBI, "jpg", targetFile);
                showToast("Zapisano obraz w pliku " + targetFile.getName());
                AppLogger.info("Zapisano przetworzony obraz w systemie: " + targetFile.getAbsolutePath());
                modal.close();
            } catch (IOException ex) {
                showToast("Nie udalo sie zapisac pliku " + targetFile.getName());
                AppLogger.error("Blad zapisu pliku: " + ex.getMessage());
            }
        });

        modal.setScene(new Scene(layout, 380, 200));
        modal.showAndWait();
    }

    private void applyNumericConstraint(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                field.setText(newVal.replaceAll("[^\\d]", ""));
            } else if (!newVal.isEmpty()) {
                if (Integer.parseInt(newVal) > 3000) field.setText(oldVal);
            }
        });
    }

    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: rgba(50, 50, 50, 0.85); -fx-text-fill: white; -fx-padding: 8px 15px; -fx-background-radius: 20px; -fx-font-size: 12px;");
        toast.setOpacity(0);
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        toast.setTranslateY(-30);
        
        rootPane.getChildren().add(toast);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0); fadeOut.setDelay(Duration.seconds(2.5));
        
        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(toast));
        fadeIn.play();
    }

    @Override
    public void stop() {
        //logi - zamkniecie aplikacji
        AppLogger.info("Zamknieto aplikacje.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}