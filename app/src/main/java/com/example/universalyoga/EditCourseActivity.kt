package com.example.universalyoga

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class EditCourseActivity : AppCompatActivity() {

    private lateinit var edtCourseName: EditText
    private lateinit var edtTime: EditText
    private lateinit var edtComment: EditText
    private lateinit var btnSave: Button
    private lateinit var dbHelper: DBHelper
    private var courseId: Long = -1
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_course)
        supportActionBar?.hide()


        edtCourseName = findViewById(R.id.edtCourseName)
        edtTime = findViewById(R.id.edtTime)
        edtComment = findViewById(R.id.edtComment)
        btnSave = findViewById(R.id.btnSave)

        dbHelper = DBHelper(this)
        courseId = intent.getLongExtra("course_id", -1)
        btnBack = findViewById(R.id.btnBack)

        displayCourseDetails()

        edtTime.setOnClickListener {
            showDatePickerDialog()
        }

        btnSave.setOnClickListener {
            saveChanges()
        }
        btnBack.setOnClickListener { navigateToScreen(DetailCourseActivity::class.java) }


    }
    private fun navigateToScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun displayCourseDetails() {
        val course = dbHelper.getClassById(courseId)
        if (course != null) {
            edtCourseName.setText(course["name"])
            edtTime.setText(course["deadline"])
            edtComment.setText(course["comment"])
        } else {
            Toast.makeText(this, "Course not found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                edtTime.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun saveChanges() {
        val nameCourse = edtCourseName.text.toString().trim()
        val time = edtTime.text.toString().trim()
        val comment = edtComment.text.toString().trim()

        if (nameCourse.isEmpty() || time.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
            return
        }

        ContentValues().apply {
            put("name", nameCourse)
            put("deadline", time)
            put("comment", comment)
        }

        val updated = dbHelper.updateCourse(courseId, nameCourse, time, comment)
        if (updated > 0) {
            updateCourseInFirebase(courseId, nameCourse, time, comment)

            Toast.makeText(this, "Course updated successfully.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Failed to update course.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCourseInFirebase(courseId: Long, nameCourse: String, time: String, comment: String) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val coursesRef = firebaseDatabase.getReference("courses_yoga")

        val updatedCourseData = mapOf(
            "nameCourse" to nameCourse,
            "time" to time,
            "comment" to comment
        )

        coursesRef.child(courseId.toString()).updateChildren(updatedCourseData)
            .addOnSuccessListener {
                Toast.makeText(this, "Course data updated on Firebase successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error updating course data on Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
