// Create a Class given the Course and Type implemented
package com.example.universalyoga

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CreateClassActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var edtDate: EditText
    private lateinit var edtTime: EditText
    private lateinit var spinnerCourse: Spinner
    private lateinit var courseNames: List<String>

    // setting up buttons, spinners and others
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        dbHelper = DBHelper(this)

        initializeViews()
        loadCourseNames()


        edtDate.setOnClickListener { displayDatePicker(edtDate) }
        setupTimePicker(edtTime)

        setupSpinner(R.array.class_category, R.id.spinnerCategory)
        setupSpinner(R.array.level, R.id.spinnerLevel)

        findViewById<Button>(R.id.btnSubmit).setOnClickListener { handleSubmit() }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            navigateBackToMainActivity()
        }

        supportActionBar?.hide()
    }
        // loading course names from the Database to filter
        private fun loadCourseNames() {
            courseNames = dbHelper.getAllCourses().map { it["name"].toString() }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                courseNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourse.adapter = adapter
        }
    // Create a new view to add a new class
    private fun initializeViews() {
        edtDate = findViewById(R.id.edtDate)
        edtTime = findViewById(R.id.edtTime)
        spinnerCourse = findViewById(R.id.spinnerCourse)
    }
    // setting up spinners
    private fun setupSpinner(arrayResId: Int, spinnerResId: Int) {
        val spinner = findViewById<Spinner>(spinnerResId)
        val adapter = ArrayAdapter.createFromResource(
            this, arrayResId, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    // Display date picker for the Date section
    private fun displayDatePicker(editText: EditText) {
        val currentDate = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                currentDate.set(year, month, dayOfMonth)
                val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate.time)

                val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val formattedDate = dateFormatter.format(currentDate.time)

                val finalDate = "$formattedDate, $dayOfWeek"
                editText.setText(finalDate)
            },
            currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    // Display the Time Picker for the Time section
    private fun setupTimePicker(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val time = s.toString().replace(":", "")
                val formattedTime = when {
                    time.length >= 3 ->
                        "${time.substring(0, 2)}:${time.substring(2)}"
                    else -> time
                }

                editText.setText(formattedTime)
                editText.setSelection(formattedTime.length)

                isUpdating = false
            }
        })
    }
    // Method to handle the data being inputted and submitted
    private fun handleSubmit() {
        val selectedCourse = spinnerCourse.selectedItem.toString()
        val Instructor = findViewById<EditText>(R.id.edtEnterNameInstructor).text.toString().trim()
        val description = findViewById<Spinner>(R.id.spinnerLevel).selectedItem.toString()
        val dayOfWeek = edtDate.text.toString().trim()
        val timeOfCourse = edtTime.text.toString().trim()
        val capacity = findViewById<EditText>(R.id.edtQuantity).text.toString().trim().toIntOrNull() ?: 0
        val duration = findViewById<EditText>(R.id.edtDuration).text.toString().trim().toIntOrNull() ?: 0
        val pricePerClass = findViewById<EditText>(R.id.edtPrice).text.toString().trim().toFloatOrNull() ?: 0f
        val typeOfClass = findViewById<Spinner>(R.id.spinnerCategory).selectedItem.toString()
        val additionalComments = findViewById<EditText>(R.id.edtComment).text.toString().trim()

        if (Instructor.isEmpty() ||
            dayOfWeek.isEmpty() ||
            timeOfCourse.isEmpty()) {
            Toast.makeText(
                this, "Required fields cannot be left blank", Toast.LENGTH_SHORT).show()
            return
        }

        val newRowId = dbHelper.insertClass(
            Instructor = Instructor, description = description, dayOfWeek = dayOfWeek, course = selectedCourse, timeOfCourse = timeOfCourse, capacity = capacity, duration = duration, price = pricePerClass, typeOfClass = typeOfClass, additionalComments = additionalComments, date = dayOfWeek
        )

        if (newRowId != -1L) {
            saveToFirebase(newRowId, selectedCourse, Instructor, description, dayOfWeek, timeOfCourse, capacity, duration, pricePerClass, typeOfClass, additionalComments)
        } else {
            Toast.makeText(this, "Class creation failed", Toast.LENGTH_SHORT).show()
        }
    }
    // Saving data to Firebase
    private fun saveToFirebase(
        newRowId: Long, courseName: String, Instructor: String, description: String, dayOfWeek: String, timeOfCourse: String, capacity: Int, duration: Int, pricePerClass: Float, typeOfClass: String, additionalComments: String
    ) {
        val database = FirebaseDatabase.getInstance("https://universal-yoga-15325-default-rtdb.asia-southeast1.firebasedatabase.app")
        val yogaClassesRef = database.getReference("universal yoga_classes")

        val newClassData = mapOf(
            "Course" to courseName, "nameInstructor" to Instructor, "level" to description, "date" to dayOfWeek, "startTime" to timeOfCourse, "quantity" to capacity, "courseDuration" to duration, "price" to pricePerClass, "category" to typeOfClass, "comments" to additionalComments
        )

        yogaClassesRef.child(newRowId.toString()).setValue(newClassData)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "The class has been successfully created and the data has been saved.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this, "Error saving data to Firebase: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Linking back to the Main Activity
    private fun navigateBackToMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
        finish()
    }
}
