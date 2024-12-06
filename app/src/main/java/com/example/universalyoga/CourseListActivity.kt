package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CourseListActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_DETAIL_COURSE = 1
    }

    private lateinit var rvList: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var dbHelper: DBHelper
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_course)
        supportActionBar?.hide()

        rvList = findViewById(R.id.rvList)
        dbHelper = DBHelper(this)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            navigateBackTo(CourseActivity::class.java)
        }

        val courseList = dbHelper.getAllCourses()

        courseAdapter = CourseAdapter(courseList) { course ->
            val courseId = course["id"]?.toLongOrNull()
            if (courseId != null) {
                val intent = Intent(this, DetailCourseActivity::class.java).apply {
                    putExtra("course_id", courseId)
                    putExtra("course_name", course["name"])
                    putExtra("course_deadline", course["deadline"])
                    putExtra("course_comment", course["comment"])
                }
                startActivityForResult(intent, REQUEST_CODE_DETAIL_COURSE)
            } else {
                Toast.makeText(this, "Invalid course ID.", Toast.LENGTH_SHORT).show()
            }
        }

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = courseAdapter


    }
    private fun navigateBackTo(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DETAIL_COURSE && resultCode == RESULT_OK) {
            val updatedCourseList = dbHelper.getAllCourses()
            courseAdapter.updateData(updatedCourseList)
        }
    }
}

