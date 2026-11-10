package PETA3;

import java.util.Scanner;

public class MainMenu {

    public void run(Scanner scanner, SimulatedData data) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("========================================");
            System.out.println("       SCHOOL CLINIC MANAGEMENT");
            System.out.println("========================================");
            System.out.println("1. Student Identification");
            System.out.println("2. Student Registration");
            System.out.println("3. Medical Record Management");
            System.out.println("4. Clinic Visit");
            System.out.println("5. View Student Record");
            System.out.println("6. Exit");
            System.out.println("========================================");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            System.out.println(choice);

            if (choice == 1) {

                new StudentIdentificationFeature().runFeature(scanner, data);

            } else if (choice == 2) {

                new StudentRegistrationFeature().runFeature(scanner, data);

            } else if (choice == 3) {

                new MedicalRecordFeature().runFeature(scanner, data);

            } else if (choice == 4) {

                new ClinicVisitFeature().runFeature(scanner, data);

            } else if (choice == 5) {

                data.viewStudentRecord(scanner);

            } else if (choice == 6) {

                System.out.println("Goodbye!");
                running = false;

            } else {

                System.out.println("Invalid choice.");
            }
        }
    }
}