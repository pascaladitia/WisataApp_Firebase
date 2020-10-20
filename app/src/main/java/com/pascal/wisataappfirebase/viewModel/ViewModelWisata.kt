package com.pascal.wisataappfirebase.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import com.pascal.wisataappfirebase.model.online.WisataFirebase
import com.pascal.wisataappfirebase.repository.Repository

class ViewModelWisata(application: Application): AndroidViewModel(application) {

    val repository = Repository(application.applicationContext)

    var responGet = MutableLiveData<List<Wisata>?>()
    var responInsert = MutableLiveData<Unit>()
    var responUpdate = MutableLiveData<Unit>()
    var responDelete = MutableLiveData<Unit>()
    var isError = MutableLiveData<Throwable>()

    fun getWisataView() {
        repository.getWisata({
            responGet.value = it
        }, {
            isError.value = it
        })
    }

    fun insertWisataView(item: Wisata) {
        repository.insertWisata(item, {
            responInsert.value = it
        }, {
            isError.value = it
        })
    }

    fun updateWisataView(item: Wisata) {
        repository.updateWisata(item, {
            responUpdate.value = it
        }, {
            isError.value = it
        })
    }

    fun deleteWisataView(item: Wisata) {
        repository.deleteWisata(item, {
            responDelete.value = it
        }, {
            isError.value = it
        })
    }

    fun insertFirebase(name: String, description: String, location: String, latMaps: String, lonMaps: String, imgPath: Uri) {
        val storageRef = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images").push()

        storageRef.putFile(imgPath!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    databaseRef.child("image").setValue(it.toString())
                    databaseRef.child("name").setValue(name)
                    databaseRef.child("description").setValue(description)
                    databaseRef.child("location").setValue(location)
                    databaseRef.child("lat").setValue(latMaps)
                    databaseRef.child("lon").setValue(lonMaps)
                }
            }
            .addOnFailureListener{
                println("Info File : ${it.message}")
            }
    }

    fun updateFirebase(name: String, description: String, location: String, lat: String, lon: String, key: String, imgPath: Uri) {
        val storageRef = FirebaseStorage.getInstance().getReference("images")
        val databaseRef = FirebaseDatabase.getInstance().getReference("images")

        storageRef.putFile(imgPath!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    val wisata = WisataFirebase(it.toString(), name, description, location, lat, lon)
                    databaseRef.child(key ?: "").setValue(wisata)
                }
            }
            .addOnFailureListener{
                println("Info File : ${it.message}")
            }
    }
}