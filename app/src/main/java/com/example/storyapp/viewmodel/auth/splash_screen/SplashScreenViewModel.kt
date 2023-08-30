package com.example.storyapp.viewmodel.splash_screen

import androidx.lifecycle.ViewModel
import com.example.storyapp.respository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SplashScreenViewModel constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()
}