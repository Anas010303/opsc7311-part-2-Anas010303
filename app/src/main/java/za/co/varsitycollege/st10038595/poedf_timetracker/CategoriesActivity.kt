package za.co.varsitycollege.st10038595.poedf_timetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {
    private lateinit var categoryEditText: EditText
    private lateinit var categoryTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        categoryEditText = findViewById(R.id.categoryEditText)
        val saveCategoryButton: Button = findViewById(R.id.saveCategoryButton)
        categoryTextView = findViewById(R.id.categoryTextView)  // Assuming you have this TextView to display saved category

        saveCategoryButton.setOnClickListener {
            val categoryName = categoryEditText.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                saveCategory(categoryName)
                displayCategory(categoryName)
            } else {
                showToast("Please enter a valid category name")
            }
        }
    }

    private fun saveCategory(categoryName: String) {
        val sharedPreferences = getSharedPreferences("CategoryPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(categoryName, categoryName)
        editor.apply()
        showToast("Category saved successfully!")
    }

    private fun displayCategory(categoryName: String) {
        categoryTextView.text = "Selected category: $categoryName"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
