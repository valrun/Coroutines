package com.example.coroutines

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.example.coroutines.datebase.AppDatabase
import com.example.coroutines.list.ItemAdapter
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApp : Application() {
    private lateinit var mRetrofit: Retrofit
    private lateinit var mService: JSONService
    var mAdapter: ItemAdapter? = null

    private lateinit var mDateBase: AppDatabase
    private var mSharedPreference: SharedPreferences? = null
    private var emptyBase: Boolean = true

    var listItems: MutableList<Item> = mutableListOf()
    private var indexOfNewItem = 0

    var textView: TextView? = null
    private var animatorSet: AnimatorSet? = null

    private lateinit var scope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        instance = this

        mRetrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mService = mRetrofit.create(JSONService::class.java)

        mSharedPreference = getSharedPreferences("take", Context.MODE_PRIVATE)
        emptyBase = this.mSharedPreference?.getBoolean(EMPTY, true) ?: true
        mDateBase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()

        scope = CoroutineScope(Dispatchers.IO)
    }

    fun onDestroy() {
        scope.cancel()
    }

    private fun <T> mCallback(success: (Response<T>) -> (Unit)): Callback<T> {
        return mCallback(success) {}
    }

    private fun <T> mCallback(success: (Response<T>) -> (Unit), fail: () -> (Unit)): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    success(response)
                } else {
                    toast("unsuccessful")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                toast("something wrong :\n${t.message}")
                fail()
            }
        }
    }

    fun loadItems() {
        startLoadAnimation()

        println("emptyBase:$emptyBase")
        if (emptyBase) {
            mService.getPosts().enqueue(mCallback({ getPosts(it) }, { stopLoadAnimation() }))
        } else {
            scope.launch {
                val date = async { mDateBase.itemDao()?.getItems() }
                withContext(Dispatchers.Main) {
                    val list = date.await()
                    if (list != null) {
                        listItems.addAll(list)
                        mAdapter?.notifyDataSetChanged()
                        listItems.forEach {
                            if (indexOfNewItem < it.id + 1) {
                                indexOfNewItem = it.id + 1
                            }
                        }
                    }
                    stopLoadAnimation()
                }
            }
        }
    }

    private fun getPosts(response: Response<ArrayList<Item>>) {
        response.body()?.forEach {
            listItems.add(it)
            mAdapter?.notifyDataSetChanged()
            if (indexOfNewItem < it.id + 1) {
                indexOfNewItem = it.id + 1
            }
        }
        scope.launch {
            mDateBase.itemDao()?.insertItems(listItems)
        }

        emptyBase = false
        mSharedPreference?.edit()?.apply {
            this.putBoolean(EMPTY, emptyBase)
            apply()
        }

        stopLoadAnimation()
    }

    fun deleteItem(id: Int) {
        mService.deletePost(id)
            .enqueue(mCallback {
                toast(
                    "you deleted post (id: $id)\n" +
                            "response code: ${it.code()}"
                )
            })

        scope.launch {
            mDateBase.itemDao()?.deleteByID(id)
        }

        for ((index, item) in listItems.withIndex()) {
            if (item.id == id) {
                listItems.removeAt(index)
                mAdapter?.notifyDataSetChanged()
                break
            }
        }
    }

    fun addItem(title: String, text: String) {
        val item = Item(indexOfNewItem, title, text, 0)
        mService.addPost(item)
            .enqueue(mCallback {
                val res = it.body()
                if (res != null) {
                    toast(
                        "Post added:\n" +
                                "User id:${res.userId}\n" +
                                "Post id:${res.id}\n" +
                                "Title:${res.title}\n" +
                                "Body:${res.body}\n" +
                                "response code: ${it.code()}"
                    )
                }
            })
        listItems.add(item)
        mAdapter?.notifyDataSetChanged()

        scope.launch {
            mDateBase.itemDao()?.insertItem(item)
        }

        indexOfNewItem += 1
    }

    fun update() {
        startLoadAnimation()
        mService.getPosts().enqueue(mCallback({
            scope.launch {
                mDateBase.itemDao()?.deleteAll()
            }
            emptyBase = true

            listItems.clear()
            mAdapter?.notifyDataSetChanged()
            indexOfNewItem = 0

            getPosts(it)
        }, {
            stopLoadAnimation()
        }))
    }

    private fun stopLoadAnimation() {
        textView?.visibility = View.INVISIBLE
        animatorSet?.end()
    }

    private fun startLoadAnimation() {
        textView?.visibility = View.VISIBLE
        if (animatorSet == null) {
            animatorSet = AnimatorInflater.loadAnimator(this, R.animator.load) as AnimatorSet
            animatorSet!!.setTarget(textView)
        }
        animatorSet!!.start()
    }

    private fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        lateinit var instance: MyApp
        const val EMPTY = "true"
    }
}