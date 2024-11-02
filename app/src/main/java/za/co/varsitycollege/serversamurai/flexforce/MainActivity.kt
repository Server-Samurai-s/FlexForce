package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log // Import Log to use for logging
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.appcompat.widget.Toolbar
import com.google.firebase.analytics.FirebaseAnalytics // Import FirebaseAnalytics for tracking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging // Import FirebaseMessaging for push notifications

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics // Declare FirebaseAnalytics instance
    private lateinit var firebaseAuth: FirebaseAuth // Declare FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up navigation controller
        val navController = findNavController(R.id.nav_host_fragment)

        // Check if the user is remembered
        checkRememberedLogin(navController)

        setupActionBarWithNavController(navController)

        // Get FCM token
        fetchFCMToken()
    }

    // Function to check if the user is remembered and navigate accordingly
    private fun checkRememberedLogin(navController: androidx.navigation.NavController) {
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            // If "Remember Me" is true, navigate directly to the home screen
            navController.navigate(R.id.homeFragment)
        } else {
            // Otherwise, navigate to the login screen
            navController.navigate(R.id.welcomeFragment)
        }
    }

    // Function to fetch the FCM Token
    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the FCM token
            val token = task.result
            Log.d("FCM Token", "FCM Token: $token")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
