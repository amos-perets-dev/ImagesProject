package com.example.imagesproject.screens.gallery

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.imagesproject.R
import com.example.imagesproject.db.repostitory.IRepositoryManager
import com.example.imagesproject.db.repostitory.RepositoryManager
import com.example.imagesproject.images_project.ImagesProjectApplication
import com.example.imagesproject.models.validation.ISearchValidation
import com.example.imagesproject.models.validation.SearchValidationImpl

class MainGalleryInjector {

    companion object {

        fun createViewModel(activity: FragmentActivity): MainGalleryViewModel {

            val context = ImagesProjectApplication.create().applicationContext
            val maxLengthSearchTerm = context.resources.getInteger(R.integer.activity_gallery_search_images_edit_text_max_length)
            val errorMsgTextEmptyType = context.resources.getString(R.string.activity_gallery_images_search_error_msg_empty_type_text)
            val errorMsgTextLengthType = context.resources.getString(R.string.activity_gallery_images_search_error_msg_length_type_text)
            val errorMsgTextLastPage = context.resources.getString(R.string.activity_gallery_images_search_error_msg_last_page_text)

            val searchValidation : ISearchValidation =
                SearchValidationImpl(maxLengthSearchTerm, errorMsgTextEmptyType, errorMsgTextLengthType, errorMsgTextLastPage)

            val repositoryManager : IRepositoryManager = RepositoryManager.create()

            var galleryAdapter = MainGalleryAdapter()

            val viewModel = MainGalleryViewModel(searchValidation, repositoryManager, galleryAdapter, context)

            val viewModelFactory = createViewModelFactory(viewModel)

            return ViewModelProviders.of(activity , viewModelFactory).get(MainGalleryViewModel::class.java)
        }

        private fun  createViewModelFactory(viewModel: Any): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return viewModel as T
                }
            }
        }

    }



}