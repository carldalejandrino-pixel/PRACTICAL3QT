package PETA3;

import java.util.Scanner;

public class ConsoleLauncher {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        SimulatedData data = new SimulatedData();

        new MainMenu().run(scanner, data);

        scanner.close();
    }
}