package com.example.imagesproject.network

import com.example.imagesproject.data.ImagesListData
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ImagesApi {

    /**
     * Get the images by the search term from the API
     *
     * @return [Single] [ImagesListData]
     */
    @GET("/api/")
    fun getImagesBySearch(
        @Query("key") key: String, @Query("q") query: String, @Query("page") page: Int, @Query(
            "per_page"
        ) perPage: Int
    ): Single<ImagesListData>
}