package org.knapsack;

public class Item {
    public int id;
    public int value;
    public int weight;

    public Item(int id, int value, int weight) {
        this.id = id;
        this.value = value;
        this.weight = weight;
    }


    @Override
    public String toString() {
        return "No: " + id + " v: " + value + " w: " + weight;
    }
}