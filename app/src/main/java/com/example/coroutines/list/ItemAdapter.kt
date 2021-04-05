package com.example.coroutines.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutines.Item
import com.example.coroutines.R
import kotlinx.coroutines.CoroutineScope

class ItemAdapter(
    private val items: List<Item>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false),
            scope
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(items[position])
}