package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels.Workout
import za.co.varsitycollege.serversamurai.flexforce.retrofit.RetrofitClient

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_challenge_innerView.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_challenge_innerView : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_challenge_inner_view, container, false)

        // Initialize the TextView
        textView = view.findViewById(R.id.testapi)

        // Set up the back button
        val backBtnChallengeView: ImageButton = view.findViewById(R.id.backBtn)
        backBtnChallengeView.setOnClickListener {
            findNavController().navigate(R.id.action_nav_challenge_view_inner_to_nav_challenge_view)
            Toast.makeText(context, "Back button clicked", Toast.LENGTH_SHORT).show()
        }

        // Fetch the Chest Day workout
        fetchChestDayWorkout()

        return view
    }

    private fun fetchChestDayWorkout() {
        RetrofitClient.instance.getChestDayWorkout().enqueue(object : Callback<Workout> {
            override fun onResponse(call: Call<Workout>, response: Response<Workout>) {
                if (response.isSuccessful) {
                    val workout = response.body()
                    workout?.let {
                        val workoutInfo = """
                            Exercise 1: ${it.exercise1.name}, Sets: ${it.exercise1.sets}, Reps: ${it.exercise1.reps}
                            Exercise 2: ${it.exercise2.name}, Sets: ${it.exercise2.sets}, Reps: ${it.exercise2.reps}
                            Exercise 3: ${it.exercise3.name}, Sets: ${it.exercise3.sets}, Reps: ${it.exercise3.reps}
                        """.trimIndent()
                        textView.text = workoutInfo
                        Toast.makeText(context, "Workout data loaded", Toast.LENGTH_LONG).show()
                    }
                } else {
                    textView.text = "Failed to retrieve data"
                    Toast.makeText(context, "Failed to retrieve data", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Workout>, t: Throwable) {
                textView.text = "Error fetching data: ${t.message}"
                Toast.makeText(context, "Error fetching data: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_challenge_innerView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_challenge_innerView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}