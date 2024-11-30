package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.universalyoga.R
import com.example.universalyoga.DBHelper

class SearchTeacherActivity : AppCompatActivity() {
    private lateinit var edtSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var listTeacherDisplay: ListView
    private lateinit var database: DBHelper
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_teacher)
        supportActionBar?.hide()

        edtSearch = findViewById(R.id.edtSearch)
        btnSearch = findViewById(R.id.btnSearch)
        listTeacherDisplay = findViewById(R.id.listTeacherDisplay)
        btnBack = findViewById(R.id.btnBack)

        database = DBHelper(this)

        btnSearch.setOnClickListener {
            performSearch()
        }
        btnBack.setOnClickListener {
            navigateBackTo(ManageTeacherActivity::class.java)
        }

    }

    private fun navigateBackTo(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
        finish()
    }

    private fun performSearch() {
        val searchQuery = edtSearch.text.toString().trim()

        if (searchQuery.isEmpty()) {
            Toast.makeText(
                this, "Please enter a name to search", Toast.LENGTH_SHORT).show()
            return
        }

        val searchResults = database.searchTeacherByName(searchQuery)

        if (searchResults.isEmpty()) {
            Toast.makeText(
                this, "No teachers found", Toast.LENGTH_SHORT).show()
        } else {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                searchResults.map { "Name Teacher: ${it["name"]}\nEmail Teacher: ${it["email"]}\nComment: ${it["comment"]}" }
            )
            listTeacherDisplay.adapter = adapter
        }
    }
}
