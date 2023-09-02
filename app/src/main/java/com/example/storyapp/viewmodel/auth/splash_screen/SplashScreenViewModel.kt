package com.example.storyapp.viewmodel.auth.splash_screen

import androidx.lifecycle.ViewModel
import com.example.storyapp.respository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun getAuthToken(): Flow<String?> {
        return authRepository.getAuthToken()
    }
}