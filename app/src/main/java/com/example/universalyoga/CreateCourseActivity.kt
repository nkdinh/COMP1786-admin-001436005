// Create a Course to be implemented
package com.example.universalyoga

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class CreateCourseActivity : AppCompatActivity() {

    private lateinit var btnCreate: Button
    private lateinit var edtComment: EditText
    private lateinit var edtTime: EditText
    private lateinit var edtNameCourse: EditText
    private lateinit var dbHelper: DBHelper
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_course)
        supportActionBar?.hide()


        dbHelper = DBHelper(this)

        btnCreate = findViewById(R.id.btnCreate)
        edtComment = findViewById(R.id.edtComment)
        edtTime = findViewById(R.id.edtTime)
        edtNameCourse = findViewById(R.id.edtNameCourse)
        btnBack = findViewById(R.id.btnBack)

        edtTime.setOnClickListener { showDatePicker() }
        btnCreate.setOnClickListener { handleCourseCreation() }
        btnBack.setOnClickListener { navigateToScreen(CourseActivity::class.java) }
    }
    private fun navigateToScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                edtTime.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    private fun handleCourseCreation() {
        val nameCourse = edtNameCourse.text.toString().trim()
        val time = edtTime.text.toString().trim()
        val comment = edtComment.text.toString().trim()

        if (nameCourse.isEmpty() || time.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, "You have not filled all the information.", Toast.LENGTH_SHORT).show()
            return
        }

        val newRowId = dbHelper.insertCourse(nameCourse, time, comment)

        if (newRowId != -1L) {
            saveToFirebase(newRowId.toString(), nameCourse, time, comment)
        } else {
            showToast("Error: Unable to create the course.")
        }
    }



    private fun saveToFirebase(courseId: String, nameCourse: String, time: String, comment: String) {

        val firebaseDb  = FirebaseDatabase.getInstance()
        val coursesRef = firebaseDb .getReference("courses_yoga")


        val courseData = mapOf(
            "nameCourse" to nameCourse,
            "time" to time,
            "comment" to comment
        )


        coursesRef.child(courseId).setValue(courseData)
            .addOnSuccessListener {
                showToast("Course \"$nameCourse\" was created!")
                navigateToScreen(CourseActivity::class.java)
            }
            .addOnFailureListener { error ->
                showToast("Error saving data to Firebase: ${error.message}")
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
