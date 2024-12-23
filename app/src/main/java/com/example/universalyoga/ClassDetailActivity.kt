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
        startActivityForResult(editIntent, 100) // Deprecated, but no replacement yet
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
            tvDate.text = getString(
                R.string.date_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DAY))
            )
            tvTime.text = getString(
                R.string.start_time_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME))
            )
            tvQuantity.text = getString(
                R.string.quantity_text,
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CAPACITY))
            )
            tvCourseDuration.text = getString(
                R.string.course_duration_text,
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DURATION))
            )
            tvPrice.text = getString(
                R.string.price_text,
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE))
            )
            tvCategory.text = getString(
                R.string.category_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TYPE))
            )
            tvLevel.text = getString(
                R.string.level_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION))
            )
            tvNameInstructor.text = getString(
                R.string.instructor_name_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_INSTRUCTOR))
            )
            tvComments.text = getString(
                R.string.comments_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMMENTS))
            )
            courseNameTextView.text = getString(
                R.string.course_name_text,
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COURSE_NAME))
            )
        } else {
            Toast.makeText(this, getString(R.string.no_class_found), Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}
