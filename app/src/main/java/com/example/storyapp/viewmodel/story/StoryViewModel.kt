package com.example.storyapp.viewmodel.story

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.storyapp.model.ResponseAddStory
import com.example.storyapp.respository.AuthRepository
import com.example.storyapp.respository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
@ExperimentalPagingApi
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Flow<Result<ResponseAddStory>> = storyRepository.uploadStory(token, file, description)
}