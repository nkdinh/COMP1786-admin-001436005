package com.example.universalyoga

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ClassDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var classId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        dbHelper = DBHelper(this)
        classId = intent.getLongExtra("CLASS_ID", -1)

        val tvDate = findViewById<TextView>(R.id.tvDataDate)
        val tvTime = findViewById<TextView>(R.id.tvDataTime)
        val tvQuantity = findViewById<TextView>(R.id.tvDataQuantity)
        val tvCourseDuration = findViewById<TextView>(R.id.tvDataCourseDuration)
        val tvPrice = findViewById<TextView>(R.id.tvDataPrice)
        val tvCategory = findViewById<TextView>(R.id.tvDataCategory)
        val tvLevel = findViewById<TextView>(R.id.tvDataLevel)
        val tvNameInstructor = findViewById<TextView>(R.id.tvDataNameInstructor)
        val tvComments = findViewById<TextView>(R.id.tvDataComment)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val courseNameTextView = findViewById<TextView>(R.id.DataCourseName)


        updateData(
            tvDate, tvTime, tvQuantity, tvCourseDuration, tvPrice, tvCategory, tvLevel, tvNameInstructor, tvComments, courseNameTextView
        )

        btnEdit.setOnClickListener { startEditActivity() }
        btnDelete.setOnClickListener { confirmDelete() }
        btnBack.setOnClickListener { finish() }

        supportActionBar?.hide()
    }

    private fun startEditActivity() {
        val editIntent = Intent(this, ClassEditActivity::class.java).apply {
            putExtra("CLASS_ID", classId)
        }
        startActivityForResult(editIntent, 100)
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete").setMessage("Sure?").setPositiveButton("Yes") { _, _ -> deleteClass() }.setNegativeButton("No", null).show()
    }

    private fun deleteClass() {
        val rowsDeleted = dbHelper.deleteClass(classId)
        if (rowsDeleted > 0) {
            FirebaseDatabase.getInstance().reference
                .child("universal yoga_classes")
                .child(classId.toString())
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(
                        this, "Class deleted successfully.", Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this, "Error deleting from Firebase: ${exception.message}", Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                this, "Error deleting class from local database", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            refreshData()
        }
    }

    private fun refreshData() {
        val tvDate = findViewById<TextView>(R.id.tvDataDate)
        val tvTime = findViewById<TextView>(R.id.tvDataTime)
        val tvQuantity = findViewById<TextView>(R.id.tvDataQuantity)
        val tvCourseDuration = findViewById<TextView>(R.id.tvDataCourseDuration)
        val tvPrice = findViewById<TextView>(R.id.tvDataPrice)
        val tvCategory = findViewById<TextView>(R.id.tvDataCategory)
        val tvLevel = findViewById<TextView>(R.id.tvDataLevel)
        val tvNameInstructor = findViewById<TextView>(R.id.tvDataNameInstructor)
        val tvComments = findViewById<TextView>(R.id.tvDataComment)
        val courseNameTextView = findViewById<TextView>(R.id.DataCourseName)


        updateData(tvDate, tvTime, tvQuantity, tvCourseDuration, tvPrice, tvCategory, tvLevel, tvNameInstructor, tvComments, courseNameTextView)
    }

    private fun updateData(
        tvDate: TextView, tvTime: TextView, tvQuantity: TextView,
        tvCourseDuration: TextView, tvPrice: TextView, tvCategory: TextView,
        tvLevel: TextView, tvNameInstructor: TextView, tvComments: TextView,
        courseNameTextView: TextView
    ) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_NAME,
            null,
            "${DBHelper.COLUMN_ID} = ?",
            arrayOf(classId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            tvDate.text =
                "Date: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_DAY))}"
            tvTime.text =
                "Start Time: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_TIME))}"
            tvQuantity.text =
                "Quantity: ${cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_CAPACITY))} members"
            tvCourseDuration.text =
                "Course Duration: ${cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_DURATION))} minutes"
            tvPrice.text =
                "Price: ${cursor.getInt(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_PRICE))}$"
            tvCategory.text =
                "Category: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_TYPE))}"
            tvLevel.text =
                "Level: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION))}"
            tvNameInstructor.text =
                "Name Instructor: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_INSTRUCTOR))}"
            tvComments.text =
                "Comments: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_COMMENTS))}"
            courseNameTextView.text =
                "Course Name: ${cursor.getString(cursor
                    .getColumnIndexOrThrow(DBHelper.COLUMN_COURSE_NAME))}"
        }
        else {
            Toast.makeText(
                this, "No class found.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}
