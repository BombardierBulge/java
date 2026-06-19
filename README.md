# Projekty Java — Platformy Programistyczne

Repozytorium zawiera trzy projekty laboratoryjne z przedmiotu Platformy Programistyczne:

| Lab | Projekt | Opis |
|-----|---------|------|
| lab6 | [knapsack-project](lab6/knapsack-project/) | Rozwiązanie problemu plecakowego algorytmem zachłannym |
| lab7 | [image-processor-app](lab7/image-processor-app/) | Aplikacja okienkowa do przetwarzania obrazów (JavaFX, wielowątkowość) |
| lab8 | [lab8](lab8/) | Aplikacja webowa wypożyczalni samochodów (JHipster, Spring Boot + Angular) |

## Wymagania

- Java 21
- Maven 3.x
- Dla lab7: JavaFX 21 (zarządzane przez Maven)
- Dla lab8: Node.js, Docker (opcjonalnie — baza MySQL)

## Szybki start

### Lab 6 — Problem plecakowy

```bash
cd lab6/knapsack-project
mvn compile exec:java -Dexec.mainClass="org.knapsack.Main"
```

### Lab 7 — Przetwarzanie obrazów

```bash
cd lab7/image-processor-app
mvn javafx:run
```

### Lab 8 — Wypożyczalnia samochodów

Szczegóły uruchomienia w [lab8/README.md](lab8/README.md).

```bash
cd lab8
./npmw install
./npmw run backend:start   # terminal 1
./npmw run start           # terminal 2
```

Aplikacja dostępna pod adresem: http://localhost:8080
