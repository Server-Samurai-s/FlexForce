package za.co.varsitycollege.serversamurai.flexforce

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.Models.AppDatabase
import za.co.varsitycollege.serversamurai.flexforce.Models.User
import java.io.Console

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var connectivityReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "flexforce-database"
        ).build()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    private fun syncPendingRegistrations() {
        CoroutineScope(Dispatchers.IO).launch {
            val pendingUsers = database.userDao().getAllUsers()
            Log.e("RegistrationSync", "Pending users: ${pendingUsers.size}");
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
                                Log.e("RegistrationSync", "Failed to sync user: ${user.email}. Error: $errorMessage");
                                Toast.makeText(applicationContext, "Failed to sync user: ${user.email}. Error: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }
    }
}