package za.co.varsitycollege.st10038595.poedf_timetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoalsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val minGoalEditText = findViewById<EditText>(R.id.minGoalEditText)
        val maxGoalEditText = findViewById<EditText>(R.id.maxGoalEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        loadGoals { minGoal, maxGoal ->
            minGoalEditText.setText(minGoal.toString())
            maxGoalEditText.setText(maxGoal.toString())
        }

        saveButton.setOnClickListener {
            val minGoalText = minGoalEditText.text.toString()
            val maxGoalText = maxGoalEditText.text.toString()
            val minGoal = if (minGoalText.isNotBlank()) minGoalText.toInt() else null
            val maxGoal = if (maxGoalText.isNotBlank()) maxGoalText.toInt() else null

            if (minGoal != null && maxGoal != null) {
                saveGoals(minGoal, maxGoal)
            } else {
                Toast.makeText(this, "Please enter valid goals", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGoals(callback: (Int, Int) -> Unit) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val minGoal = document.getLong("minGoal")?.toInt() ?: 0
                val maxGoal = document.getLong("maxGoal")?.toInt() ?: 0
                callback(minGoal, maxGoal)
            }
            .addOnFailureListener {
                callback(0, 0)
            }
    }

    private fun saveGoals(minGoal: Int, maxGoal: Int) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val goals = hashMapOf(
            "minGoal" to minGoal,
            "maxGoal" to maxGoal
        )

        firestore.collection("users").document(userId).set(goals)
            .addOnSuccessListener {
                Toast.makeText(this, "Goals saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving goals: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
