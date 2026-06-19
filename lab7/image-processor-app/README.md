# PWr Image Processor — Przetwarzanie obrazów

Aplikacja okienkowa w JavaFX do wczytywania, edycji i zapisywania obrazów JPG. Operacje filtrów (negatyw, progowanie, konturowanie) wykonywane są **równolegle** na czterech wątkach.

## Cel programu

Program umożliwia:

- wczytanie obrazu JPG,
- obrót i skalowanie,
- zastosowanie filtrów obrazu,
- podgląd oryginału i wyniku obok siebie,
- zapis przetworzonego pliku do katalogu `~/Pictures`,
- rejestrowanie zdarzeń w pliku `logs.txt`.

## Struktura projektu

```
src/main/java/pl/pwr/imageapp/
├── gui/
│   └── MainApp.java                    — interfejs użytkownika (JavaFX)
├── processor/
│   └── ParallelImageProcessor.java     — operacje na obrazie (w tym wielowątkowe)
└── logger/
    └── AppLogger.java                  — zapis logów do pliku
```

## Jak działa program

### Interfejs użytkownika (`MainApp`)

Aplikacja dzieli ekran na:

- **panel sterowania** (lewa strona) — przyciski i lista operacji,
- **podgląd oryginału** — obraz źródłowy,
- **podgląd zmian** — obraz po edycji.

Przepływ pracy:

1. **Wczytaj obraz** — wybór pliku `.jpg` z dysku. Po załadowaniu aktywne stają się pozostałe kontrolki.
2. **Obracanie** — obrót o 90° w lewo lub w prawo (operacja sekwencyjna).
3. **Skalowanie** — okno modalne z podaniem szerokości i wysokości (0–3000 px).
4. **Przetwarzanie wielowątkowe** — wybór operacji z listy i kliknięcie „Wykonaj operacje”.
5. **Zapisz obraz** — zapis do `~/Pictures` z podaną nazwą (min. 3 znaki).

Obrazy przechowywane są w pamięci jako `BufferedImage`. Oryginał (`originalBI`) pozostaje niezmieniony; wszystkie operacje modyfikują kopię roboczą (`processedBI`).

### Operacje na obrazie (`ParallelImageProcessor`)

| Operacja | Metoda | Wielowątkowość |
|----------|--------|----------------|
| Skalowanie | `scale()` | Nie — używa `Graphics2D` |
| Obrót 90° | `rotate()` | Nie — pętle po pikselach |
| Negatyw | `processNegativeParallel()` | Tak — 4 wątki |
| Progowanie | `processThresholdParallel()` | Tak — 4 wątki |
| Konturowanie | `processContourParallel()` | Tak — 4 wątki |

#### Negatyw

Dla każdego piksela odwraca składowe RGB: `nowa = 255 - stara`.

#### Progowanie

1. Konwertuje piksel na odcienie szarości (luminancja ITU-R BT.601):

   `gray = 0.299·R + 0.587·G + 0.114·B`

2. Jeśli `gray > próg` → biały (255), w przeciwnym razie → czarny (0).

Próg (0–255) podaje użytkownik w oknie modalnym.

#### Konturowanie

Wykrywa krawędzie na podstawie różnic jasności sąsiednich pikseli (w poziomie i pionie). Piksele na krawędzi obrazu ustawiane są na czarno.

### Przetwarzanie równoległe

Operacje filtrów dzielą obraz na **4 poziome pasy** (wiersze). Każdy pas przetwarza osobny wątek z puli `Executors.newFixedThreadPool(4)`. Główny wątek czeka na zakończenie (`awaitTermination`) przed zwróceniem wyniku.

```
┌─────────────────┐
│   wątek 0       │  wiersze 0 … h/4
├─────────────────┤
│   wątek 1       │  wiersze h/4 … h/2
├─────────────────┤
│   wątek 2       │  wiersze h/2 … 3h/4
├─────────────────┤
│   wątek 3       │  wiersze 3h/4 … h
└─────────────────┘
```

### Logowanie (`AppLogger`)

Zdarzenia zapisywane są do pliku `logs.txt` w katalogu roboczym aplikacji:

```
[2026-06-19 10:30:00] [INFO] Uruchomiono aplikacje okienkowa.
[2026-06-19 10:30:15] [INFO] Pomyslnie zaladowano plik obrazu: foto.jpg
[2026-06-19 10:30:22] [INFO] Pomyslnie wykonano operacje Negatyw.
```

Poziomy: `INFO`, `WARN`, `ERROR`.

## Uruchomienie

```bash
mvn javafx:run
```

Alternatywnie po kompilacji:

```bash
mvn compile
mvn javafx:run
```

## Wymagania

- Java 21
- Maven
- JavaFX 21 (pobierane automatycznie przez Maven)

## Ograniczenia

- Obsługiwany format wejściowy: **JPG** (`.jpg`, `.JPG`)
- Zapis wyłącznie jako **JPG** do `~/Pictures`
- Maksymalne wymiary skalowania: 3000×3000 px
- Nazwa pliku przy zapisie: min. 3 znaki, max. 100 znaków

## Technologie

- Java 21
- JavaFX 21 (GUI)
- `java.awt.image.BufferedImage` (przetwarzanie pikseli)
- `java.util.concurrent` (pula wątków)
