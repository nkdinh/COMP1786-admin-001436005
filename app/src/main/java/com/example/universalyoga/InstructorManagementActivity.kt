package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class InstructorManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_instructor)
        supportActionBar?.hide()

        val searchInstructorBtn: Button = findViewById(R.id.searchInstructorBtn)
        val createInstructorBtn: Button = findViewById(R.id.createInstructorBtn)
        val listInstructorBtn: Button = findViewById(R.id.listInstructorBtn)
        val btnBack: ImageView = findViewById(R.id.btnBack)

        searchInstructorBtn.setOnClickListener {
            openActivity(SearchInstructorActivity::class.java)
        }

        createInstructorBtn.setOnClickListener {
            openActivity(CreateInstructorActivity::class.java)
        }

        listInstructorBtn.setOnClickListener {
            openActivity(InstructorListActivity::class.java)
        }
        btnBack.setOnClickListener {
            openActivity(MainActivity::class.java)
        }
    }
    private fun openActivity(activityClass: Class<*>) {
        Intent(this, activityClass).apply {
            startActivity(this)
        }
    }
}