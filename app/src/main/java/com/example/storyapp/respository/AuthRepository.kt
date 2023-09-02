package com.example.storyapp.respository

import com.example.storyapp.data.local.AuthPreferencesDataSource
import com.example.storyapp.data.remote.ApiServices
import com.example.storyapp.model.ResponseLogin
import com.example.storyapp.model.ResponseRegister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiServices: ApiServices, private val authPreferencesDataSource: AuthPreferencesDataSource) {

        suspend fun userLogin(email: String, password: String): Flow<Result<ResponseLogin>> = flow {
                try {
                        val response = apiServices.loginUser(email, password)
                        emit(Result.success(response))
                } catch (e: Exception) {
                        e.printStackTrace()
                        emit(Result.failure(e))
                }
        }.flowOn(Dispatchers.IO)

        suspend fun userRegister(
                name: String,
                email: String,
                password: String
        ): Flow<Result<ResponseRegister>> = flow {
                try {
                        val response = apiServices.registerUser(name, email, password)
                        emit(Result.success(response))
                } catch (e: Exception) {
                        e.printStackTrace()
                        emit(Result.failure(e))
                }
        }.flowOn(Dispatchers.IO)


        fun getAuthToken(): Flow<String?> = authPreferencesDataSource.getAuthToken()

        suspend fun saveAuthToken(token: String) = authPreferencesDataSource.saveAuthToken(token)

//        suspend fun clear() = authPreferencesDataSource.clear()
}