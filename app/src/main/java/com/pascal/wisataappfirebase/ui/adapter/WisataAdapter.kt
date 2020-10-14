package com.pascal.wisataappfirebase.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import kotlinx.android.synthetic.main.item_wisata.view.*

class WisataAdapter (
    private val data: List<Wisata>?,
    private val itemClick: OnClickListener
) : RecyclerView.Adapter<WisataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wisata, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position)

        holder.bind(item)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Wisata?) {
            view.item_nama.text = item?.name
            view.item_description.text = item?.description
            view.item_location.text = item?.location

            view.btn_itemUpdate.setOnClickListener{
                itemClick.update(item)
            }

            view.btn_itemDelete.setOnClickListener {
                itemClick.delete(item)
            }

            view.setOnClickListener {
                itemClick.detail(item)
            }
        }
    }

    interface OnClickListener {
        fun update(item: Wisata?)
        fun delete(item: Wisata?)
        fun detail(item: Wisata?)
    }
}


