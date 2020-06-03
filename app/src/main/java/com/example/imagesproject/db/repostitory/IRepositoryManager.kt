package com.example.imagesproject.db.repostitory

import com.example.imagesproject.models.picture.picture.IPicture
import com.google.common.collect.ImmutableList
import io.reactivex.Single

interface IRepositoryManager {

    /**
     * Get the all images by the search term from the API
     *
     * @param isNextPage check if is the new search or not,
     * true is a next page, false is a new search
     *
     *  @return [Single] List of all the images [IPicture]
     */
    fun getAllImagesBySearchTerm(
        searchTerm: String,
        isNextPage: Boolean
    ): Single<ImmutableList<IPicture>>

    /**
     * Get the all images by the last search term
     *
     *  @return [Single] List of all the images [IPicture]
     */
    fun getAllImages(): Single<ImmutableList<IPicture>>

    /**
     * Get the last search term
     *
     * @return [String] search term
     */
    fun getLastSearchTerm(): String

    /**
     * Add the the current search term
     *
     * @param search [String] current search term
     */
    fun addSearch(search: String)

    /**
     * Set the current image that on clicked
     *
     * @param position of the current image that on clicked
     */
    fun setCurrentImage(position: Int)

    /**
     * Get the current image that on clicked
     *
     * @return get the current image position that on clicked
     */
    fun getCurrentImage(): Int
}