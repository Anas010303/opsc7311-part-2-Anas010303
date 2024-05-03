package za.co.varsitycollege.st10038595.poedf_timetracker
// GoalsActivity.kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class GoalsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        // Initialize UI components
        val minGoalEditText = findViewById<EditText>(R.id.minGoalEditText)
        val maxGoalEditText = findViewById<EditText>(R.id.maxGoalEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Load existing goals (if any) from preferences or database
        val existingMinGoal = loadMinGoalFromPreferences()
        val existingMaxGoal = loadMaxGoalFromPreferences()

        // Set initial values in EditTexts
        minGoalEditText.setText(existingMinGoal.toString())
        maxGoalEditText.setText(existingMaxGoal.toString())

        // Handle save button click
        saveButton.setOnClickListener {
            val minGoalText = minGoalEditText.text.toString()
            val maxGoalText = maxGoalEditText.text.toString()

            val minGoal = if (minGoalText.isNotBlank()) minGoalText.toInt() else null
            val maxGoal = if (maxGoalText.isNotBlank()) maxGoalText.toInt() else null


            // Save goals to preferences or database
            if (minGoal != null) {
                saveMinGoalToPreferences(minGoal)
            }
            if (maxGoal != null) {
                saveMaxGoalToPreferences(maxGoal)
            }

            // Show a success message (you can customize this)
            Toast.makeText(this, "Goals saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMinGoalFromPreferences(): Int {
        // Implement loading logic from preferences or database
        // For now, return a default value
        return 0
    }

    private fun loadMaxGoalFromPreferences(): Int {
        // Implement loading logic from preferences or database
        // For now, return a default value
        return 0
    }

    private fun saveMinGoalToPreferences(minGoal: Int) {
        // Implement saving logic to preferences or database
        // For now, print the value
        println("Minimum Goal: $minGoal")
    }

    private fun saveMaxGoalToPreferences(maxGoal: Int) {
        // Implement saving logic to preferences or database
        // For now, print the value
        println("Maximum Goal: $maxGoal")
    }
}

