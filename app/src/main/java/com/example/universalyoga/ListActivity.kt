package com.example.universalyoga

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universalyoga.DetailActivity


class ListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var yogaClasses: MutableList<String>
    private lateinit var classIds: MutableList<Long>
    private lateinit var adapter: YogaClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        supportActionBar?.hide()

        dbHelper = DBHelper(this)

        val recyclerView = findViewById<RecyclerView>(R.id.rvClasses)
        yogaClasses = loadDataFromDatabase()

        recyclerView.layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.custom_divider)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)



        adapter = YogaClassAdapter(yogaClasses) { position ->
            val classId = classIds[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            navigateBackToMainActivity()
        }

        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToMainActivity()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        yogaClasses.clear()
        yogaClasses.addAll(loadDataFromDatabase())
        adapter.notifyDataSetChanged()
    }

    private fun loadDataFromDatabase(): MutableList<String> {
        val classList = mutableListOf<String>()
        classIds = mutableListOf()

        val db = dbHelper.readableDatabase
        val cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val classId = cursor.getLong(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_ID))
            val day = cursor.getString(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_DAY))
            val type = cursor.getString(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_TYPE))

            val classDetails = "$day - $type"
            classList.add(classDetails)
            classIds.add(classId)
        }
        cursor.close()

        return classList
    }

    private fun navigateBackToMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
        finish()
    }
}
