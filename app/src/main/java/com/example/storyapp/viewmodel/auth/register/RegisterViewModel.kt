package com.example.storyapp.viewmodel.register

import androidx.lifecycle.ViewModel
import com.example.storyapp.respository.AuthRepository
import androidx.lifecycle.viewModelScope

class RegisterViewModel constructor(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) = authRepository.userRegister(name, email, password)

}