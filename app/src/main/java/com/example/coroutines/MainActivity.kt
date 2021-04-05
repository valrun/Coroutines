package com.example.coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutines.MyApp.Companion.instance
import com.example.coroutines.list.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val scope = lifecycle.coroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.myRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ItemAdapter(instance.listItems, scope)

        instance.mAdapter = recyclerView.adapter as ItemAdapter?
        instance.textView = findViewById(R.id.load)
        instance.loadItems(scope)

        enter.setOnClickListener {
            instance.addItem(
                    titleText.text.toString(),
                    editText.text.toString(),
                scope
            )
            titleText.setText("")
            editText.setText("")
        }
        update.setOnClickListener {
            instance.update(scope)
        }
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

