package com.example.coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutines.MyApp.Companion.instance
import com.example.coroutines.list.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.myRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ItemAdapter(instance.listItems)

        instance.mAdapter = recyclerView.adapter as ItemAdapter?
        instance.textView = findViewById(R.id.load)
        instance.loadItems()

        enter.setOnClickListener {
            instance.addItem(
                    titleText.text.toString(),
                    editText.text.toString(),
            )
            titleText.setText("")
            editText.setText("")
        }
        update.setOnClickListener {
            instance.update()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("editText", editText.text.toString())
        outState.putString("editText", titleText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editText.text.insert(0, savedInstanceState.getString("editText"))
        titleText.text.insert(0, savedInstanceState.getString("editText"))
    }
}

