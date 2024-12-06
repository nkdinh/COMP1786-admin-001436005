package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageClassActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var adapter: ClassListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_class)
        supportActionBar?.hide()
        btnBack = findViewById(R.id.btnBack)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = DBHelper(this)
        val classList = dbHelper.getAllClasses()

        adapter = ClassListAdapter(classList)
        recyclerView.adapter = adapter

        btnBack.setOnClickListener {
            navigateToScreen(MainActivity::class.java)
        }
    }

    private fun navigateToScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }
}
