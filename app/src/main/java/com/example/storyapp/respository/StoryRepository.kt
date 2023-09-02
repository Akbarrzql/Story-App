package com.example.storyapp.respository

import com.example.storyapp.data.remote.ApiServices
import com.example.storyapp.model.ResponseAddStory
import com.example.storyapp.model.ResponseGetStory
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class StoryRepository @Inject constructor(private val apiServices: ApiServices) {

    suspend fun getAllStories(
        token: String,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<ResponseGetStory>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiServices.getAllStories(bearerToken, page, size)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Flow<Result<ResponseAddStory>> = flow{
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiServices.uploadStory(bearerToken, file, description)
            emit(Result.success(response))
            println("Response: $response")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            emit(Result.failure(e))
        }
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}