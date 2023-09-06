package com.example.storyapp.respository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.paging_source.StoryRemoteMediator
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

@ExperimentalPagingApi
class StoryRepository @Inject constructor(private val apiServices: ApiServices, private val storyDatabase: StoryDatabase) {

    fun getAllStories(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                apiServices,
                generateBearerToken(token)
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

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