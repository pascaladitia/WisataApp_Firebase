package com.pascal.wisataappfirebase.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import com.pascal.wisataappfirebase.model.online.WisataFirebase
import com.pascal.wisataappfirebase.viewModel.ViewModelWisata
import kotlinx.android.synthetic.main.activity_input.*
import kotlinx.android.synthetic.main.item_wisata.view.*

class InputActivity : AppCompatActivity() {

    private lateinit var viewModel : ViewModelWisata
    private lateinit var navController : NavController
    private var imgPath: Uri? = null
    private var item: Wisata? = null
    private var firebase: WisataFirebase? = null

    var latMaps : String? = null
    var lonMaps : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        viewModel = ViewModelProviders.of(this).get(ViewModelWisata::class.java)

        getParcel()
        attachObserve()
        initView()
        initBtn()
    }

    private fun getParcel() {
        item = intent?.getParcelableExtra("data")
        firebase = intent?.getParcelableExtra("firebase")

        latMaps = intent?.getStringExtra("lat")
        lonMaps = intent?.getStringExtra("lon")
    }

    private fun initBtn() {
        input_maps.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
            finish()
        }

        btn_upload.setOnClickListener {
            val iImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(iImg, 0)
        }
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
        }

        if (firebase != null) {
            input_name.setText(firebase?.name)
            input_description.setText(firebase?.description)
            input_location.setText(firebase?.location)
            Glide.with(this)
                .load(firebase?.image)
                .apply(
                    RequestOptions()
                        .override(200,200)
                        .error(R.drawable.ic_image))
                .into(input_image)

            btn_Input.text = "Update"
        }

        if (item != null) {
            input_name.setText(item?.name)
            input_description.setText(item?.description)
            input_location.setText(item?.location)

            btn_Input.text = "Update"
        }

        if (latMaps != null) {
            input_latitude.text = latMaps
            input_longtitude.text = lonMaps
        }

        inputData()
    }

    private fun inputData() {
        when (btn_Input.text) {
            "Update" -> {
                btn_Input.setOnClickListener {
                    var name = input_name.text.toString()
                    var description = input_description.text.toString()
                    var location = input_location.text.toString()

                    if (name.isEmpty()) {
                        input_name.error = "Nama tidak boleh kosong"
                    } else if (description.isEmpty()) {
                        input_description.error = "Deskripsi tidak boleh kosong"
                    } else if (location.isEmpty()) {
                        input_location.error = "Location tidak boleh kosong"
                    } else {

                        viewModel.updateWisataView(
                            Wisata(
                                item?.id, name, description, location, latMaps, lonMaps
                            )
                        )

                        updateFirebase()
                    }
                }
            }

            else -> {
                btn_Input.setOnClickListener {
                    var name = input_name.text.toString()
                    var description = input_description.text.toString()
                    var location = input_location.text.toString()

                    if (name.isEmpty()) {
                        input_name.error = "Nama tidak boleh kosong"
                    } else if (description.isEmpty()) {
                        input_description.error = "Deskripsi tidak boleh kosong"
                    } else if (location.isEmpty()) {
                        input_location.error = "Location tidak boleh kosong"
                    } else {
                        viewModel.insertWisataView(
                            Wisata(
                                null, name, description, location, latMaps, lonMaps)
                        )

                        getFirebase()
                    }
                }
            }
        }
    }

    private fun getFirebase() {
        val storageRef = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images").push()

        var name = input_name.text.toString()
        var description = input_description.text.toString()
        var location = input_location.text.toString()

        storageRef.putFile(imgPath!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    databaseRef.child("image").setValue(it.toString())
                    databaseRef.child("name").setValue(name)
                    databaseRef.child("description").setValue(description)
                    databaseRef.child("location").setValue(location)
                    databaseRef.child("lat").setValue(latMaps)
                    databaseRef.child("lon").setValue(lonMaps)

                    finish()
                }
            }
            .addOnFailureListener{
                println("Info File : ${it.message}")
            }
    }

    private fun updateFirebase() {
        val storageRef = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images")

        var name = input_name.text.toString()
        var description = input_description.text.toString()
        var location = input_location.text.toString()

        storageRef.putFile(imgPath!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    val wisata = WisataFirebase(it.toString(), name, description, location, latMaps, lonMaps)
                    databaseRef.child(firebase?.key ?: "").setValue(wisata)

                    finish()
                }
            }
            .addOnFailureListener{
                println("Info File : ${it.message}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imgPath = data?.data
        }
    }

    private fun attachObserve() {
        viewModel.isError.observe(this, Observer { showError(it) })
        viewModel.responInsert.observe(this, Observer { showAddNote(it) })
        viewModel.responUpdate.observe(this, Observer { showUpdateNote(it) })
    }

    private fun showError(it: Throwable?) {
        showToast(it?.message.toString())
    }

    private fun showAddNote(it: Unit) {
        showToast("Data berhasil disimpan")
        finish()
    }

    private fun showUpdateNote(it: Unit?) {
        showToast("Data berhasil diupdate")
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
}