package com.potatomeme.recreation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieApi {
    @GET("api/movie/{key}/photoList?page=1&size=12&adultFlag=T")
    fun getPhotos(@Path("key") key:String): Call<MoviePhotoResponse>
}