// Language used: Kotlin + Java
package com.example.universalyoga

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    // setting up the layout
    private lateinit var databaseHelper: DBHelper
    private var yogaClassId: Long = -1
    private lateinit var edtDate: EditText
    private lateinit var edtTime: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var levelSpinner: Spinner
    private lateinit var spinnerCourseName: Spinner

    // Connecting the objects
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        supportActionBar?.hide()

        databaseHelper = DBHelper(this)
        yogaClassId = intent.getLongExtra("CLASS_ID", -1)

        spinnerCourseName = findViewById(R.id.spinnerCourseName)
        val edtNameTeacher = findViewById<EditText>(R.id.edtEnterNameTeacher)
        levelSpinner = findViewById(R.id.spinnerLevel)
        edtDate = findViewById(R.id.edtDate)
        edtTime = findViewById(R.id.edtTime)
        val edtQuantity = findViewById<EditText>(R.id.edtQuantity)
        val edtDuration = findViewById<EditText>(R.id.edtDuration)
        val edtPrice = findViewById<EditText>(R.id.edtPrice)
        categorySpinner = findViewById(R.id.spinnerCategory)
        val edtComments = findViewById<EditText>(R.id.edtComment)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack: ImageButton = findViewById(R.id.btnBack)


        setupCategorySpinner()
        setupLevelSpinner()
        loadCourseNames()

        edtDate.setOnClickListener {
            showDatePickerDialog(edtDate)
        }

        setupTimePicker(edtTime)

        changeData(
            edtDate, edtTime, edtQuantity, edtDuration, edtPrice,
            categorySpinner, edtNameTeacher, levelSpinner, edtComments, spinnerCourseName
        )

        btnSave.setOnClickListener {
            saveData(
                edtDate, edtTime, edtQuantity, edtDuration, edtPrice,
                categorySpinner, edtNameTeacher, levelSpinner, edtComments, spinnerCourseName
            )
        }

        btnBack.setOnClickListener { finish() }
    }

    // Loading the courses from Database
    private fun loadCourseNames() {
        val courseNames = databaseHelper.getAllCourses().map { it["name"].toString() }

        if (courseNames.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourseName.adapter = adapter
        } else {
            Toast.makeText(this, "No courses available", Toast.LENGTH_SHORT).show()
        }
    }

    // Loading the categories from Database
    private fun setupCategorySpinner() {
        val classCategory = resources.getStringArray(R.array.class_category)
        val classCategoryAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, classCategory)
        classCategoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = classCategoryAdapter
    }

    // Loading the levels from Database
    private fun setupLevelSpinner() {
        val level = resources.getStringArray(R.array.level)
        val levelAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, level)
        levelAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        levelSpinner.adapter = levelAdapter
    }

    // Editing the data
    private fun changeData(
        edtDate: EditText, edtTime: EditText, edtQuantity: EditText, edtDuration: EditText, edtPrice: EditText, categorySpinner: Spinner, edtNameTeacher: EditText, levelSpinner: Spinner, edtComments: EditText, courseNameSpinner: Spinner
    ) {
        val database = databaseHelper.readableDatabase
        val cursor = database.query(
            DBHelper.TABLE_NAME,
            null,
            "${DBHelper.COLUMN_ID} = ?",
            arrayOf(yogaClassId.toString()),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            edtDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_DAY)))
            edtTime.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_TIME)))
            edtQuantity.setText(cursor.getInt(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_CAPACITY)).toString())
            edtDuration.setText(cursor.getInt(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_DURATION)).toString())
            val price = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRICE)).toInt()
            edtPrice.setText(price.toString())
            edtNameTeacher.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_TEACHER)))
            edtComments.setText(cursor.getString(cursor.getColumnIndexOrThrow(
                DBHelper.COLUMN_COMMENTS)))

            val classCategory = resources.getStringArray(R.array.class_category)
            val level = resources.getStringArray(R.array.level)
            val courseName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COURSE_NAME))


            categorySpinner.setSelection(classCategory.indexOf(
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TYPE))))
            levelSpinner.setSelection(level.indexOf(
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION))))

            val coursePosition = (courseNameSpinner.adapter as ArrayAdapter<String>).getPosition(courseName)
            courseNameSpinner.setSelection(coursePosition)

        } else {
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }

    // Saving data
    private fun saveData(
        edtDate: EditText, edtTime: EditText, edtQuantity: EditText, edtDuration: EditText, edtPrice: EditText, categorySpinner: Spinner, edtNameTeacher: EditText, levelSpinner: Spinner, edtComments: EditText, courseNameSpinner: Spinner
    ) {
        val dayOfWeek = edtDate.text.toString().trim()
        val courseTime = edtTime.text.toString().trim()
        val capacity = edtQuantity.text.toString().trim().toIntOrNull()
        val duration = edtDuration.text.toString().trim().toIntOrNull()
        val pricePerClass = edtPrice.text.toString().trim().toFloatOrNull()
        val typeOfClass = categorySpinner.selectedItem.toString()
        val teacherName = edtNameTeacher.text.toString().trim()
        val description = levelSpinner.selectedItem.toString()
        val additionalComments = edtComments.text.toString().trim()
        val courseName = courseNameSpinner.selectedItem.toString()


        if (dayOfWeek.isEmpty() ||
            courseTime.isEmpty() ||
            capacity == null ||
            duration == null ||
            pricePerClass == null ||
            teacherName.isEmpty()) {
            Toast.makeText(
                this, "Please fill in all information", Toast.LENGTH_SHORT).show()
            return
        }

        val contentValues = ContentValues().apply {
            put(DBHelper.COLUMN_DAY, dayOfWeek)
            put(DBHelper.COLUMN_TIME, courseTime)
            put(DBHelper.COLUMN_CAPACITY, capacity)
            put(DBHelper.COLUMN_DURATION, duration)
            put(DBHelper.COLUMN_PRICE, pricePerClass)
            put(DBHelper.COLUMN_TYPE, typeOfClass)
            put(DBHelper.COLUMN_TEACHER, teacherName)
            put(DBHelper.COLUMN_DESCRIPTION, description)
            put(DBHelper.COLUMN_COMMENTS, additionalComments)
            put(DBHelper.COLUMN_DATE, dayOfWeek)
            put(DBHelper.COLUMN_COURSE_NAME, courseName)
        }

        val rowsAffected = databaseHelper.updateClass(yogaClassId, contentValues)
        if (rowsAffected > 0) {
            val database = FirebaseDatabase.getInstance()
            val yogaClassesRef = database.getReference("universal yoga_classes")

            val updatedClassData = mutableMapOf<String, Any>(
                "date" to dayOfWeek, "startTime" to courseTime, "quantity" to capacity, "courseDuration" to duration, "price" to pricePerClass, "category" to typeOfClass, "nameTeacher" to teacherName, "level" to description, "comments" to additionalComments, "Course" to courseName
            )

            yogaClassesRef.child(yogaClassId.toString()).updateChildren(updatedClassData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this, "The class has been updated and saved to Firebase.", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this, "Error saving data to Firebase: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(
                this, "Error while updating class", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)

            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)  // "EEEE" để lấy tên thứ
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            val finalDate = "$dayOfWeek, $formattedDate"

            editText.setText(finalDate)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupTimePicker(editText: EditText) {
        editText.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                val timeFormat = String.format("%02d:%02d", hourOfDay, minute)
                editText.setText(timeFormat)
            }, 12, 0, true)

            timePickerDialog.show()
        }
    }
}
