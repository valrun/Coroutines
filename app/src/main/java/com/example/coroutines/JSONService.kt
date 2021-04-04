package com.example.coroutines

import retrofit2.Call
import retrofit2.http.*

interface JSONService {

    @GET("/posts")
    fun getPosts(): Call<ArrayList<Item>>

    @POST("/posts")
    fun addPost(@Body post: Item): Call<Item>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id: Int): Call<Unit>

}