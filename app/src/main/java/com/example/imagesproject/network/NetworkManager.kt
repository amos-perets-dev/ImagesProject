package com.example.imagesproject.network

import com.example.imagesproject.R
import com.example.imagesproject.images_project.ImagesProjectApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkManager {

    private var retrofit: Retrofit? = null

    init {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(ImagesProjectApplication.create().resources.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpClient.Builder().build())
                .build()
        }
    }

    fun getRetrofit(): Retrofit? {
        return retrofit
    }

    companion object Factory {

        private var networkManager: NetworkManager? = null

        @JvmStatic
        fun create(): NetworkManager {
            if (networkManager == null) {
                networkManager = NetworkManager()
            }
            return networkManager as NetworkManager
        }
    }

}