package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createBtn: Button = findViewById(R.id.createBtn)
        val viewClassBtn: Button = findViewById(R.id.viewClassBtn)
        val searchBtn: Button = findViewById(R.id.searchBtn)
        val manageInstructor: Button = findViewById(R.id.manageInstructor)
        val manageCourse: Button = findViewById(R.id.manageCourse)
        val manageClassInstance: Button = findViewById(R.id.manageClassInstance)

        createBtn.setOnClickListener {
            openActivity(CreateClassActivity::class.java)
        }

        viewClassBtn.setOnClickListener {
            openActivity(ListFormatActivity::class.java)
        }

        searchBtn.setOnClickListener {
            openActivity(SearchActivity::class.java)
        }
        manageInstructor.setOnClickListener {
            openActivity(InstructorManagementActivity::class.java)
        }
        manageCourse.setOnClickListener {
            openActivity(CourseActivity::class.java)
        }
        manageClassInstance.setOnClickListener {
            openActivity(ClassManagementActivity::class.java)
        }

        supportActionBar?.hide()
    }

    private fun openActivity(activityClass: Class<*>) {
        Intent(this, activityClass).apply {
            startActivity(this)
        }
    }
}
