package com.example.imagesproject.screens.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imagesproject.db.repostitory.IRepositoryManager
import com.example.imagesproject.models.picture.picture.IPicture
import com.example.imagesproject.models.validation.ISearchValidation
import com.example.imagesproject.models.validation.ValidationState
import com.example.imagesproject.screens.full_screen_gallery.FullScreenImagesActivity
import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainGalleryViewModel(
    private val searchValidation: ISearchValidation,
    private val repositoryManager: IRepositoryManager,
    private val galleryAdapter: MainGalleryAdapter,
    private val context: Context
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val showError = MutableLiveData<String>()

    private val intent = MutableLiveData<androidx.core.util.Pair<Intent, ActivityOptionsCompat>>()

    private val imagesListData = MutableLiveData<Pair<ImmutableList<IPicture>, Boolean>>()

    private val isLoadImages = MutableLiveData<Boolean>()

    private var searchTermTmp: String = ""

    init {
        this.compositeDisposable.add(
            this.galleryAdapter
            .getClickItem()
            .subscribe {clickOnImage(it,this. context)}
        )
    }

    /**
     * Call when click on search terms button
     *
     * @param [String] search term
     */
    fun clickOnSearchImagesButton(searchTerm: String) {
        this.searchTermTmp = searchTerm
        val validationState = searchValidation.isSearchValid(searchTerm).ordinal
        val isValid = validationState == ValidationState.VALID.ordinal
        if (isValid) {
            getImagesList(searchTerm, true)
        } else {
            notifyErrorTextByState(validationState)
        }
    }

    /**
     * Set to default the first search
     */
    fun setDefaultSearchTerm(lastSearch: String) {
        if (lastSearch.isNotEmpty()) clickOnSearchImagesButton(lastSearch)
    }

    private fun getImagesList(searchTerm: String, isClearPrevList: Boolean) {
        val isNextPage = !isClearPrevList
        this.compositeDisposable.add(
            this.repositoryManager
                .getAllImagesBySearchTerm(searchTerm, isNextPage)
                .doOnSubscribe { notifyLoadImages(true) }
                .subscribeOn(Schedulers.io())
                .doOnEvent { t1, t2 -> notifyLoadImages(false) }
                .doOnEvent { t1, t2 -> this.repositoryManager.addSearch(searchTerm) }
                .subscribe { imagesList, t2 -> notifyByStateList(imagesList, isClearPrevList) }
        )
    }

    private fun notifyByStateList(imagesList: ImmutableList<IPicture>, isClearPrevList: Boolean) {
        if (imagesList.isNotEmpty()) {
            updateList(imagesList, isClearPrevList)
        } else {
            notifyErrorTextByState(ValidationState.LAST_PAGE.ordinal)
        }
    }

    /**
     * Update the list [MainGalleryAdapter]
     */
    private fun updateList(picturesList : ImmutableList<IPicture>, isClearPrevList: Boolean) {
        this.galleryAdapter.updateList(picturesList, isClearPrevList)
    }

    /**
     * Call when the user scroll to the bottom
     */
    fun endScroll() {
        if (this.searchTermTmp.isEmpty()) return
        getImagesList(this.searchTermTmp, false)
    }

    private fun notifyErrorTextByState(validationState: Int) {
        val errorTextByState = searchValidation.getErrorTextByState(validationState)
        this.showError.postValue(errorTextByState)
    }

    private fun notifyLoadImages(isLoadImages: Boolean) {
        this.isLoadImages.postValue(isLoadImages)
    }

    fun isNeedShowErrorMsg(lifecycleOwner: LifecycleOwner): Observable<String> =
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, showError))

    fun getIntent(lifecycleOwner: LifecycleOwner): Observable<androidx.core.util.Pair<Intent, ActivityOptionsCompat>> =
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, intent))
            .subscribeOn(Schedulers.io())

    fun isLoadImages(lifecycleOwner: LifecycleOwner): Observable<Boolean> =
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, isLoadImages))

    fun getLastSearch() = Single.just(this.repositoryManager.getLastSearchTerm())

    private fun clickOnImage(
        pair: androidx.core.util.Pair<Int, ActivityOptionsCompat>,
        context: Context
    ) {
        this.repositoryManager.setCurrentImage(pair.first!!)
        this.intent.postValue(androidx.core.util.Pair(Intent(context, FullScreenImagesActivity::class.java), pair.second))
    }

    /**
     * Get the gallery adapter
     */
    fun getAdapter() = this.galleryAdapter

    override fun onCleared() {
        this.compositeDisposable.clear()
        super.onCleared()
    }

    fun setDataFromGallery(data: Uri?): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.putExtra(Intent.EXTRA_STREAM, data)
        shareIntent.type = "image/png"
        return shareIntent
    }
}