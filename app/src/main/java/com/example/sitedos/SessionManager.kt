package com.example.sitedos

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveLoginDetails(username: String, password: String, role : String, id : String) {
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("role",role)
        editor.putString("id_user", id)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        val username = sharedPreferences.getString("username", null)
        val password = sharedPreferences.getString("password", null)
        val role = sharedPreferences.getString("role", null)
        val id = sharedPreferences.getString("id_user", null)
        return !username.isNullOrBlank() && !password.isNullOrBlank()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    fun getRole() : String? {
        return sharedPreferences.getString("role",null)
    }

    fun getIdUser() : String? {
        return sharedPreferences.getString("id_user",null)
    }

    fun clearLoginDetails() {
        editor.remove("username")
        editor.remove("password")
        editor.remove("role")
        editor.remove("id_user")
        editor.apply()
    }
}
