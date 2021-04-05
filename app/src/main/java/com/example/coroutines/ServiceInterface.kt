package com.example.coroutines

import okhttp3.ResponseBody
import retrofit2.http.*

interface ServiceInterface {
    @GET("/posts")
    suspend fun getPosts(): ArrayList<Item>

    @POST("/posts")
    suspend fun addPost(@Body post: Item): Item?

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): ResponseBody

}