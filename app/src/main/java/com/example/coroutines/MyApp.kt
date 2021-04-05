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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MyApp : Application() {
    private lateinit var mRetrofit: Retrofit
    private lateinit var mService: ServiceInterface
    var mAdapter: ItemAdapter? = null

    private lateinit var mDateBase: AppDatabase
    private var mSharedPreference: SharedPreferences? = null
    private var emptyBase: Boolean = true

    var listItems: MutableList<Item> = mutableListOf()
    private var indexOfNewItem = 0

    var textView: TextView? = null
    private var animatorSet: AnimatorSet? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        mRetrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mService = mRetrofit.create(ServiceInterface::class.java)

        mSharedPreference = getSharedPreferences("take", Context.MODE_PRIVATE)
        emptyBase = this.mSharedPreference?.getBoolean(EMPTY, true) ?: true
        mDateBase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        ).build()
    }

    fun loadItems() {
        startLoadAnimation()

        if (emptyBase) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    var result = arrayListOf<Item>()

                    try {
                        result = mService.getPosts()
                    } catch (e: IOException) {
                        withContext(Dispatchers.Main) {
                            e.message?.let { toast(it) }
                        }
                    }

                    listItems.addAll(result)
                    withContext(Dispatchers.Main) {
                        mAdapter?.notifyDataSetChanged()
                    }
                    listItems.forEach {
                        if (indexOfNewItem < it.id + 1) {
                            indexOfNewItem = it.id + 1
                        }
                    }
                    mDateBase.itemDao()?.insertItems(listItems)
                    emptyBase = false
                    mSharedPreference?.edit()?.apply {
                        this.putBoolean(EMPTY, emptyBase)
                        apply()
                    }
                }
            } catch (e: CancellationException) {
                toast("something wrong :\n${e.message}")
            } finally {
                stopLoadAnimation()
            }
        } else {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val data = mDateBase.itemDao()?.getItems() as MutableList<Item>
                    data.forEach { listItems.add(it) }
                    listItems.forEach {
                        if (indexOfNewItem < it.id + 1) {
                            indexOfNewItem = it.id + 1
                        }
                    }
                    withContext(Dispatchers.Main) {
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            } catch (e: CancellationException) {
                toast("something wrong :\n${e.message}")
            } finally {
                stopLoadAnimation()
            }
        }
    }

    fun deleteItem(id: Int) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    mService.deletePost(id)
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        e.message?.let { toast(it) }
                    }
                }

                var index = 0
                for (post in listItems) {
                    if (listItems[index].id == id) {
                        break
                    }
                    index++
                }
                mDateBase.itemDao()?.deleteByID(index)
                listItems.removeAt(index)
                withContext(Dispatchers.Main) {
                    mAdapter?.notifyDataSetChanged()
                }
            }
        } catch (e: CancellationException) {
            toast("something wrong :\n${e.message}")
        } finally {
            stopLoadAnimation()
        }
    }

    fun addItem(title: String, text: String) {
        val item = Item(indexOfNewItem, title, text, 0)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    mService.addPost(item)
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        e.message?.let { toast(it) }
                    }
                }

                listItems.add(item)
                mDateBase.itemDao()?.insertItem(item)
                withContext(Dispatchers.Main) {
                    mAdapter?.notifyDataSetChanged()
                }
                indexOfNewItem += 1
            }
        } catch (e: CancellationException) {
            toast("something wrong :\n${e.message}")
        } finally {
            stopLoadAnimation()
        }
    }

    fun update() {
        startLoadAnimation()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                var result = arrayListOf<Item>()

                try {
                    result = mService.getPosts()
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        e.message?.let { toast(it) }
                    }
                    return@launch
                }

                listItems.clear()
                listItems.addAll(result)
                withContext(Dispatchers.Main) {
                    mAdapter?.notifyDataSetChanged()
                }

                mDateBase.itemDao()?.deleteAll()
                mDateBase.itemDao()?.insertItems(listItems)
                emptyBase = false
                mSharedPreference?.edit()?.apply {
                    this.putBoolean(EMPTY, emptyBase)
                    apply()
                }
            }
        } catch (e: CancellationException) {
            toast("something wrong :\n${e.message}")
        } finally {
            stopLoadAnimation()
        }
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