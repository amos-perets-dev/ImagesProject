package com.example.imagesproject.network

interface INetworkManager {
    /**
     * Check if there is network
     */
    fun isNetworkAvailable(): Boolean
}