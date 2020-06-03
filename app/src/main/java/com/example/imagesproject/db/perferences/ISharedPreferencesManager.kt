package com.example.imagesproject.db.perferences

interface ISharedPreferencesManager {

    /**
     * Get the last search term
     */
    fun getLastSearch(): String

    /**
     * Add the search term to the DB
     */
    fun addSearch(search: String)

}