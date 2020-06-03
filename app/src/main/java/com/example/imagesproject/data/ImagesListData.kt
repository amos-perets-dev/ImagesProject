package com.example.imagesproject.data

import com.google.gson.annotations.SerializedName

data class ImagesListData(
    @SerializedName("total")
    var total: Int,

    @SerializedName("totalHits")
    var totalHits: Int,

    @SerializedName("hits")
    var hits: List<ImageData>
)