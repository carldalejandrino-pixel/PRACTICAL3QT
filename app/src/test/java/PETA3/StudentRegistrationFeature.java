package PETA3;

import java.util.Scanner;

public class StudentRegistrationFeature {

    public void runFeature(Scanner scanner, SimulatedData data) {

        System.out.println("--- Student Registration ---");

        System.out.print("Student UID: ");
        int uid = Integer.parseInt(scanner.nextLine());
        System.out.println(uid);

        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.println(firstName);

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.println(lastName);

        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.println(age);

        System.out.print("Grade level: ");
        int gradeLevel = Integer.parseInt(scanner.nextLine());
        System.out.println(gradeLevel);

        System.out.print("Section: ");
        String section = scanner.nextLine();
        System.out.println(section);

        System.out.print("Emergency contact: ");
        String emergencyContact = scanner.nextLine();
        System.out.println(emergencyContact);

        data.add(
                uid,
                firstName,
                lastName,
                age,
                gradeLevel,
                section,
                emergencyContact
        );

        System.out.println("Student successfully registered!");
        System.out.println("Student UID: " + uid);
    }
}