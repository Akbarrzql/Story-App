package com.example.storyapp.viewmodel.auth.register

import androidx.lifecycle.ViewModel
import com.example.storyapp.respository.AuthRepository
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) = authRepository.userRegister(name, email, password)

}