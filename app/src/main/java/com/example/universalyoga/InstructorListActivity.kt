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

class InstructorListActivity : AppCompatActivity() {
    private lateinit var listInstructor: ListView
    private lateinit var database: DBHelper
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_list_instructor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listInstructor = findViewById(R.id.listInstructor)
        database = DBHelper(this)

        displayAllInstructors()


        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            navigateBackTo(InstructorManagementActivity::class.java)
        }


    }
    private fun navigateBackTo(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }
    override fun onResume() {
        super.onResume()
        displayAllInstructors()
    }

    private fun displayAllInstructors() {
        val instructors = database.getAllInstructors()

        val instructorDisplayList = instructors.map {
            "Name: ${it["name"]}\nEmail: ${it["email"]}\nComment: ${it["comment"]}"
        }
        val InstructorIds = instructors.map { it["id"] as Long }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            instructorDisplayList
        )
        listInstructor.adapter = adapter

        listInstructor.setOnItemClickListener { _, _, position, _ ->
            val selectedInstructorId = InstructorIds[position]
            openInstructorDetail(selectedInstructorId)
        }
    }

    private fun openInstructorDetail(InstructorId: Long) {
        val intent = Intent(this, InstructorDetailActivity::class.java)
        intent.putExtra("Instructor_ID", InstructorId)
        startActivity(intent)
    }
}
