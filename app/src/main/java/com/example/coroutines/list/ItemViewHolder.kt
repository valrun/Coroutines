package com.example.coroutines.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutines.Item
import com.example.coroutines.MyApp
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.coroutines.CoroutineScope

class ItemViewHolder(private val root: View, private val scope : CoroutineScope) : RecyclerView.ViewHolder(root) {
    fun bind(item: Item) {
        with(root) {
            title.text = item.title
            body.text = item.body
            delete.setOnClickListener {
                MyApp.instance.deleteItem(item.id, scope)
            }
        }
    }
}