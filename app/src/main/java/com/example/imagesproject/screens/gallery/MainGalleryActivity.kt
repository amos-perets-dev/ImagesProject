package com.example.imagesproject.screens.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagesproject.R
import com.example.imagesproject.models.picture.picture.IPicture
import com.google.common.collect.ImmutableList
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import kotlinx.android.synthetic.main.activity_main_gallery.*


class MainGalleryActivity : AppCompatActivity() {

    private val mainGalleryViewModel by lazy {
        MainGalleryInjector.createViewModel(this)
    }

    private val searchImagesButton by lazy {
        search_images_button
    }

    private val sendGalleryPhotoButton by lazy {
        add_image_gallery_button
    }

    private val recyclerViewPictures by lazy {
        images_recycler_view.setItemViewCacheSize(40)
        images_recycler_view.isDrawingCacheEnabled = true
        images_recycler_view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        images_recycler_view.isNestedScrollingEnabled = false
        images_recycler_view
    }

    private val compositeDisposable = CompositeDisposable()

    private var INDICATOR_ANIMATE_DURATION: Long = 0

//    private var galleryAdapter = MainGalleryAdapter()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_gallery)
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)

        INDICATOR_ANIMATE_DURATION =
            resources.getInteger(R.integer.activity_gallery_search_images_loading_images_indicator_animate_duration)
                .toLong()

        this.compositeDisposable.addAll(
            this.mainGalleryViewModel.getLastSearch()
                .doOnEvent { lastSearch, t2 -> search_images_edit_text.setText(lastSearch) }
                .subscribe { lastSearch, t2 ->
                    this.mainGalleryViewModel.setDefaultSearchTerm(
                        lastSearch
                    )
                },

            RxView.globalLayouts(recyclerViewPictures)
                .firstOrError()
                .subscribe(this::initRecyclerView, Throwable::printStackTrace),

            RxRecyclerView
                .scrollStateChanges(this.recyclerViewPictures)
                .map { newState ->
                    (!this.recyclerViewPictures.canScrollVertically(RecyclerView.FOCUS_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE)
                }
                .filter(Functions.equalsWith(true))
                .subscribe(Functions.actionConsumer(mainGalleryViewModel::endScroll)),

            this.mainGalleryViewModel
                .isNeedShowErrorMsg(this)
                .subscribe(this::showErrorMsg),

            RxView.clicks(this.searchImagesButton)
                .map { search_images_edit_text.text }
                .map(Editable::toString)
                .subscribe(this.mainGalleryViewModel::clickOnSearchImagesButton),

            RxView.clicks(this.sendGalleryPhotoButton)
                .map { Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) }
                .subscribe { startActivityForResult(it, OPEN_GALLERY_REQUEST) },

            this.mainGalleryViewModel.isLoadImages(this)
                .subscribe(this::changeStateByLoading),

            this.mainGalleryViewModel
                .getIntent(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { startActivity(it.first, it.second!!.toBundle()) }
        )
    }

    /**
     * Init the recycler view
     */
    private fun initRecyclerView(ignored: Any) {
        this.recyclerViewPictures.adapter = this.mainGalleryViewModel.getAdapter()
        this.recyclerViewPictures.layoutManager =
            StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun changeStateByLoading(isLoadImages: Boolean) {
        this.searchImagesButton.isEnabled = !isLoadImages
        loading_images_indicator
            .animate()
            .alpha(if (isLoadImages) 1f else 0f)
            .setDuration(INDICATOR_ANIMATE_DURATION)
            .start()
        this.searchImagesButton.setBackgroundColor(getBackgroundColorByLoadingState(isLoadImages))
    }

    private fun getBackgroundColorByLoadingState(isLoadImages: Boolean): Int =
        resources.getColor(
            if (isLoadImages) R.color.activity_gallery_images_search_button_background_unavailable_color
            else R.color.activity_gallery_images_search_button_background_available_color
        )

    /**
     * Show the error msg
     */
    private fun showErrorMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        this.compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        private const val OPEN_GALLERY_REQUEST = 1111
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OPEN_GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                startActivity(this.mainGalleryViewModel.setDataFromGallery(data!!.data))
            }
        }
    }
}
