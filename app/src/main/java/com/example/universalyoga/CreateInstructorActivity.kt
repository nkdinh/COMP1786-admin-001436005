// Creating a new Instructor
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

class CreateInstructorActivity : AppCompatActivity() {
    private lateinit var edtInstructorName: EditText
    private lateinit var edtInstructorEmail: EditText
    private lateinit var edtCommentInstructor: EditText
    private lateinit var btnSaveInstructor: Button
    private lateinit var btnBack: ImageButton
    private lateinit var database: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_instructor)

        edtInstructorName = findViewById(R.id.edtInstructorName)
        edtInstructorEmail = findViewById(R.id.edtInstructorEmail)
        edtCommentInstructor = findViewById(R.id.edtCommentInstructor)
        btnSaveInstructor = findViewById(R.id.btnSaveInstructor)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)

        btnSaveInstructor.setOnClickListener {
            addInstructor()
        }

        btnBack.setOnClickListener {
            navigateToMain(InstructorManagementActivity::class.java)
        }
    }


    private fun addInstructor() {
        val name = edtInstructorName.text.toString().trim()
        val email = edtInstructorEmail.text.toString().trim()
        val comment = edtCommentInstructor.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(
                this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val result = database.insertInstructor(name, email, comment)

        if (result != -1L) {
            Toast.makeText(
                this, "Instructor added successfully!", Toast.LENGTH_SHORT
            ).show()
            uploadInstructorToFirebase(result, name, email, comment)
        } else {
            Toast.makeText(
                this, "Failed to add the instructor. Please try again.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadInstructorToFirebase(
        instructorId: Long,
        name: String,
        email: String,
        comment: String
    ) {
        val firebaseDb = FirebaseDatabase.getInstance()
        val instructorRef = firebaseDb.getReference("Instructors")

        val instructorData = mapOf(
            "name" to name, "email" to email, "comment" to comment
        )

        instructorRef.child(instructorId.toString()).setValue(instructorData)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Instructor's data saved to Firebase!", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this, "Error saving to Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToMain(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }
}