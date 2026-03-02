# Odkształcenia Sprężyste - Metoda Elementów Skończonych

**Autor:** Maria Szarata  
**Temat:** 4.3 Odkształcenia sprężyste

---

## 1. Opis projektu
Projekt realizuje rozwiązanie równania różniczkowego opisującego odkształcenia sprężyste za pomocą **Metody Elementów Skończonych (MES)**. Program oblicza przybliżone rozwiązanie $u(x)$ na przedziale $[0, 2]$.

### Model Matematyczny
Rozwiązywane jest równanie różniczkowe:
$$-\frac{d}{dx} \left( E(x) \frac{du(x)}{dx} \right) = 0$$

**Warunki brzegowe:**
* $u(2) = 0$
* $\frac{du(0)}{dx} + u(0) = 10$

**Charakterystyka materiałowa $E(x)$:**

* $E(x) = 3$ dla $x \in [0, 1]$
* $E(x) = 5$ dla $x \in (1, 2]$



---

## 2. Zawartość repozytorium
* `szarata_kod_java` - Kod programu w języku Java.
* `obliczenia.jpg` - Ręczne wyprowadzenie sformułowania wariacyjnego.
* `wykres_n=40.jpg` - Przykładowa wizualizacja rozwiązania dla $n=40$.

---

## 3. Wymagania
Aby uruchomić aplikację, wymagane są:
* **Java (JDK)** - kompilator i środowisko uruchomieniowe.
* **gnuplot** - narzędzie służące do generowania wykresów wyników.

---

## 4. Instrukcja uruchomienia

1. **Kompilacja:** Przejdź do katalogu z kodem i skompiluj pliki:
   ```bash
   javac *.java
2. **Uruchomienie:** Program przyjmuje liczbę elementów skończonych `n` jako parametr wejściowy:
   ```bash
   java Main 40
(Gdzie `40` to przykładowa wartość parametru `n`).

## 5. Przebieg rozwiązania
Proces wyznaczania rozwiązania $u(x)$ składa się z następujących kroków:

* **Wyprowadzenie sformułowania wariacyjnego:** Przekształcenie równania różniczkowego do postaci całkowej (słabej), co zostało udokumentowane w pliku `obliczenia.jpg`.
* **Dyskretyzacja:** Podział przedziału $[0, 2]$ na $n$ elementów skończonych.
* **Budowa układu równań:** Wygenerowanie macierzy sztywności oraz wektora obciążeń z uwzględnieniem skokowej zmiany parametru $E(x)$.
* **Rozwiązanie układu:** Wyznaczenie wartości funkcji w węzłach.
* **Wizualizacja:** Automatyczne wygenerowanie wykresu przy użyciu narzędzia `gnuplot`.

## 6. Przykładowy wynik
Poniżej znajduje się wizualizacja odkształcenia dla parametru $n = 40$:
