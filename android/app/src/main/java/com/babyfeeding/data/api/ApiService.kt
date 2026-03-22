package com.babyfeeding.data.api

import com.babyfeeding.data.model.Baby
import com.babyfeeding.data.model.FeedingRecord
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class HealthResponse(
    val status: String
)

data class IdResponse(
    val id: String
)

data class SuccessResponse(
    val success: Boolean
)

interface ApiService {
    @GET("health")
    suspend fun health(): HealthResponse

    @GET("api/records")
    suspend fun getRecords(@Query("since") since: Long? = null): List<FeedingRecord>

    @POST("api/records")
    suspend fun createRecord(@Body record: FeedingRecord): IdResponse

    @PUT("api/records/{id}")
    suspend fun updateRecord(
        @Path("id") id: String,
        @Body record: FeedingRecord
    ): SuccessResponse

    @DELETE("api/records/{id}")
    suspend fun deleteRecord(@Path("id") id: String): SuccessResponse

    @GET("api/babies")
    suspend fun getBabies(): List<Baby>

    @POST("api/babies")
    suspend fun createBaby(@Body baby: Baby): IdResponse

    @PUT("api/babies/{id}")
    suspend fun updateBaby(
        @Path("id") id: String,
        @Body baby: Baby
    ): SuccessResponse
}
