package com.vadym.birthday.ui.gift

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vadym.birthday.R

class GiftMenuAdapter(
    private val context: Context,
    private val itemList: List<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<GiftMenuAdapter.GiftMenuViewHolder>() {

    private var selectedPosition = -1  // Track selected item

    inner class GiftMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.menuImage)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position
                    notifyDataSetChanged()
                    onItemClick(itemList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gift_menu, parent, false)
        return GiftMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: GiftMenuViewHolder, position: Int) {
        val imageResId = itemList[position]
        Glide.with(context).load(imageResId).into(holder.imageView)
    }

    override fun getItemCount(): Int = itemList.size
}