package com.example.storyapp.viewmodel.location

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.storyapp.model.ResponseGetStory
import com.example.storyapp.respository.AuthRepository
import com.example.storyapp.respository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(private val storyRepository: StoryRepository, private val authRepository: AuthRepository) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()
    fun getAllStories(token: String): Flow<Result<ResponseGetStory>> = storyRepository.getStoryLocation(token)
}