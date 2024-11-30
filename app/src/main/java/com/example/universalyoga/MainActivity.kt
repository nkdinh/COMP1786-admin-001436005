package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.universalyoga.CreateActivity
import com.example.universalyoga.MainCourseActivity
import com.example.universalyoga.ManageClassActivity
import com.example.universalyoga.ManageTeacherActivity
import com.example.universalyoga.SearchActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createBtn: Button = findViewById(R.id.createBtn)
        val viewClassBtn: Button = findViewById(R.id.viewClassBtn)
        val searchBtn: Button = findViewById(R.id.searchBtn)
        val manageTeacher: Button = findViewById(R.id.manageTeacher)
        val manageCourse: Button = findViewById(R.id.manageCourse)
        val manageClassInstance: Button = findViewById(R.id.manageClassInstance)

        createBtn.setOnClickListener {
            openActivity(CreateActivity::class.java)
        }

        viewClassBtn.setOnClickListener {
            openActivity(ListActivity::class.java)
        }

        searchBtn.setOnClickListener {
            openActivity(SearchActivity::class.java)
        }
        manageTeacher.setOnClickListener {
            openActivity(ManageTeacherActivity::class.java)
        }
        manageCourse.setOnClickListener {
            openActivity(MainCourseActivity::class.java)
        }
        manageClassInstance.setOnClickListener {
            openActivity(ManageClassActivity::class.java)
        }

        supportActionBar?.hide()
    }

    private fun openActivity(activityClass: Class<*>) {
        Intent(this, activityClass).apply {
            startActivity(this)
        }
    }
}
