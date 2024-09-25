package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        FirebaseAuth.getInstance()

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
    }

    // Function to check if the user is remembered and navigate accordingly
    private fun checkRememberedLogin(navController: androidx.navigation.NavController) {
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            // If "Remember Me" is true, navigate directly to the home screen
            navController.navigate(R.id.homeFragment)
        } else {
            // Otherwise, navigate to the login screen
            navController.navigate(R.id.loginFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
