package PETA3;

import java.util.ArrayList;

public class Student {

    int studentUID;
    String firstName;
    String lastName;
    int age;
    int gradeLevel;
    String section;
    String emergencyContact;

    ArrayList<String> medicalHistory;
    ArrayList<String> allergies;
    ArrayList<String> medications;
    ArrayList<String> clinicVisits;

    public Student(int studentUID, String firstName, String lastName,
                   int age, int gradeLevel, String section,
                   String emergencyContact) {

        this.studentUID = studentUID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gradeLevel = gradeLevel;
        this.section = section;
        this.emergencyContact = emergencyContact;

        medicalHistory = new ArrayList<>();
        allergies = new ArrayList<>();
        medications = new ArrayList<>();
        clinicVisits = new ArrayList<>();
    }

    public String toString() {

        return "UID: " + studentUID
                + " | Name: " + firstName + " " + lastName
                + " | Age: " + age
                + " | Grade: " + gradeLevel
                + " | Section: " + section;
    }
}