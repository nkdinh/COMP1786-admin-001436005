package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import com.example.universalyoga.MainActivity
import com.example.universalyoga.R

class MainCourseActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_course)
        btnBack = findViewById(R.id.btnBack)
        supportActionBar?.hide()

        btnBack.setOnClickListener {
            navigateToActivity(MainActivity::class.java)
        }

        val btnCreate = findViewById<Button>(R.id.btnCreate)
        val btnList = findViewById<Button>(R.id.btnList)

        btnCreate.setOnClickListener {
            openActivity(CreateCourseActivity::class.java)
        }

        btnList.setOnClickListener {
            openActivity(ListCourseActivity::class.java)
        }
    }
    private fun navigateToActivity(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun openActivity(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}
