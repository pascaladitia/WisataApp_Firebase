package com.pascal.wisataappfirebase.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.net.toUri
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
import com.pascal.wisataappfirebase.utils.FilePath
import com.pascal.wisataappfirebase.viewModel.ViewModelWisata
import kotlinx.android.synthetic.main.activity_input.*
import kotlinx.android.synthetic.main.dialog_choose_image.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_wisata.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.random.Random

class InputActivity : AppCompatActivity() {

    private lateinit var viewModel : ViewModelWisata
    private var imgPath: Uri? = null
    private var item: Wisata? = null
    private var firebase: WisataFirebase? = null

    private val CAMERA_CODE = 1
    private val GALLERY_CODE = 2

    private var dialog: Dialog? = null

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

        input_image.setOnClickListener {
            showDialog()
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

                        updateFirebaseViewModel()
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

                        insertFirebaseViewModel()
                    }
                }
            }
        }
    }

    private fun insertFirebaseViewModel() {
        var name = input_name.text.toString()
        var description = input_description.text.toString()
        var location = input_location.text.toString()

        imgPath?.let {
            viewModel.insertFirebase(name, description, location, latMaps.toString(), lonMaps.toString(),
                it
            )
        }

        finish()
    }

    private fun updateFirebaseViewModel() {
        var name = input_name.text.toString()
        var description = input_description.text.toString()
        var location = input_location.text.toString()

        imgPath?.let {
            viewModel.updateFirebase(name, description, location, latMaps.toString(), lonMaps.toString(),
                firebase?.key!!, it
            )
        }

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog?.dismiss()
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            resultCamera(data)
        } else if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            resultGallery(data)
        }
    }

    private fun resultGallery(data: Intent?) {
        val image_bitmap = selectFromGalleryResult(data)
        input_image.setImageBitmap(image_bitmap)
    }

    private fun selectFromGalleryResult(data: Intent?): Bitmap {
        var bm: Bitmap? = null
        if (data != null) {
            try {
                imgPath = data.data

                bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bm!!
    }

    private fun resultCamera(data: Intent?) {
        val image = data?.extras?.get("data")
        val random = Random.nextInt(0, 999)
        val name_file = "Camera$random"

        Log.d("data img camera",image.toString())

        val image_bitmap = persistImage(image as Bitmap, name_file)
        //imgPath = Uri.parse(image_bitmap)
        imgPath = Uri.fromFile(File(image_bitmap))

        input_image.setImageBitmap(BitmapFactory.decodeFile(image_bitmap))
    }

    private fun persistImage(bitmap: Bitmap, name: String) :String? {
        val filesDir = filesDir
        val imageFile = File(filesDir, "${name}.png")

        var path = imageFile.path
        val os: OutputStream?
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e("TAG", "persistImage: ${e.message.toString()}", e)
        }
        return path
    }

    private fun showDialog() {
        val window = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_image, null)
        window.setView(view)

        view.dialog_image.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_CODE)
        }

        view.dialog_gallery.setOnClickListener {
            val mimeTypes = arrayOf("image/jpg", "image/jpeg", "image/gif")

            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivityForResult(Intent.createChooser(intent, "Choose Image"), GALLERY_CODE)
        }

        dialog = window.create()
        dialog?.show()
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