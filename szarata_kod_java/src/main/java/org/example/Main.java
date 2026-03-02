package org.example;

import java.io.PrintWriter;
import java.util.Locale;

public class Main {

    // Funkcja opisująca moduł Younga E(x)
    // E(x) = 3 dla x ∈ [0,1], E(x) = 5 dla x ∈ (1,2]
    static double E(double x) {
        return (x <= 1.0) ? 3.0 : 5.0;
    }

    // Węzły i wagi 2-punktowej kwadratury Gaussa-Legendre’a
    // (zdefiniowane na przedziale referencyjnym [-1, 1])
    static final double[] GP = { -1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0) };
    static final double[] GW = { 1.0, 1.0 };

    public static void main(String[] args) {
        // Ustawienie kropki jako separatora dziesiętnego
        Locale.setDefault(Locale.US);

        // Liczba elementów (domyślnie 20, można podać z linii poleceń)
        int n = 20;
        if (args.length > 0) {
            try { n = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        if (n < 1) n = 1;

        // Przedział obliczeń [a, b]
        double a = 0.0, b = 2.0;

        // Długość pojedynczego elementu
        int nn = n + 1;
        double h = (b - a) / n;

        // Współrzędne węzłów
        double[] x = new double[nn];
        for (int i = 0; i < nn; i++){
            x[i] = a + i * h;
        }

        // Globalna macierz sztywności i wektor prawej strony
        double[][] K = new double[nn][nn];
        double[] F = new double[nn];

        // MONTAŻ GLOBALNEJ MACIERZY SZTYWNOŚCI (METODA FEM)
        for (int e = 0; e < n; e++) {
            // Indeksy globalnych węzłów elementu
            int i = e, j = e + 1;

            // Granice elementu
            double xe = x[i], xe1 = x[j];
            double he = xe1 - xe;

            // Lokalne współczynniki macierzy elementu
            double k00 = 0, k01 = 0, k10 = 0, k11 = 0;

            // Jeżeli element przecina punkt x = 1 (nieciągłość E),
            // dzielimy całkę na dwa podprzedziały
            if (xe < 1.0 && xe1 > 1.0) {
                // podział elementu na [xe,1] i [1,xe1]
                k00 += integratePart(xe, 1.0, he);
                k00 += integratePart(1.0, xe1, he);
            } else {
                k00 += integratePart(xe, xe1, he);
            }

            // Macierz elementu dla funkcji liniowych
            k01 = -k00;
            k10 = -k00;
            k11 =  k00;

            // Składanie do macierzy globalnej
            K[i][i] += k00; K[i][j] += k01;
            K[j][i] += k10; K[j][j] += k11;
        }

        // WARUNEK BRZEGOWY ROBINA: u'(0) + u(0) = 10
        // Po uwzględnieniu w słabej postaci:
        // +3*u(0)v(0) po lewej stronie oraz +30*v(0) po prawej
        K[0][0] += 3.0;
        F[0]    += 30.0;

        // WARUNEK DIRICHLETA: u(2) = 0
        // Realizowany przez modyfikację ostatniego wiersza macierzy
        int last = nn - 1;
        double uRight = 0.0;

        for (int r = 0; r < nn; r++) {
            F[r] -= K[r][last] * uRight;
            K[r][last] = 0.0;
        }
        for (int c = 0; c < nn; c++) K[last][c] = 0.0;
        K[last][last] = 1.0;
        F[last] = uRight;

        // Rozwiązanie układu równań liniowych
        double[] u = solveGaussian(K, F);

        // Wypisanie rozwiązania na standardowe wyjście
        System.out.println("x,u");
        for (int i = 0; i < nn; i++)
            System.out.printf("%.10f,%.10f%n", x[i], u[i]);

        // Zapis rozwiązania do pliku CSV
        try (PrintWriter pw = new PrintWriter("solution.csv")) {
            pw.println("x,u");
            for (int i = 0; i < nn; i++)
                pw.printf("%.10f,%.10f%n", x[i], u[i]);
        } catch (Exception ignored) {}

        // Wyświetlenie wykresu w gnuplot
        plotWithGnuplot();
    }

    // Całkowanie ∫ E(x) * (dφ/dx)^2 dx metodą Gaussa-Legendre’a
    // na zadanym podprzedziale elementu
    static double integratePart(double xl, double xr, double he) {
        double J = (xr - xl) / 2.0;         // Jakobian transformacji
        double dphidx = 1.0 / he;           // Pochodna funkcji bazowej
        double sum = 0.0;

        for (int q = 0; q < 2; q++) {
            double xq = 0.5 * (xl + xr) + J * GP[q];
            sum += GW[q] * E(xq) * J * dphidx * dphidx;
        }
        return sum;
    }

    // Rozwiązywanie układu równań liniowych metodą eliminacji Gaussa
    // z częściowym wyborem elementu głównego
    static double[] solveGaussian(double[][] A, double[] b) {
        int n = b.length;
        for (int k = 0; k < n; k++) {
            int piv = k;
            for (int i = k + 1; i < n; i++)
                if (Math.abs(A[i][k]) > Math.abs(A[piv][k])) piv = i;

            // Zamiana wierszy
            double[] tmp = A[k]; A[k] = A[piv]; A[piv] = tmp;
            double t = b[k]; b[k] = b[piv]; b[piv] = t;

            // Normalizacja wiersza
            double d = A[k][k];
            for (int j = k; j < n; j++) A[k][j] /= d;
            b[k] /= d;

            // Eliminacja
            for (int i = k + 1; i < n; i++) {
                double f = A[i][k];
                for (int j = k; j < n; j++) A[i][j] -= f * A[k][j];
                b[i] -= f * b[k];
            }
        }

        // Podstawianie wsteczne
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = b[i];
            for (int j = i + 1; j < n; j++) x[i] -= A[i][j] * x[j];
        }
        return x;
    }

    // Uruchomienie gnuplota i wyświetlenie wykresu rozwiązania
    static void plotWithGnuplot() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "gnuplot",
                    "-persist",
                    "-e",
                    "set datafile separator ','; " +
                            "set title 'FEM odksztalcenie sprezyste'; " +
                            "set xlabel 'x'; set ylabel 'u(x)'; " +
                            "set grid; " +
                            "plot 'solution.csv' using 1:2 with linespoints lw 2 title 'u(x)'"
            );
            pb.inheritIO();
            pb.start();
        } catch (Exception e) {
            System.err.println("Nie udalo się uruchomic gnuplot: " + e.getMessage());
        }
    }

}
