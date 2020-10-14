package com.pascal.wisataappfirebase.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
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
}