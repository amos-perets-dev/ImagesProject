package com.example.imagesproject.screens.full_screen_gallery

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.imagesproject.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_full_screen_images.*


class FullScreenImagesActivity : AppCompatActivity() {

    private val fullScreenImagesViewModel by lazy {
        FullScreenImagesInjector.createViewModel(this)
    }

    private var imagesPagerAdapter: ImagesPagerAdapter? = null

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_images)

        compositeDisposable.add(
            fullScreenImagesViewModel
                .getAllImages()
                .subscribe { imagesList, t2 ->
                    this.imagesPagerAdapter = ImagesPagerAdapter(imagesList, resources.displayMetrics.widthPixels, this)
                    full_images_view_pager.adapter =  this.imagesPagerAdapter
                    full_images_view_pager.setCurrentItem(
                        fullScreenImagesViewModel.getCurrentImage(),
                        false
                    )
                }
        )
    }

    override fun onDestroy() {
        this.imagesPagerAdapter?.dispose()
        this.compositeDisposable.dispose()
        super.onDestroy()
    }
}