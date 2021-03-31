package com.example.coroutines

import retrofit2.Call
import retrofit2.http.*

interface JSONService {

    @GET("/posts")
    /*suspend*/ fun getPosts(): Call<ArrayList<Item>>

    @POST("/posts")
    /*suspend*/ fun addPost(@Body post: Item): Call<Item>

    @DELETE("posts/{id}")
    /*suspend*/ fun deletePost(@Path("id") id: Int): Call<Unit>

}