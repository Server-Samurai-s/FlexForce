package za.co.varsitycollege.serversamurai.flexforce.service

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.Models.AppDatabase
import za.co.varsitycollege.serversamurai.flexforce.Models.User

class SyncManager(private val context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "flexforce-database"
    ).build()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun syncData() {
        CoroutineScope(Dispatchers.IO).launch {
            val users = database.userDao().getAllUsers()
            for (user in users) {
                firestore.collection("users").document(user.uid).set(user)
            }
        }
    }
}