package com.example.stdlogin;

public class Student {
    String studentId;
    String name;
    String age;
    String department;

    // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    public Student() {
    }

    public Student(String studentId, String name, String age, String department) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.department = department;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getAge() { return age; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return name + " (" + department + ") - Age: " + age;
    }
}