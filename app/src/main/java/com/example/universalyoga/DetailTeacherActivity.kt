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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.universalyoga.DBHelper
import com.example.universalyoga.R
import com.google.firebase.database.FirebaseDatabase

class DetailTeacherActivity : AppCompatActivity() {
    private lateinit var tvNameTeacher: TextView
    private lateinit var tvEmailTeacher: TextView
    private lateinit var tvComment: TextView
    private lateinit var btnEditTeacher: Button
    private lateinit var btnDeleteTeacher: Button
    private lateinit var btnBack: ImageButton
    private lateinit var database: DBHelper
    private var teacherId: Long = -1L

    companion object {
        private const val REQUEST_CODE_EDIT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_detail_teacher)

        tvNameTeacher = findViewById(R.id.tvNameTeacher)
        tvEmailTeacher = findViewById(R.id.tvEmailTeacher)
        tvComment = findViewById(R.id.tvComment)
        btnEditTeacher = findViewById(R.id.btnEditTeacher)
        btnDeleteTeacher = findViewById(R.id.btnDeleteTeacher)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)
        teacherId = intent.getLongExtra("TEACHER_ID", -1L)

        if (teacherId != -1L) {
            displayTeacherDetails(teacherId)
        } else {
            Toast.makeText(
                this, "Error loading teacher data", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnEditTeacher.setOnClickListener {
            navigateToEditTeacher()
        }

        btnDeleteTeacher.setOnClickListener {
            promptDeleteTeacher()
        }

        btnBack.setOnClickListener {
            navigateBack(ListTeacherActivity::class.java)
        }

    }
    private fun navigateBack(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun displayTeacherDetails(id: Long) {
        val teacher = database.getTeacherById(id)
        if (teacher != null) {
            tvNameTeacher.text = teacher["name"]
            tvEmailTeacher.text = teacher["email"]
            tvComment.text = teacher["comment"]
        } else {
            Toast.makeText(this, "Teacher not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun navigateToEditTeacher() {
        val intent = Intent(this, EditTeacherActivity::class.java)
        intent.putExtra("TEACHER_ID", teacherId)
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    private fun promptDeleteTeacher() {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Sure?")
            .setPositiveButton("Yes") { _, _ ->
                performDeleteTeacher()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performDeleteTeacher() {
        val isDeleted = database.deleteTeacher(teacherId)
        if (isDeleted) {
            removeTeacherFromFirebase()
            Toast.makeText(
                this, "Teacher deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(
                this, "Failed to delete teacher", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeTeacherFromFirebase() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val teacherRef = firebaseDatabase.getReference("teachers").child(teacherId.toString())

        teacherRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Teacher data removed from Firebase.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this, "Error removing from Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            displayTeacherDetails(teacherId)
        }
    }
}