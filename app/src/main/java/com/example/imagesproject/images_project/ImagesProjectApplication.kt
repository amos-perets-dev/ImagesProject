package com.example.imagesproject.images_project

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class ImagesProjectApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()

        imagesProjectApplication = applicationContext as ImagesProjectApplication?

    }

    companion object Factory {

        private var imagesProjectApplication: ImagesProjectApplication? = null

        @JvmStatic
        fun create(): ImagesProjectApplication {
            if (imagesProjectApplication == null) {
                imagesProjectApplication =
                    ImagesProjectApplication()
            }
            return imagesProjectApplication as ImagesProjectApplication
        }
    }

}