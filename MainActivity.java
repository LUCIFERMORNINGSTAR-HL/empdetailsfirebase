package com.example.stdlogin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etName, etAge, etDept;
    Button btnAdd, btnUpdate;
    ListView listViewStudents;

    DatabaseReference databaseStudents;
    List<Student> studentList;
    String selectedStudentId = ""; // To hold ID for update/delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Reference
        databaseStudents = FirebaseDatabase.getInstance().getReference("students");

        // UI Initialization
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etDept = findViewById(R.id.etDept);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        listViewStudents = findViewById(R.id.listViewStudents);

        studentList = new ArrayList<>();

        // Add Student
        btnAdd.setOnClickListener(v -> addStudent());

        // Update Student
        btnUpdate.setOnClickListener(v -> updateStudent());

        // List Item Click (Populate fields for Edit/Delete)
        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
            Student student = studentList.get(i);
            etName.setText(student.getName());
            etAge.setText(student.getAge());
            etDept.setText(student.getDepartment());
            selectedStudentId = student.getStudentId();
        });

        // Long Click to Delete
        listViewStudents.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Student student = studentList.get(i);
            deleteStudent(student.getStudentId());
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // READ: Listen for database changes
        databaseStudents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Student student = postSnapshot.getValue(Student.class);
                    studentList.add(student);
                }
                // Display using simple adapter
                ArrayAdapter<Student> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, studentList);
                listViewStudents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // CREATE
    private void addStudent() {
        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        String dept = etDept.getText().toString();

        if (!name.isEmpty()) {
            String id = databaseStudents.push().getKey(); // Generate Unique ID
            Student student = new Student(id, name, age, dept);
            databaseStudents.child(id).setValue(student);
            Toast.makeText(this, "Student Added", Toast.LENGTH_SHORT).show();
            clearFields();
        }
    }

    // UPDATE
    private void updateStudent() {
        if (selectedStudentId.isEmpty()) return;

        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        String dept = etDept.getText().toString();

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("students").child(selectedStudentId);
        Student student = new Student(selectedStudentId, name, age, dept);
        dR.setValue(student);
        Toast.makeText(this, "Student Updated", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    // DELETE
    private void deleteStudent(String id) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("students").child(id);
        dR.removeValue();
        Toast.makeText(this, "Student Deleted", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        etName.setText("");
        etAge.setText("");
        etDept.setText("");
        selectedStudentId = "";
    }
}