package PETA3;

import java.util.Scanner;

public class StudentIdentificationFeature {

    public void runFeature(Scanner scanner, SimulatedData data) {

        System.out.println("--- Student Identification ---");
        System.out.println("1. Search by UID");
        System.out.println("2. Search by Name");
        System.out.print("Choose an option: ");

        int choice = Integer.parseInt(scanner.nextLine());
        System.out.println(choice);

        if (choice == 1) {

            System.out.print("Enter Student UID: ");

            int uid = Integer.parseInt(scanner.nextLine());

            Student student = data.findByUID(uid);

            if (student == null) {

                System.out.println("Student not found.");

            } else {

                System.out.println("Student found!");
                System.out.println(student);
            }

        } else if (choice == 2) {

            System.out.print("First name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            Student student =
                    data.findByName(firstName, lastName);

            if (student == null) {

                System.out.println("Student not found.");

            } else {

                System.out.println("Student found!");
                System.out.println(student);
            }

        } else {

            System.out.println("Invalid choice.");
        }
    }
}