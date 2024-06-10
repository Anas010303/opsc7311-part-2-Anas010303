package za.co.varsitycollege.st10038595.poedf_timetracker


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var isTimerRunning = false
    private var timerSecondsElapsed: Long = 0
    private lateinit var timerRunnable: Runnable
    private lateinit var timerHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        timerHandler = Handler()

        val loginButton: Button = findViewById(R.id.loginButton)
        val categoriesButton: Button = findViewById(R.id.categoriesButton)
        val timesheetEntriesButton: Button = findViewById(R.id.timesheetEntriesButton)
        val goalsButton: Button = findViewById(R.id.goalsButton)
        val timerButton: Button = findViewById(R.id.timerButton)
        val timerProgressBar: ProgressBar = findViewById(R.id.timerProgressBar)
        val timerTextView: TextView = findViewById(R.id.timerTextView)
        val statisticsButton: Button = findViewById(R.id.statisticsButton)

        loginButton.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                firebaseAuth.signOut()
                loginButton.text = getString(R.string.login)
                showToast("Logged out")
            }
        }
        categoriesButton.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }
        timesheetEntriesButton.setOnClickListener {
            startActivity(Intent(this, TimesheetEntriesActivity::class.java))
        }
        goalsButton.setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }
        statisticsButton.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
        timerButton.setOnClickListener {
            if (!isTimerRunning) {
                startTimer(timerTextView, timerProgressBar)
                timerButton.text = getString(R.string.pause_timer)
            } else {
                pauseTimer(timerTextView, timerButton)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val loginButton: Button = findViewById(R.id.loginButton)
        if (firebaseAuth.currentUser != null) {
            loginButton.text = getString(R.string.logout)
        }
    }

    private fun startTimer(timerTextView: TextView, timerProgressBar: ProgressBar) {
        isTimerRunning = true
        timerProgressBar.max = 60
        timerRunnable = object : Runnable {
            override fun run() {
                timerSecondsElapsed++
                timerTextView.text = formatTime(timerSecondsElapsed)
                timerProgressBar.progress = (timerSecondsElapsed % 60).toInt()
                timerHandler.postDelayed(this, 1000)
            }
        }
        timerHandler.postDelayed(timerRunnable, 1000)
    }

    private fun pauseTimer(timerTextView: TextView, timerButton: Button) {
        isTimerRunning = false
        timerButton.text = getString(R.string.resume_timer)
        timerHandler.removeCallbacks(timerRunnable)
        showToast("Your time is ${timerSecondsElapsed} seconds.")
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
