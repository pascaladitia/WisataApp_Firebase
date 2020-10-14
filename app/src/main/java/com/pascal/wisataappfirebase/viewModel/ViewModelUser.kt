package com.pascal.wisataappfirebase.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pascal.wisataappfirebase.model.local.user.User
import com.pascal.wisataappfirebase.repository.Repository
import io.reactivex.Single

class ViewModelUser(application: Application) : AndroidViewModel(application) {

    val repository = Repository(application.applicationContext)

    var responShowUser = MutableLiveData<List<User>?>()
    var isError = MutableLiveData<Throwable>()
    var responAddUser = MutableLiveData<Unit>()
    var responEmail = MutableLiveData<User>()
    var responseDeleteUser = MutableLiveData<Unit>()

    fun showUserView() {
        repository.showUser({
            responShowUser.value = it
        }, {
            isError.value = it
        })
    }

    fun addUserView(item: User) {
        repository.addUser(item, {
            responAddUser.value = it
        }, {
            isError.value = it
        })
    }

    fun getDataEmail(email: String): LiveData<User> {

        val user = MutableLiveData<User>()

        repository.getDataEmail(email, {
            Log.d("TAG", "lloginValidation: ViewModel getDataEmail  $it.")

            responEmail.postValue(it)
        }, {
            isError.postValue(it)
        })

        return user
    }

        fun deleteUserView() {
            repository.deleteUser( {
                responseDeleteUser.value = it
            }, {
                isError.value = it
            })
        }
    }
