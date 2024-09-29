package za.co.varsitycollege.serversamurai.flexforce.service


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import za.co.varsitycollege.serversamurai.flexforce.Exercise

interface ApiService {
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ExerciseResponse>

    @POST("api/workouts/saveWorkout/{userId}")
    fun addWorkout(
        @Path("userId") userId: String,
        @Body workoutRequest: WorkoutRequest
    ): Call<Void>

    // Fixed the typo in the endpoint
    @GET("api/workouts/getUserWorkouts/{userId}")
    fun getUserWorkouts(@Path("userId") userId: String): Call<List<WorkoutRequest>>
}

data class ExerciseResponse(val exercises: List<Exercise>)

data class MuscleRequest(val muscles: List<String>)

data class WorkoutRequest(
    val workoutName: String,
    val workoutDay: String,
    val exercises: List<Exercise>,  // Ensure this is a List
    val id: String? = null           // Optional id for workout
)

object ApiClient {
    private const val BASE_URL = "https://flexforce-api.vercel.app/"

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

