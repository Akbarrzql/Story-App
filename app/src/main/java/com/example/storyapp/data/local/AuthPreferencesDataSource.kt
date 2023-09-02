package com.example.storyapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferencesDataSource @Inject constructor(private val dataSource: DataStore<Preferences>) {

    fun getAuthToken(): Flow<String?>{
        return dataSource.data.map { preferences ->
            preferences[KEY_AUTH]
        }
    }

    suspend fun saveAuthToken(token: String){
        dataSource.edit { preferences ->
            preferences[KEY_AUTH] = token
        }
    }

//    suspend fun clear(){
//        dataSource.edit { preferences ->
//            preferences.clear()
//        }
//    }

    companion object{
        private val KEY_AUTH = stringPreferencesKey("auth_token")
    }

}