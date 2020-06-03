package com.example.imagesproject.screens.full_screen_gallery

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.imagesproject.db.repostitory.IRepositoryManager
import com.example.imagesproject.db.repostitory.RepositoryManager

class FullScreenImagesInjector {

    companion object {

        fun createViewModel(activity: FragmentActivity): FullScreenImagesViewModel {

            val repositoryManager: IRepositoryManager = RepositoryManager.create()

            val viewModel = FullScreenImagesViewModel(
                repositoryManager.getAllImages(),
                repositoryManager.getCurrentImage()
            )

            val viewModelFactory = createViewModelFactory(viewModel)

            return ViewModelProviders.of(activity, viewModelFactory)
                .get(FullScreenImagesViewModel::class.java)
        }

        private fun createViewModelFactory(viewModel: Any): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return viewModel as T
                }
            }
        }

    }


}