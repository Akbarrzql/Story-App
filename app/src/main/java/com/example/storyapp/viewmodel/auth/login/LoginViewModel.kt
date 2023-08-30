package com.example.storyapp.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.respository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel constructor(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun login(email: String, password: String) = authRepository.userLogin(email, password)

    fun saveAuthToken(token: String){
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }
}