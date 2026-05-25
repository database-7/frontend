package com.example.readmate_frontend.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("jwt_token", token).apply()
    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun saveUserId(id: String) = prefs.edit().putString("user_id", id).apply()
    fun getUserId(): String = prefs.getString("user_id", "") ?: ""

    fun saveUserNum(userNum: Int) = prefs.edit().putInt("user_num", userNum).apply()
    fun getUserNum(): Int = prefs.getInt("user_num", -1)

    fun saveUserName(name: String) = prefs.edit().putString("user_name", name).apply()
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""

    fun clear() = prefs.edit().clear().apply()
}