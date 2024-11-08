package za.co.varsitycollege.serversamurai.flexforce.service

import okhttp3.Interceptor
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
import za.co.varsitycollege.serversamurai.flexforce.data.models.ApiResponse
import za.co.varsitycollege.serversamurai.flexforce.data.models.Challenge
import za.co.varsitycollege.serversamurai.flexforce.data.models.ChallengeResponse
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseResponse
import za.co.varsitycollege.serversamurai.flexforce.data.models.Response
import za.co.varsitycollege.serversamurai.flexforce.data.models.WorkoutEntity

object ApiClient {
    private const val BASE_URL = "https://flexforce-api.vercel.app/"
    private var authToken: String? = null

    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
            authToken?.let {
                request.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(request.build())
        })
        .build()

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun setAuthToken(token: String) {
        authToken = token
    }
}

interface ApiService {
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ApiResponse>

    @POST("api/workouts/saveWorkout/{userId}")
    fun addWorkout(
        @Path("userId") userId: String,
        @Body workoutRequest: WorkoutRequest
    ): Call<Void>

    @GET("api/workouts/getUserWorkouts/{userId}")
    fun getUserWorkouts(@Path("userId") userId: String): Call<List<WorkoutRequest>>

    @GET("api/workouts/chest-day")
    fun getChestDayWorkout(): Call<WorkoutEntity>

    @GET("api/workouts/leg-day")
    fun getLegDayWorkout(): Call<WorkoutEntity>

    @GET("api/workouts/challengesWorkouts")
    fun getChallengesWorkouts(): Call<ChallengeResponse>

    @GET("api/workouts/challenges/{id}")
    fun getChallengeView(@Path("id") challengeId: String): Call<Challenge>

    // Get challenges for a specific user
    @GET("api/workouts/user/{userId}/challenges")
    fun getUserChallenges(
        @Path("userId") userId: String
    ): Call<List<Challenge>>

    // Update user challenge status (e.g., started, completed, left)
    @POST("api/workouts/user/{userId}/challenges/update")
    fun updateUserChallengeStatus(
        @Path("userId") userId: String,
        @Body request: UpdateChallengeStatusRequest
    ): Call<Void>

    // Get the status of a specific user challenge
    @GET("api/workouts/user/{userId}/challenges/{challengeId}/status")
    fun getUserChallengeStatus(
        @Path("userId") userId: String,
        @Path("challengeId") challengeId: String
    ): Call<ChallengeStatusResponse>



    // Save a user workout
    @POST("save")
    @FormUrlEncoded
    fun saveUserWorkout(
        @Field("userId") userId: String,
        @Field("workoutName") workoutName: String,
        @Field("exercises") exerciseEntities: List<ExerciseEntity>
    ): Call<Response>

    // Delete a user workout
    @DELETE("user/{userId}/workout/{workoutId}")
    fun deleteUserWorkout(@Path("userId") userId: String, @Path("workoutId") workoutId: String): Call<Response>
}

data class MuscleRequest(val muscles: List<String>)

data class WorkoutRequest(
    val workoutName: String,
    val workoutDay: String,
    val exerciseEntities: List<ExerciseEntity>,
    val id: String? = null
)

data class UpdateChallengeStatusRequest(
    val challengeId: String,
    val status: String
)

data class ChallengeStatusResponse(
    val status: String
)

