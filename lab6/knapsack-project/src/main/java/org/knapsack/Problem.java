package org.knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Problem {
    public int n;
    public int seed;
    public int lowerBound;
    public int upperBound;
    public List<Item> items;

    public Problem(int n, int seed, int lowerBound, int upperBound) {
        this.n = n;
        this.seed = seed;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.items = new ArrayList<>();
        
        Random random = new Random(seed);

        for (int i = 0; i < n; i++) {
            int weight = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
            int value = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
            items.add(new Item(i, value, weight));
        }
    }


    public Result Solve(int capacity) {
        Result result = new Result();
  
        List<Item> sortedItems = new ArrayList<>(items);
        

        sortedItems.sort((a, b) -> {
            double ratioA = (double) a.value / a.weight;
            double ratioB = (double) b.value / b.weight;
            return Double.compare(ratioB, ratioA);
        });

        int remainingCapacity = capacity;

        for (Item item : sortedItems) {
            if (remainingCapacity == 0) break;
            
            int count = remainingCapacity / item.weight;
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    result.items.add(item);
                }
                result.totalWeight += count * item.weight;
                result.totalValue += count * item.value;
                remainingCapacity -= count * item.weight;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}