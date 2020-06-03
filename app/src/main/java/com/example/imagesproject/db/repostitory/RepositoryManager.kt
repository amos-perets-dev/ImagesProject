package com.example.imagesproject.db.repostitory

import android.content.Context
import android.util.SparseIntArray
import com.example.imagesproject.data.ImageData
import com.example.imagesproject.data.ImagesListData
import com.example.imagesproject.db.perferences.ISharedPreferencesManager
import com.example.imagesproject.models.picture.helper.IPictureHelper
import com.example.imagesproject.models.picture.helper.PictureHelperImpl
import com.example.imagesproject.models.picture.picture.IPicture
import com.example.imagesproject.network.ImagesApi
import com.google.common.collect.FluentIterable
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class RepositoryManager(
    private val imagesApi: ImagesApi,
    private val key: String,
    private val pictureHelper: IPictureHelper,
    private val context: Context,
    private val sharedPreferencesManager: ISharedPreferencesManager,
    private val imagesPerPage: Int,
    private val imagesSparse: SparseIntArray,
    private var indexImage: Int,
    private val imagesListNotifier: BehaviorSubject<ImmutableList<IPicture>>,
    private val imagesListTmp: ArrayList<IPicture>,
    private var currentImage: Int
) : IRepositoryManager {

    override fun getAllImagesBySearchTerm(searchTerm: String, isNextPage: Boolean): Single<ImmutableList<IPicture>> {
        val nextPage = this.pictureHelper.getNextPage(isNextPage)
        val emptyList = FluentIterable.from(Lists.newArrayList<IPicture>()).toList()

        if (nextPage == PictureHelperImpl.LAST_PAGE) return Single.just(emptyList)

        return this.imagesApi.getImagesBySearch(this.key, searchTerm, nextPage, this.imagesPerPage)
            .subscribeOn(Schedulers.io())
            .doOnEvent { imagesData, t2 ->
                // Clear the  prev state
                // And calculate the total pages
                if(!isNextPage){
                    this.indexImage = 0
                    this.imagesListTmp.clear()
                    this.pictureHelper.calculatePages(imagesData.total, imagesData.totalHits)
                }
            }
            .map(ImagesListData::hits)
            .map(this::createPicturesList)
            .onErrorReturnItem( emptyList )
            .doOnEvent { imagesList, t2 ->
                this.imagesListTmp.addAll(imagesList)
                this.imagesListNotifier.onNext(FluentIterable.from(this.imagesListTmp).toList())
            }
            .cache()
            //Delay to Load images
            .delay(1500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
    }

    override fun getAllImages(): Single<ImmutableList<IPicture>> =
        this.imagesListNotifier.hide().firstOrError()

    override fun setCurrentImage(position: Int) {
        this.currentImage = position
    }

    override fun getCurrentImage(): Int = this.currentImage

    override fun getLastSearchTerm(): String = this.sharedPreferencesManager.getLastSearch()

    override fun addSearch(search : String) = this.sharedPreferencesManager.addSearch(search)

    /**
     * Create pictures list [ImmutableList[IPicture]]
     *
     * @return [ImmutableList[IPicture]]
     */
    private fun createPicturesList(imagesListData: List<ImageData>): ImmutableList<IPicture>  =
        FluentIterable.from(imagesListData)
            .transform {
                val imageData = it!!

                val pictureObject = pictureHelper.createPictureObject(this.context, imageData)
                this.imagesSparse.put(imageData.imageId, this.indexImage++)
                return@transform pictureObject
            }
            .toList()

    companion object Factory {

        private var repositoryManager: RepositoryManager? = null

        @JvmStatic
        fun create(): RepositoryManager {
            if (repositoryManager == null) {
                repositoryManager = RepositoryManagerInjector.createRepositoryManager()
            }
            return repositoryManager as RepositoryManager
        }
    }
}