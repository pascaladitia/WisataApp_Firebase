package com.pascal.wisataappfirebase.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.online.WisataFirebase
import kotlinx.android.synthetic.main.item_wisata.view.*

class FirebaseAdapter(private val data: ArrayList<WisataFirebase>?,
                      private val itemClick: OnClickListener
) : RecyclerView.Adapter<FirebaseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebaseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wisata, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position)

        holder.bind(item)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: WisataFirebase?) {
            view.item_nama.text = item?.name
            view.item_description.text = item?.description
            view.item_location.text = item?.location

            Glide.with(itemView.context)
                .load(item?.image)
                .apply(
                    RequestOptions()
                    .override(200,200)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH))
                .into(itemView.item_image)

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
        fun update(item: WisataFirebase?)
        fun delete(item: WisataFirebase?)
        fun detail(item: WisataFirebase?)
    }
}