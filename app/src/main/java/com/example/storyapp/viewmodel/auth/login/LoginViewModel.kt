package com.example.storyapp.viewmodel.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.respository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun login(email: String, password: String) = authRepository.userLogin(email, password)

    fun saveAuthToken(token: String){
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }
}