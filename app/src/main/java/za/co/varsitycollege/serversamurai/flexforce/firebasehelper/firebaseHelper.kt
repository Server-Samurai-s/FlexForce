package za.co.varsitycollege.serversamurai.flexforce.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import za.co.varsitycollege.serversamurai.flexforce.Models.FitnessData

class firebaseHelper {

    private val db = FirebaseFirestore.getInstance()

    // Get the current user ID
    private fun getCurrentUserId(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid
    }

    // Create or Update FitnessData in Firestore
    fun upsertFitnessData(fitnessData: FitnessData, onComplete: (Boolean) -> Unit) {
        val userId = getCurrentUserId()
        if (userId != null) {
            db.collection("fitnessData").document(userId)
                .set(fitnessData)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            onComplete(false)
        }
    }
}