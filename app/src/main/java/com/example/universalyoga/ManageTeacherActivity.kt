package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ManageTeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_teacher)
        supportActionBar?.hide()

        val searchTeacherBtn: Button = findViewById(R.id.searchTeacherBtn)
        val createTeacherBtn: Button = findViewById(R.id.createTeacherBtn)
        val listTeacherBtn: Button = findViewById(R.id.listTeacherBtn)
        val btnBack: ImageView = findViewById(R.id.btnBack)

        searchTeacherBtn.setOnClickListener {
            openActivity(SearchTeacherActivity::class.java)
        }

        createTeacherBtn.setOnClickListener {
            openActivity(CreateTeacherActivity::class.java)
        }

        listTeacherBtn.setOnClickListener {
            openActivity(ListTeacherActivity::class.java)
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