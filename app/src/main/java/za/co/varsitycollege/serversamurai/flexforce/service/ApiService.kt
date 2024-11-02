package za.co.varsitycollege.serversamurai.flexforce.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import za.co.varsitycollege.serversamurai.flexforce.Exercise
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels

object ApiClient {
    private const val BASE_URL = "https://flexforce-api.vercel.app/"

    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {

    // Existing Endpoints
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ExerciseResponse>

    @POST("api/workouts/saveWorkout/{userId}")
    fun addWorkout(
        @Path("userId") userId: String,
        @Body workoutRequest: WorkoutRequest
    ): Call<Void>

    @GET("api/workouts/getUserWorkouts/{userId}")
    fun getUserWorkouts(@Path("userId") userId: String): Call<List<WorkoutRequest>>

    @GET("api/workouts/chest-day")
    fun getChestDayWorkout(): Call<ApiDataModels.Workout>

    @GET("api/workouts/leg-day")
    fun getLegDayWorkout(): Call<ApiDataModels.Workout>

    @GET("api/workouts/challengesWorkouts")
    fun getChallengesWorkouts(): Call<ApiDataModels.ChallengeResponse>

    @GET("api/workouts/challenges/{id}")
    fun getChallengeView(@Path("id") challengeId: String): Call<ApiDataModels.Challenge>

    // New Challenge Endpoints

    // Initialize challenges (one-time setup, no auth required)
    @POST("api/workouts/initialize-challenges")
    fun initializeChallenges(): Call<Void>

    // Get challenges for a specific user
    @GET("api/workouts/user/{userId}/challenges")
    fun getUserChallenges(
        @Path("userId") userId: String
    ): Call<List<ApiDataModels.Challenge>>

    // Update user challenge status (e.g., started, completed, left)
    @POST("api/workouts/user/{userId}/challenges/update")
    fun updateUserChallengeStatus(
        @Path("userId") userId: String,
        @Body request: UpdateChallengeStatusRequest
    ): Call<Void>


    // Save a user workout
    @POST("save")
    @FormUrlEncoded
    fun saveUserWorkout(
        @Field("userId") userId: String,
        @Field("workoutName") workoutName: String,
        @Field("exercises") exercises: List<ApiDataModels.Exercise>
    ): Call<ApiDataModels.Response>

    // Delete a user workout
    @DELETE("user/{userId}/workout/{workoutId}")
    fun deleteUserWorkout(@Path("userId") userId: String, @Path("workoutId") workoutId: String): Call<ApiDataModels.Response>
}


data class ExerciseResponse(val exercises: List<Exercise>)

data class MuscleRequest(val muscles: List<String>)

data class WorkoutRequest(
    val workoutName: String,
    val workoutDay: String,
    val exercises: List<Exercise>,  // Ensure this is a List
    val id: String? = null           // Optional id for workout
)

data class UpdateChallengeStatusRequest(
    val challengeId: String,
    val status: String
)
