package org.knapsack;

import java.util.ArrayList;
import java.util.List;

public class Result {
    public List<Item> items = new ArrayList<>();
    public int totalValue = 0;
    public int totalWeight = 0;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------\n");
        for (Item item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("Weight: ").append(totalWeight).append("\n");
        sb.append("Value: ").append(totalValue).append("\n");
        return sb.toString();
    }
}