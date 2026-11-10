package PETA3;

import java.util.ArrayList;
import java.util.Scanner;

public class SimulatedData {

    private ArrayList<Student> students = new ArrayList<>();

    public SimulatedData() {

        add(
                20260001,
                "Juan",
                "Dela Cruz",
                16,
                11,
                "ICT-A",
                "0917-000-0001"
        );

        add(
                20260002,
                "Maria",
                "Santos",
                17,
                11,
                "ICT-B",
                "0917-000-0002"
        );
    }

    public void add(int uid, String firstName, String lastName,
                    int age, int gradeLevel,
                    String section, String emergencyContact) {

        students.add(
                new Student(
                        uid,
                        firstName,
                        lastName,
                        age,
                        gradeLevel,
                        section,
                        emergencyContact
                )
        );
    }

    public ArrayList<Student> getAll() {

        return students;
    }

    public Student findByUID(int uid) {

        for (Student student : students) {

            if (student.studentUID == uid) {

                return student;
            }
        }

        return null;
    }

    public Student findByName(String firstName, String lastName) {

        for (Student student : students) {

            if (student.firstName.equalsIgnoreCase(firstName)
                    && student.lastName.equalsIgnoreCase(lastName)) {

                return student;
            }
        }

        return null;
    }

    public void viewStudentRecord(Scanner scanner) {

        System.out.println("--- View Student Record ---");

        System.out.print("Student UID: ");

        int uid = Integer.parseInt(scanner.nextLine());

        Student student = findByUID(uid);

        if (student == null) {

            System.out.println("No student found.");

        } else {

            System.out.println();
            System.out.println("Student Information");
            System.out.println(student);

            System.out.println("Emergency Contact: "
                    + student.emergencyContact);

            System.out.println();
            System.out.println("Medical History:");

            if (student.medicalHistory.size() == 0) {

                System.out.println("None");

            } else {

                for (String history : student.medicalHistory) {

                    System.out.println("- " + history);
                }
            }

            System.out.println();
            System.out.println("Allergies:");

            if (student.allergies.size() == 0) {

                System.out.println("None");

            } else {

                for (String allergy : student.allergies) {

                    System.out.println("- " + allergy);
                }
            }

            System.out.println();
            System.out.println("Medications:");

            if (student.medications.size() == 0) {

                System.out.println("None");

            } else {

                for (String medication : student.medications) {

                    System.out.println("- " + medication);
                }
            }

            System.out.println();
            System.out.println("Clinic Visits:");

            if (student.clinicVisits.size() == 0) {

                System.out.println("None");

            } else {

                for (String visit : student.clinicVisits) {

                    System.out.println("- " + visit);
                }
            }
        }
    }
}