package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

class HomeInnerView : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var editTxtGoalWeight: EditText
    private lateinit var editTxtGoalBodyFat: EditText
    private lateinit var submitGoalButton: Button

    private lateinit var editTxtCurrentWeight: EditText
    private lateinit var editTxtCurrentBodyFat: EditText
    private lateinit var editTxtHeight: EditText
    private lateinit var submitFitnessButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_inner_view, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize EditTexts and Buttons for Goals
        editTxtGoalWeight = view.findViewById(R.id.editTxt_GoalWeight)
        editTxtGoalBodyFat = view.findViewById(R.id.editTxt_GoalBodyFat)
        submitGoalButton = view.findViewById(R.id.buttonSubmitGoals)

        // Initialize EditTexts and Buttons for Fitness
        editTxtCurrentWeight = view.findViewById(R.id.editTxt_CurrentWeight)
        editTxtCurrentBodyFat = view.findViewById(R.id.editTxt_CurrentBodyFat)
        editTxtHeight = view.findViewById(R.id.editTxt_Height)
        submitFitnessButton = view.findViewById(R.id.buttonSubmitFitness)

        // Fetch the most recent fitness and goal data to populate hints
        fetchLatestFitnessEntry()
        fetchLatestGoalData()

        // Handle Submit Button Click for goals
        submitGoalButton.setOnClickListener {
            storeGoalData()
        }

        // Handle Submit Button Click for fitness data
        submitFitnessButton.setOnClickListener {
            storeFitnessData()
        }

        return view
    }

    // Function to fetch the most recent fitness entry
    private fun fetchLatestFitnessEntry() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("details")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fitnessEntries = document.get("fitnessEntries") as? List<Map<String, Any>>
                        if (!fitnessEntries.isNullOrEmpty()) {
                            // Get the most recent fitness entry (last entry in the list)
                            val latestEntry = fitnessEntries.last()

                            // Set the hints to the values from the latest entry for fitness data
                            editTxtCurrentWeight.hint = latestEntry["currentWeight"].toString() + " Kg"
                            editTxtCurrentBodyFat.hint = latestEntry["currentBodyFat"].toString() + " %"
                            editTxtHeight.hint = latestEntry["height"].toString() + " cm"
                        } else {
                            Toast.makeText(context, "No fitness data found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "No user profile found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching fitness data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to fetch the most recent goal data
    private fun fetchLatestGoalData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("goals")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val goalWeight = document.getString("goalWeight")
                        val goalBodyFat = document.getString("goalBodyFat")

                        // Set the hints to the values from the latest entry for goal data
                        editTxtGoalWeight.hint = "$goalWeight Kg"
                        editTxtGoalBodyFat.hint = "$goalBodyFat %"
                    } else {
                        Toast.makeText(context, "No goal data found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching goal data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to store goal data separately
    private fun storeGoalData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val goalWeight = editTxtGoalWeight.text.toString()
            val goalBodyFat = editTxtGoalBodyFat.text.toString()

            // Validate the input
            if (goalWeight.isEmpty() || goalBodyFat.isEmpty()) {
                Toast.makeText(context, "Please fill in both goal fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a map of goal data
            val goalData = hashMapOf(
                "goalWeight" to goalWeight,
                "goalBodyFat" to goalBodyFat,
                "dateSet" to Timestamp.now()  // Store the current date and time
            )

            // Store goal data in Firestore under users/userId/userDetails/goals
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("goals")
                .set(goalData)  // Replace the document with the new goal data
                .addOnSuccessListener {
                    Toast.makeText(context, "Goal data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error saving goal data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to store fitness data separately
    private fun storeFitnessData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val weight = editTxtCurrentWeight.text.toString()
            val bodyFat = editTxtCurrentBodyFat.text.toString()
            val height = editTxtHeight.text.toString()

            // Validate the input
            if (weight.isEmpty() || bodyFat.isEmpty() || height.isEmpty()) {
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a map of fitness data
            val fitnessData = hashMapOf(
                "currentWeight" to weight,
                "currentBodyFat" to bodyFat,
                "height" to height,
                "dateSubmitted" to Timestamp.now()  // Store the current date and time
            )

            // Store fitness data in Firestore under users/userId/userDetails/fitnessEntries
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("details")
                .update("fitnessEntries", FieldValue.arrayUnion(fitnessData))  // Append new entry to the list
                .addOnSuccessListener {
                    Toast.makeText(context, "Fitness data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error saving fitness data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
