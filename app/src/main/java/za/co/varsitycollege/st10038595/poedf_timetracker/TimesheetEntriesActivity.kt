package za.co.varsitycollege.st10038595.poedf_timetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TimesheetEntriesActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var photoUri: Uri? = null
    private lateinit var entriesAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_entries)

        // Initialize UI components
        val dateEditText: EditText = findViewById(R.id.dateEditText)
        val startTimeEditText: EditText = findViewById(R.id.startTimeEditText)
        val endTimeEditText: EditText = findViewById(R.id.endTimeEditText)
        val descriptionEditText: EditText = findViewById(R.id.descriptionEditText)
        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        val photoButton: Button = findViewById(R.id.photoButton)
        val saveButton: Button = findViewById(R.id.saveButton)
        val listViewEntries: ListView = findViewById(R.id.listViewEntries)

        // Setup category spinner (populating it with categories from SharedPreferences)
        val categories = loadCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Populate the ListView with timesheet entries
        val entries = loadTimesheetEntries()
        entriesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        listViewEntries.adapter = entriesAdapter

        // Handle photo button click
        photoButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
            }
        }

        // Handle save button click
        saveButton.setOnClickListener {
            val date = dateEditText.text.toString()
            val startTime = startTimeEditText.text.toString()
            val endTime = endTimeEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()

            if (date.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && description.isNotEmpty()) {
                saveTimesheetEntry(date, startTime, endTime, description, category, photoUri?.toString())
                entriesAdapter.add("$date|$startTime|$endTime|$description|$category|${photoUri?.toString() ?: "No Photo"}")
                entriesAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Timesheet entry saved.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategories(): List<String> {
        val sharedPreferences = getSharedPreferences("CategoryPrefs", MODE_PRIVATE)
        return sharedPreferences.all.keys.toList()
    }

    private fun loadTimesheetEntries(): List<String> {
        val sharedPreferences = getSharedPreferences("TimesheetEntries", MODE_PRIVATE)
        return sharedPreferences.all.map { it.value as String }
    }

    private fun saveTimesheetEntry(date: String, startTime: String, endTime: String, description: String, category: String, photoUri: String?) {
        val sharedPreferences = getSharedPreferences("TimesheetEntries", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val entryKey = "$date $startTime"  // Using date and start time as a unique key
        val entryValue = "$date|$startTime|$endTime|$description|$category|${photoUri ?: "No Photo"}"
        editor.putString(entryKey, entryValue)
        editor.apply()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoUri = data?.data
        }
    }
}
