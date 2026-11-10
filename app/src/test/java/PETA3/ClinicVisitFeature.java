package PETA3;

import java.util.Scanner;

public class ClinicVisitFeature {

    public void runFeature(Scanner scanner, SimulatedData data) {

        System.out.println("--- Clinic Visit ---");

        System.out.print("Student UID: ");

        int uid = Integer.parseInt(scanner.nextLine());

        Student student = data.findByUID(uid);

        if (student == null) {

            System.out.println("Student not found.");

        } else {

            System.out.println("Student: "
                    + student.firstName + " "
                    + student.lastName);

            System.out.print("Reason for visit: ");

            String reason = scanner.nextLine();

            System.out.print("Date of visit: ");

            String date = scanner.nextLine();

            student.clinicVisits.add(date + " - " + reason);

            System.out.println("Clinic visit successfully recorded.");
        }
    }
}