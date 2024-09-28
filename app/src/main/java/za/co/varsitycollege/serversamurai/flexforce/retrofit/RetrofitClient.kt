package za.co.varsitycollege.serversamurai.flexforce.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:5000/api/workouts/"

    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    // Fetch chest day workout
    @GET("chest-day")
    fun getChestDayWorkout(): Call<ApiDataModels.Workout>

    // Fetch leg day workout
    @GET("leg-day")
    fun getLegDayWorkout(): Call<ApiDataModels.Workout>

    // Fetch back day workout
    @GET("back-day")
    fun getBackDayWorkout(): Call<ApiDataModels.Workout>

    // Fetch arm day workout
    @GET("arm-day")
    fun getArmDayWorkout(): Call<ApiDataModels.Workout>

    @GET("challenge-view/{id}")
        fun getChallengeView(@Path("id") challengeId: String): Call<ApiDataModels.Challenge>


    // Save a user workout
    @POST("save")
    @FormUrlEncoded
    fun saveUserWorkout(@Field("userId") userId: String,
                        @Field("workoutName") workoutName: String,
                        @Field("exercises") exercises: List<ApiDataModels.Exercise>): Call<ApiDataModels.Response>

    // Delete a user workout
    @DELETE("user/{userId}/workout/{workoutId}")
    fun deleteUserWorkout(@Path("userId") userId: String, @Path("workoutId") workoutId: String): Call<ApiDataModels.Response>
}