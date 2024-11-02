package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.appcompat.widget.Toolbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase services
        initializeFirebaseServices()

        // Set up toolbar
        setupToolbar()

        // Set up navigation controller
        setupNavigation()

        // Fetch FCM token
        fetchFCMToken()
    }

    private fun initializeFirebaseServices() {
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Log.d("FirebaseInit", "Firebase services initialized.")
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Log.d("ToolbarSetup", "Toolbar has been set up.")
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)

        // Check if the user is remembered
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)
        val targetFragment = if (isRemembered) R.id.homeFragment else R.id.welcomeFragment

        navController.navigate(targetFragment)
        Log.d("Navigation", "Navigating to ${if (isRemembered) "Home" else "Welcome"} Fragment based on rememberMe preference.")
    }

    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                token?.let {
                    sharedPreferences.edit().putString("fcmToken", it).apply()
                    Log.d("FCM Token", "FCM Token: $it")
                    Toast.makeText(this, "FCM Token: $it", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                Toast.makeText(this, "Failed to fetch FCM token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
