package za.co.varsitycollege.serversamurai.flexforce

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.service.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: AppDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var connectivityReceiver: BroadcastReceiver
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Room database
        database = AppDatabase.getDatabase(applicationContext)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase services
        initializeFirebaseServices()

        // Set up toolbar
        setupToolbar()

        // Fetch FCM token
        fetchFCMToken()

        setupNavigation()

        // Register connectivity receiver
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetwork
                val isConnected = activeNetwork != null
                if (isConnected) {
                    syncPendingRegistrations()
                }
            }
        }
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
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

    private fun syncPendingRegistrations() {
        CoroutineScope(Dispatchers.IO).launch {
            val pendingUsers = database.userDao().getAllUsers()
            Log.e("RegistrationSync", "Pending users: ${pendingUsers.size}")
            for (user in pendingUsers) {
                auth.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            CoroutineScope(Dispatchers.IO).launch {
                                database.userDao().delete(user)
                            }
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                val errorMessage = task.exception?.message ?: "Unknown error"
                                Log.e("RegistrationSync", "Failed to sync user: ${user.email}. Error: $errorMessage")
                                Toast.makeText(applicationContext, "Failed to sync user: ${user.email}. Error: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }
    }
}