package com.babyfeeding.data.repository

import com.babyfeeding.data.api.ApiService
import com.babyfeeding.data.api.HealthResponse
import com.babyfeeding.data.api.IdResponse
import com.babyfeeding.data.api.SuccessResponse
import com.babyfeeding.data.model.Baby
import com.babyfeeding.data.model.FeedingRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedingRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun health(): Flow<HealthResponse> = flow {
        emit(apiService.health())
    }.flowOn(Dispatchers.IO)

    fun getRecords(since: Long? = null): Flow<List<FeedingRecord>> = flow {
        emit(apiService.getRecords(since))
    }.flowOn(Dispatchers.IO)

    fun createRecord(record: FeedingRecord): Flow<IdResponse> = flow {
        emit(apiService.createRecord(record))
    }.flowOn(Dispatchers.IO)

    fun updateRecord(id: String, record: FeedingRecord): Flow<SuccessResponse> = flow {
        emit(apiService.updateRecord(id, record))
    }.flowOn(Dispatchers.IO)

    fun deleteRecord(id: String): Flow<SuccessResponse> = flow {
        emit(apiService.deleteRecord(id))
    }.flowOn(Dispatchers.IO)

    fun getBabies(): Flow<List<Baby>> = flow {
        emit(apiService.getBabies())
    }.flowOn(Dispatchers.IO)

    fun createBaby(baby: Baby): Flow<IdResponse> = flow {
        emit(apiService.createBaby(baby))
    }.flowOn(Dispatchers.IO)

    fun updateBaby(id: String, baby: Baby): Flow<SuccessResponse> = flow {
        emit(apiService.updateBaby(id, baby))
    }.flowOn(Dispatchers.IO)
}
