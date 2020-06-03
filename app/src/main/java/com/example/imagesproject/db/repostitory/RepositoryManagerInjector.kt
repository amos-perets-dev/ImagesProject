package com.example.imagesproject.db.repostitory

import android.util.SparseIntArray
import com.example.imagesproject.R
import com.example.imagesproject.db.perferences.ISharedPreferencesManager
import com.example.imagesproject.db.perferences.SharedPreferencesManager
import com.example.imagesproject.images_project.ImagesProjectApplication
import com.example.imagesproject.models.picture.helper.IPictureHelper
import com.example.imagesproject.models.picture.helper.PictureHelperImpl
import com.example.imagesproject.models.picture.picture.IPicture
import com.example.imagesproject.network.ImagesApi
import com.example.imagesproject.network.NetworkManager
import com.example.imagesproject.util.DisplayUtil
import com.google.common.collect.ImmutableList
import io.reactivex.subjects.BehaviorSubject

class RepositoryManagerInjector {

    companion object{

        fun createRepositoryManager() : RepositoryManager {
            val imagesApi = NetworkManager.create().getRetrofit()!!.create(ImagesApi::class.java)
            val context = ImagesProjectApplication.create().applicationContext
            val key = context.resources.getString(R.string.api_key)

            val resources = context.resources
            val imagesPerPage = resources.getInteger(R.integer.activity_gallery_search_images_per_page_size)
            val delayLoading = resources.getInteger(R.integer.activity_gallery_search_images_delay_loading)
            val pictureHelper : IPictureHelper = PictureHelperImpl(DisplayUtil.getScreenSize(context))
            val sharedPreferencesManager : ISharedPreferencesManager = SharedPreferencesManager(context)

             val imagesSparse = SparseIntArray()

             val indexImage = 0

             val imagesListNotifier = BehaviorSubject.create<ImmutableList<IPicture>>()

             val imagesListTmp = ArrayList<IPicture>()

             val currentImage = -1

            return RepositoryManager(
                imagesApi,
                key,
                pictureHelper,
                context,
                sharedPreferencesManager,
                imagesPerPage,
                imagesSparse,
                indexImage,
                imagesListNotifier,
                imagesListTmp,
                currentImage,
                delayLoading.toLong()
            )
        }
    }

}