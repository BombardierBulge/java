# Knapsack Project — Problem plecakowy

Konsolowa aplikacja rozwiązująca **problem plecakowego** (knapsack problem) algorytmem zachłannym opartym na stosunku wartość/waga.

## Cel programu

Program generuje zestaw losowych przedmiotów o określonej wadze i wartości, a następnie dobiera do plecaka o zadanej pojemności taką kombinację przedmiotów, aby zmaksymalizować łączną wartość przy ograniczeniu wagi. Każdy przedmiot może być wzięty wielokrotnie (wariant nieograniczonego plecaka).

## Struktura projektu

```
src/main/java/org/knapsack/
├── Main.java      — punkt wejścia, interakcja z użytkownikiem
├── Problem.java   — generowanie przedmiotów i algorytm rozwiązania
├── Item.java      — model przedmiotu (id, wartość, waga)
└── Result.java    — wynik: lista wybranych przedmiotów, suma wag i wartości
```

## Jak działa program

### 1. Wejście użytkownika

Program pyta o trzy parametry:

- **Liczba przedmiotów** (`n`) — ile elementów wygenerować
- **Ziarno losowości** (`seed`) — zapewnia powtarzalność wyników
- **Pojemność plecaka** (`capacity`) — maksymalna dopuszczalna waga

### 2. Generowanie przedmiotów

Klasa `Problem` tworzy `n` przedmiotów z losową wagą i wartością z zakresu **1–10** (na podstawie podanego `seed`). Każdy przedmiot ma unikalny identyfikator.

### 3. Algorytm zachłanny

Metoda `Problem.Solve(capacity)`:

1. Sortuje przedmioty malejąco według stosunku **wartość / waga** (najlepszy stosunek na początku).
2. Iteruje po posortowanej liście i bierze tyle kopii danego przedmiotu, ile zmieści się w pozostałej pojemności.
3. Aktualizuje łączną wagę, wartość i listę wybranych przedmiotów.

Jest to heurystyka zachłanna — daje szybkie rozwiązanie, ale nie gwarantuje optimum globalnego dla każdego przypadku.

### 4. Wyjście

Program wypisuje:

- listę wszystkich wygenerowanych przedmiotów (`No: id v: wartość w: waga`),
- separator `-------`,
- listę przedmiotów wybranych do plecaka,
- łączną wagę i wartość.

### Przykład

```
Give number of items:
5
Give seed:
42
Give knapsack capacity:
20
No: 0 v: 7 w: 3
No: 1 v: 5 w: 8
...
-------
No: 0 v: 7 w: 3
No: 0 v: 7 w: 3
...
Weight: 18
Value: 42
```

## Uruchomienie

### Kompilacja i uruchomienie

```bash
mvn compile exec:java -Dexec.mainClass="org.knapsack.Main"
```

### Testy

```bash
mvn test
```

Testy sprawdzają m.in.:

- czy przy wystarczającej pojemności wynik zawiera co najmniej jeden przedmiot,
- czy przy zbyt małej pojemności wynik jest pusty,
- czy wagi i wartości mieszczą się w zadanym zakresie,
- czy suma wag nie przekracza pojemności plecaka.

## Technologie

- Java 21
- Maven
- JUnit 5 (testy)
