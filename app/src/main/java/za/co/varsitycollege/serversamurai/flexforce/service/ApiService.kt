package za.co.varsitycollege.serversamurai.flexforce.service


import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ExerciseResponse>
}


data class ExerciseResponse(val exercises: List<Exercise>)
data class Exercise(val name: String, val sets: Int, val reps: Int)
data class MuscleRequest(val muscles: List<String>)


object ApiClient {
    private const val BASE_URL = "https://flexforce-lac.vercel.app/"

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
