package com.example.imagesproject.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.imagesproject.R
import com.example.imagesproject.images_project.ImagesProjectApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class NetworkManager : INetworkManager {

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

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            ImagesProjectApplication.create().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
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