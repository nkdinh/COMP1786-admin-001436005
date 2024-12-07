package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class InstructorDetailActivity : AppCompatActivity() {
    private lateinit var tvNameInstructor: TextView
    private lateinit var tvEmailInstructor: TextView
    private lateinit var tvComment: TextView
    private lateinit var btnEditInstructor: Button
    private lateinit var btnDeleteInstructor: Button
    private lateinit var btnBack: ImageButton
    private lateinit var database: DBHelper
    private var instructorId: Long = -1L

    companion object {
        private const val REQUEST_CODE_EDIT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_detail_instructor)

        tvNameInstructor = findViewById(R.id.tvNameInstructor)
        tvEmailInstructor = findViewById(R.id.tvEmailInstructor)
        tvComment = findViewById(R.id.tvComment)
        btnEditInstructor = findViewById(R.id.btnEditInstructor)
        btnDeleteInstructor = findViewById(R.id.btnDeleteInstructor)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)
        instructorId = intent.getLongExtra("Instructor_ID", -1L)

        if (instructorId != -1L) {
            displayInstructorDetails(instructorId)
        } else {
            Toast.makeText(
                this, "Error loading instructor data", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnEditInstructor.setOnClickListener {
            navigateToEditInstructor()
        }

        btnDeleteInstructor.setOnClickListener {
            promptDeleteInstructor()
        }

        btnBack.setOnClickListener {
            navigateBack(InstructorListActivity::class.java)
        }

    }
    private fun navigateBack(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun displayInstructorDetails(id: Long) {
        val instructor = database.getInstructorById(id)
        if (instructor != null) {
            tvNameInstructor.text = instructor["name"]
            tvEmailInstructor.text = instructor["email"]
            tvComment.text = instructor["comment"]
        } else {
            Toast.makeText(this, "Instructor not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun navigateToEditInstructor() {
        val intent = Intent(this, InstructorEditActivity::class.java)
        intent.putExtra("Instructor_ID", instructorId)
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    // pop-up to delete Instructor
    private fun promptDeleteInstructor() {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Sure?")
            .setPositiveButton("Yes") { _, _ ->
                deleteInstructor()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteInstructor() {
        val isDeleted = database.deleteInstructor(instructorId)
        if (isDeleted) {
            removeInstructorFromFirebase()
            Toast.makeText(
                this, "Instructor deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(
                this, "Failed to delete Instructor", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeInstructorFromFirebase() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val instructorRef = firebaseDatabase.getReference("Instructors").child(instructorId.toString())

        instructorRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Instructor data removed from Firebase.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this, "Error removing from Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            displayInstructorDetails(instructorId)
        }
    }
}