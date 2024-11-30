package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.universalyoga.DBHelper
import com.example.universalyoga.R
import com.google.firebase.database.FirebaseDatabase

class EditTeacherActivity : AppCompatActivity() {
    private lateinit var edtNameTeacher: EditText
    private lateinit var edtEmailTeacher: EditText
    private lateinit var edtCommentTeacher: EditText
    private lateinit var btnSaveEdit: Button
    private lateinit var btnBack: ImageButton
    private lateinit var database: DBHelper
    private var teacherId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_teacher)

        edtNameTeacher = findViewById(R.id.edtNameTeacher)
        edtEmailTeacher = findViewById(R.id.edtEmailTeacher)
        edtCommentTeacher = findViewById(R.id.edtCommentTeacher)
        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)

        teacherId = intent.getLongExtra("TEACHER_ID", -1L)

        if (teacherId != -1L) {
            loadTeacherInfo(teacherId)
        }

        btnSaveEdit.setOnClickListener {
            applyChanges()
        }
        btnBack.setOnClickListener {
            navigateToPreviousScreen(DetailTeacherActivity::class.java)
        }
    }
    private fun navigateToPreviousScreen(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun loadTeacherInfo(id: Long) {
        val teacher = database.getTeacherById(id)
        if (teacher != null) {
            edtNameTeacher.setText(teacher["name"])
            edtEmailTeacher.setText(teacher["email"])
            edtCommentTeacher.setText(teacher["comment"])
        } else {
            Toast.makeText(
                this, "Error loading teacher data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun applyChanges() {
        val name = edtNameTeacher.text.toString().trim()
        val email = edtEmailTeacher.text.toString().trim()
        val comment = edtCommentTeacher.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(
                this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val isUpdated = database.updateTeacher(teacherId, name, email, comment)

        if (isUpdated) {
            updateTeacherInFirebase(name, email, comment)
            setResult(RESULT_OK)
            Toast.makeText(
                this, "Teacher details updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(
                this, "Failed to update teacher details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTeacherInFirebase(name: String, email: String, comment: String) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val teacherRef = firebaseDatabase.getReference("teachers").child(teacherId.toString())

        val updatedData = mapOf(
            "name" to name,
            "email" to email,
            "comment" to comment
        )

        teacherRef.updateChildren(updatedData)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Teacher data updated in Firebase.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this, "Error updating Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}