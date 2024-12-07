package com.example.universalyoga

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var classList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var classIds: MutableList<Long>
    private lateinit var tvEmptyListMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        dbHelper = DBHelper(this)

        val radioGroupSearchFilter: RadioGroup = findViewById(R.id.radioGroupSearch)
        val spinnerDetail: Spinner = findViewById(R.id.spinnerDetail)
        tvEmptyListMessage = findViewById(R.id.tvEmptyListMessage)
        val listViewClasses: ListView = findViewById(R.id.listview_classes)
        val actInstructor: AutoCompleteTextView = findViewById(R.id.actInstructor)
        val edtSelectDate: EditText = findViewById(R.id.edtSelectDate)
        val backBtn: ImageButton = findViewById(R.id.btnBack)

        backBtn.setOnClickListener { finish() }

        radioGroupSearchFilter.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioBtnCategory -> {
                    edtSelectDate.visibility = View.GONE
                    actInstructor.visibility = View.GONE
                    spinnerDetail.visibility = View.VISIBLE
                    selectSearchDetails("Category", spinnerDetail)
                }

                R.id.radioBtnInstructor -> {
                    edtSelectDate.visibility = View.GONE
                    actInstructor.visibility = View.VISIBLE
                    spinnerDetail.visibility = View.GONE
                    initializeInstructorAutoComplete(actInstructor)
                }

                R.id.radioBtnDate -> {
                    edtSelectDate.visibility = View.VISIBLE
                    actInstructor.visibility = View.GONE
                    spinnerDetail.visibility = View.GONE
                }
            }
        }

        edtSelectDate.setOnClickListener {
            showSelectDateDialog(edtSelectDate)
        }

        listViewClasses.setOnItemClickListener { _, _, position, _ ->
            val classId = classIds[position]
            val intent = Intent(this, ClassDetailActivity::class.java)
            intent.putExtra("CLASS_ID", classId)
            startActivity(intent)
        }
        supportActionBar?.hide()
    }

    private fun initializeInstructorAutoComplete(InstructorAutoCompleteTextView: AutoCompleteTextView) {
        val nameInstructor = dbHelper.getInstructors()
        val InstructorAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, nameInstructor)
        InstructorAutoCompleteTextView.setAdapter(InstructorAdapter)

        InstructorAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedInstructor = parent.getItemAtPosition(position).toString()
            getClasses("instructor", selectedInstructor)
        }
    }

    private fun showSelectDateDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val dateSelectionDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)

            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)

            val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormatter.format(calendar.time)

            val finalDate = "$formattedDate, $dayOfWeek"
            editText.setText(finalDate)

            getClasses("Date", finalDate)
        }, year, month, day)

        dateSelectionDialog.show()
    }

    private fun selectSearchDetails(filter: String, spinnerDetails: Spinner) {
        val details: List<String> = when (filter) {
            "Category" -> dbHelper.getAllTypes()
            else -> listOf()
        }

        val detailsAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, details)
        detailsAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        spinnerDetails.adapter = detailsAdapter

        spinnerDetails.onItemSelectedListener = object
            : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                getClasses("Category", selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getClasses(filter: String, subOption: String) {
        val classes: List<Pair<Long, String>> = when (filter) {
            "Category" -> dbHelper.getClassesByType(subOption)
            "instructor" -> dbHelper.getClassesByInstructor(subOption)
            "Date" -> {
                getFormattedClassesByDate(subOption)
            }
            else -> listOf()
        }

        classList = classes.map { it.second }.toMutableList()
        classIds = classes.map { it.first }.toMutableList()

        adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, classList)
        findViewById<ListView>(R.id.listview_classes).adapter = adapter

        if (classList.isEmpty()) {
            tvEmptyListMessage.visibility = View.VISIBLE
            findViewById<ListView>(R.id.listview_classes).visibility = View.GONE
        } else {
            tvEmptyListMessage.visibility = View.GONE
            findViewById<ListView>(R.id.listview_classes).visibility = View.VISIBLE
        }

        adapter.notifyDataSetChanged()
    }

    private fun getFormattedClassesByDate(date: String): List<Pair<Long, String>> {
        val classes = dbHelper.getClassesByDate(date)
        return classes.map { (id, name) ->
            val formattedDate = formatDate(date)
            id to "$name ($formattedDate)"
        }
    }

    private fun formatDate(date: String): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(parsedDate)
    }
}
