package org.knapsack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ProblemTest {

    //sprawdzenie czy jest co najmniej jeden przedmiot w wyniku
    @Test
    public void testAtLeastOneItemFits() {
        Problem problem = new Problem(5, 123, 1, 10);
        Result result = problem.Solve(10);
        assertFalse(result.items.isEmpty(), "Wynik powinien zawierać co najmniej jeden element.");
    }

    //wielkość przedmiotów jest większa niż pojemność plecaka
    @Test
    public void testNoItemsFit() {
        Problem problem = new Problem(5, 123, 5, 10); // wagi - od 5-10, wartości - od 5-10
        Result result = problem.Solve(3); // Pojemność to 3, żaden z przedmiotów się nie zmieści
        assertTrue(result.items.isEmpty(), "Wynik powinien być pusty.");
        assertEquals(0, result.totalValue);
        assertEquals(0, result.totalWeight);
    }

    //sprawdzenie czy przedmioty w wyniku mają wagi i wartości w zadanym zakresie
    @Test
    public void testItemsWithinBounds() {
        int lowerBound = 1;
        int upperBound = 10;
        Problem problem = new Problem(100, 42, lowerBound, upperBound);
        
        for (Item item : problem.items) {
            assertTrue(item.weight >= lowerBound && item.weight <= upperBound, "Waga poza zakresem!");
            assertTrue(item.value >= lowerBound && item.value <= upperBound, "Wartość poza zakresem!");
        }
    }

    //sprawdzenie czy suma wag i wartości przedmiotów w wyniku jest zgodna z tym co jest w obiekcie Result
    @Test
    public void testSpecificInstanceCorrectness() {
        Problem problem = new Problem(10, 1, 1, 10);
        Result result = problem.Solve(15);
        
        int expectedWeight = 0;
        int expectedValue = 0;
        
        for (Item item : result.items) {
            expectedWeight += item.weight;
            expectedValue += item.value;
        }
        
        assertEquals(expectedWeight, result.totalWeight, "Zsumowana waga przedmiotów nie zgadza się z wagą w obiekcie Result.");
        assertEquals(expectedValue, result.totalValue, "Zsumowana wartość przedmiotów nie zgadza się z wartością w obiekcie Result.");
        assertTrue(result.totalWeight <= 15, "Całkowita waga przekracza pojemność plecaka!");
    }
}