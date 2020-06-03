package com.example.imagesproject.db.perferences

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class SharedPreferencesManager(private val context: Context) : ISharedPreferencesManager{

    private val prefs = this.context.getSharedPreferences("com.example.imagesproject", AppCompatActivity.MODE_PRIVATE)

    override fun getLastSearch(): String = prefs.getString("last_search", "")!!

    override fun addSearch(search : String) {
        prefs.edit().putString("last_search", search).apply()
    }

}