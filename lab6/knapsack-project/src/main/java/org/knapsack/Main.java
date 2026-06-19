package org.knapsack;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Give number of items:");
        int n = scanner.nextInt();
        
        System.out.println("Give seed:");
        int seed = scanner.nextInt();
        
        System.out.println("Give knapsack capacity:");
        int capacity = scanner.nextInt();

        Problem problem = new Problem(n, seed, 1, 10);
        System.out.print(problem.toString());

        Result result = problem.Solve(capacity);
        System.out.print(result.toString());
        scanner.close();
    }
}