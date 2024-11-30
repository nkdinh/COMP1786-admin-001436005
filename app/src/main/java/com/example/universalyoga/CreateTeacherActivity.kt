// Creating a new Teacher
package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class CreateTeacherActivity : AppCompatActivity() {
    private lateinit var edtTeacherName: EditText
    private lateinit var edtTeacherEmail: EditText
    private lateinit var edtCommentTeacher: EditText
    private lateinit var btnSaveTeacher: Button
    private lateinit var btnBack: ImageButton
    private lateinit var database: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_teacher)

        edtTeacherName = findViewById(R.id.edtTeacherName)
        edtTeacherEmail = findViewById(R.id.edtTeacherEmail)
        edtCommentTeacher = findViewById(R.id.edtCommentTeacher)
        btnSaveTeacher = findViewById(R.id.btnSaveTeacher)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)

        btnSaveTeacher.setOnClickListener {
            addTeacher()
        }

        btnBack.setOnClickListener {
            navigateToMain(ManageTeacherActivity::class.java)
        }
    }

    private fun navigateToMain(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun addTeacher() {
        val name = edtTeacherName.text.toString().trim()
        val email = edtTeacherEmail.text.toString().trim()
        val comment = edtCommentTeacher.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(
                this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val result = database.insertTeacher(name, email, comment)

        if (result != -1L) {
            Toast.makeText(
                this, "Teacher added successfully!", Toast.LENGTH_SHORT).show()
            uploadTeacherToFirebase(result, name, email, comment)
        } else {
            Toast.makeText(
                this, "Failed to add teacher. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadTeacherToFirebase(teacherId: Long, name: String, email: String, comment: String) {
        val firebaseDb = FirebaseDatabase.getInstance()
        val teacherRef = firebaseDb.getReference("teachers")

        val teacherData = mapOf(
            "name" to name, "email" to email, "comment" to comment
        )

        teacherRef.child(teacherId.toString()).setValue(teacherData)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Teacher data saved to Firebase!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this, "Error saving to Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

}