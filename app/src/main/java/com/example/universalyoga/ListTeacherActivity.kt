package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.universalyoga.DBHelper
import com.example.universalyoga.R

class ListTeacherActivity : AppCompatActivity() {
    private lateinit var listTeacher: ListView
    private lateinit var database: DBHelper
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_list_teacher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listTeacher = findViewById(R.id.listTeacher)
        database = DBHelper(this)

        displayAllTeachers()


        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            navigateBackTo(ManageTeacherActivity::class.java)
        }


    }
    private fun navigateBackTo(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }
    override fun onResume() {
        super.onResume()
        displayAllTeachers()
    }

    private fun displayAllTeachers() {
        val teachers = database.getAllTeachers()

        val teacherDisplayList = teachers.map {
            "Name: ${it["name"]}\nEmail: ${it["email"]}\nComment: ${it["comment"]}"
        }
        val teacherIds = teachers.map { it["id"] as Long }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            teacherDisplayList
        )
        listTeacher.adapter = adapter

        listTeacher.setOnItemClickListener { _, _, position, _ ->
            val selectedTeacherId = teacherIds[position]
            openTeacherDetail(selectedTeacherId)
        }
    }

    private fun openTeacherDetail(teacherId: Long) {
        val intent = Intent(this, DetailTeacherActivity::class.java)
        intent.putExtra("TEACHER_ID", teacherId)
        startActivity(intent)
    }
}
