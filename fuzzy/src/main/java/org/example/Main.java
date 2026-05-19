package org.example;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import java.io.InputStream;

public class Main {
    static void main() {
        IO.println(String.format("Hello and welcome!"));

        // 1. Wskazanie ścieżki do pliku FCL
        // Ładowanie pliku z zasobów aplikacji (ClassPath)
        // Znak "/" na początku oznacza szukanie od głównego katalogu resources
        InputStream in = Main.class.getResourceAsStream("/tipper.fcl");

        if (in == null) {
            System.err.println("BŁĄD: Nie znaleziono pliku tipper.fcl w zasobach projektu (ClassPath)!");
            System.err.println("Upewnij się, że plik znajduje się w katalogu src/main/resources/");
            return;
        }

        // Tworzenie obiektu FIS ze strumienia danych
        FIS fis = FIS.load(in, true);

        // Zabezpieczenie na wypadek błędu w pliku lub złej ścieżki
        if (fis == null) {
            System.err.println("Nie można załadować pliku");
            return;
        }

        // 2. Pobranie domyślnego bloku funkcyjnego
        FunctionBlock functionBlock = fis.getFunctionBlock(null);

        // 3. Ustawienie zmiennych wejściowych
        functionBlock.setVariable("service", 3.0);
        functionBlock.setVariable("food", 7.0);

        // 4. Uruchomienie wnioskowania rozmytego
        functionBlock.evaluate();

        // 5. Pobranie i wyświetlenie wyniku
        double tip = functionBlock.getVariable("tip").getValue();
        System.out.println("=========================================");
        System.out.printf("Sugerowana kwota napiwku: %.2f%%\n", tip);
        System.out.println("=========================================");
    }
}
