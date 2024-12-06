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

class InstructorEditActivity : AppCompatActivity() {
    private lateinit var edtNameInstructor: EditText
    private lateinit var edtEmailInstructor: EditText
    private lateinit var edtCommentInstructor: EditText
    private lateinit var btnSaveEdit: Button
    private lateinit var btnBack: ImageButton
    private lateinit var database: DBHelper
    private var InstructorId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_instructor)

        edtNameInstructor = findViewById(R.id.edtNameInstructor)
        edtEmailInstructor = findViewById(R.id.edtEmailInstructor)
        edtCommentInstructor = findViewById(R.id.edtCommentInstructor)
        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)

        InstructorId = intent.getLongExtra("Instructor_ID", -1L)

        if (InstructorId != -1L) {
            loadInstructorInfo(InstructorId)
        }

        btnSaveEdit.setOnClickListener {
            applyChanges()
        }
        btnBack.setOnClickListener {
            navigateToPreviousScreen(InstructorDetailActivity::class.java)
        }
    }
    private fun navigateToPreviousScreen(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun loadInstructorInfo(id: Long) {
        val Instructor = database.getInstructorById(id)
        if (Instructor != null) {
            edtNameInstructor.setText(Instructor["name"])
            edtEmailInstructor.setText(Instructor["email"])
            edtCommentInstructor.setText(Instructor["comment"])
        } else {
            Toast.makeText(
                this, "Error loading Instructor data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun applyChanges() {
        val name = edtNameInstructor.text.toString().trim()
        val email = edtEmailInstructor.text.toString().trim()
        val comment = edtCommentInstructor.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(
                this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val isUpdated = database.updateInstructor(InstructorId, name, email, comment)

        if (isUpdated) {
            updateInstructorInFirebase(name, email, comment)
            setResult(RESULT_OK)
            Toast.makeText(
                this, "Instructor details updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(
                this, "Failed to update Instructor details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateInstructorInFirebase(name: String, email: String, comment: String) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val InstructorRef = firebaseDatabase.getReference("Instructors").child(InstructorId.toString())

        val updatedData = mapOf(
            "name" to name,
            "email" to email,
            "comment" to comment
        )

        InstructorRef.updateChildren(updatedData)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Instructor data updated in Firebase.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this, "Error updating Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}