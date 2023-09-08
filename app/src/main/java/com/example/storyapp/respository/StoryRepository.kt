package com.example.storyapp.respository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.paging_source.StoryRemoteMediator
import com.example.storyapp.data.remote.ApiServices
import com.example.storyapp.model.ResponseAddStory
import com.example.storyapp.model.ResponseGetStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalPagingApi
class StoryRepository @Inject constructor(private val apiServices: ApiServices, private val storyDatabase: StoryDatabase) {

    fun getAllStories(token: String): LiveData<PagingData<Story>> {
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
        ).liveData
    }

    fun getStoryLocation(token: String): Flow<Result<ResponseGetStory>> = flow {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = generateBearerToken(token)
                val response = apiServices.getAllStories(bearerToken, size = 30, location = 1)
                emit(Result.success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure(e))
            }
        }
    }

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Flow<Result<ResponseAddStory>> = flow{
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiServices.uploadStory(bearerToken, file, description, lat, lon)
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

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }
}
