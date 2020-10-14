package com.pascal.wisataappfirebase.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import com.bumptech.glide.Glide
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import com.pascal.wisataappfirebase.model.online.WisataFirebase
import kotlinx.android.synthetic.main.activity_detail.*
import kotlin.math.max
import kotlin.math.min

class DetailActivity : AppCompatActivity() {

    private var firebase: WisataFirebase? = null
    private var item: Wisata? = null
    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        initView()
    }
    private fun initView() {
        firebase = intent?.getParcelableExtra("firebase")
        item = intent?.getParcelableExtra("data")

        if (firebase != null) {
            progress_detail.visibility = View.GONE
            Glide.with(this)
                .load(firebase?.image)
                .error(R.drawable.ic_image)
                .into(detail_image)
            detail_name.text = firebase?.name
            detail_description.text = firebase?.description
            detail_description.text = firebase?.location
            detail_lat.text = firebase?.latitude
            detail_lon.text = firebase?.longtitude
        }

        if (item != null) {
            progress_detail.visibility = View.GONE
            detail_name.text = item?.name
            detail_description.text = item?.description
            detail_description.text = item?.location
            detail_lat.text = item?.latitude
            detail_lon.text = item?.longtitude
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            detail_image.scaleX = scaleFactor
            detail_image.scaleY = scaleFactor
            return true
        }
    }
}