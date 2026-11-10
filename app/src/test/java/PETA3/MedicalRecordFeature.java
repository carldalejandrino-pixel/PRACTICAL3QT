package PETA3;

import java.util.Scanner;

public class MedicalRecordFeature {

    public void runFeature(Scanner scanner, SimulatedData data) {

        System.out.println("--- Medical Record Management ---");

        System.out.print("Student UID: ");

        int uid = Integer.parseInt(scanner.nextLine());

        Student student = data.findByUID(uid);

        if (student == null) {

            System.out.println("Student not found.");

        } else {

            System.out.println("Student: "
                    + student.firstName + " "
                    + student.lastName);

            System.out.println("1. Add Medical History");
            System.out.println("2. Add Allergy");
            System.out.println("3. Add Medication");
            System.out.println("4. View Medical Information");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());
            System.out.println(choice);

            if (choice == 1) {

                System.out.print("Medical history: ");

                String history = scanner.nextLine();

                student.medicalHistory.add(history);

                System.out.println("Medical history saved.");

            } else if (choice == 2) {

                System.out.print("Allergy: ");

                String allergy = scanner.nextLine();

                student.allergies.add(allergy);

                System.out.println("Allergy saved.");

            } else if (choice == 3) {

                System.out.print("Medication: ");

                String medication = scanner.nextLine();

                student.medications.add(medication);

                System.out.println("Medication saved.");

            } else if (choice == 4) {

                System.out.println("Medical History:");

                for (String history : student.medicalHistory) {

                    System.out.println("- " + history);
                }

                System.out.println("Allergies:");

                for (String allergy : student.allergies) {

                    System.out.println("- " + allergy);
                }

                System.out.println("Medications:");

                for (String medication : student.medications) {

                    System.out.println("- " + medication);
                }

            } else {

                System.out.println("Invalid choice.");
            }
        }
    }
}