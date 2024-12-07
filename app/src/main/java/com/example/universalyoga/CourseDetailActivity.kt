package com.example.universalyoga


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var tvNameCourse: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvComment: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var dbHelper: DBHelper
    private var courseId: Long = -1

    private lateinit var btnBack: ImageButton

    companion object {
        const val REQUEST_CODE_EDIT_COURSE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_course)
        supportActionBar?.hide()

        tvNameCourse = findViewById(R.id.tvNameCourse)
        tvTime = findViewById(R.id.tvTime)
        tvComment = findViewById(R.id.tvComment)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)
        dbHelper = DBHelper(this)
        courseId = intent.getLongExtra("course_id", -1)

        if (courseId == -1L) {
            Toast.makeText(this, "Invalid course ID.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        displayCourseDetails()

        btnEdit.setOnClickListener {
            navigateToEditCourse()
        }

        btnDelete.setOnClickListener {
            confirmAndDeleteCourse()
        }

        btnBack.setOnClickListener {
            navigateToScreen(CourseListActivity::class.java)
        }

    }
    private fun navigateToScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun displayCourseDetails() {
        val course = dbHelper.getClassById(courseId)
        if (course != null) {
            tvNameCourse.text = course["name"]
            tvTime.text = course["deadline"]
            tvComment.text = course["comment"]
        } else {
            Toast.makeText(this, "Course not found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun navigateToEditCourse() {
        val intent = Intent(this, CourseEditActivity::class.java)
        intent.putExtra("course_id", courseId)
        startActivityForResult(intent, REQUEST_CODE_EDIT_COURSE)
    }

    private fun confirmAndDeleteCourse() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Sure?")

        builder.setPositiveButton("Yes") { _, _ ->
            val firebaseRef = FirebaseDatabase.getInstance().getReference("courses_yoga").child(courseId.toString())
            firebaseRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val rowsDeleted = dbHelper.deleteCourse(courseId)
                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Course deleted successfully.", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to delete course from local database.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to delete course from Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_COURSE && resultCode == RESULT_OK) {
            displayCourseDetails()
        }
    }

    private fun applyChangesAndClose() {
        setResult(RESULT_OK)
        finish()
    }
}
