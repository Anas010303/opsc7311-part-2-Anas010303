package za.co.varsitycollege.st10038595.poedf_timetracker

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimesheetEntriesActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var photoUri: Uri? = null
    private lateinit var entriesAdapter: ArrayAdapter<String>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_entries)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val entriesListView = findViewById<ListView>(R.id.entriesListView)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val hoursWorkedEditText = findViewById<EditText>(R.id.hoursWorkedEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val categoryEditText = findViewById<EditText>(R.id.categoryEditText)
        val addButton = findViewById<Button>(R.id.addButton)
        val photoImageView = findViewById<ImageView>(R.id.photoImageView)

        entriesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        entriesListView.adapter = entriesAdapter

        addButton.setOnClickListener {
            val date = dateEditText.text.toString()
            val hoursWorked = hoursWorkedEditText.text.toString().toFloatOrNull() ?: 0f
            val description = descriptionEditText.text.toString()
            val category = categoryEditText.text.toString()

            if (date.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
                val entryText = "Date: $date\nHours Worked: $hoursWorked\nDescription: $description\nCategory: $category"
                entriesAdapter.add(entryText)
                entriesAdapter.notifyDataSetChanged()
                saveTimesheetEntry(date, hoursWorked, description, category, photoUri?.toString())
                dateEditText.text.clear()
                hoursWorkedEditText.text.clear()
                descriptionEditText.text.clear()
                categoryEditText.text.clear()
                photoUri = null
                photoImageView.setImageDrawable(null)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        photoImageView.setOnClickListener {
            dispatchTakePictureIntent()
        }
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
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            val photoImageView = findViewById<ImageView>(R.id.photoImageView)
            photoImageView.setImageBitmap(imageBitmap)
            photoUri = data.data
        }
    }

    private fun saveTimesheetEntry(date: String, hoursWorked: Float, description: String, category: String, photoUri: String?) {
        val entry = TimesheetEntry(date, hoursWorked, description, category, photoUri)
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).collection("timesheetEntries")
            .add(entry)
            .addOnSuccessListener {
                Toast.makeText(this, "Timesheet entry saved.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
