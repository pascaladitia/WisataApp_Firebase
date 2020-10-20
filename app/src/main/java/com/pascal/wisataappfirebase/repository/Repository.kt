package com.pascal.wisataappfirebase.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pascal.wisataappfirebase.model.local.DatabaseConfig
import com.pascal.wisataappfirebase.model.local.user.User
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Repository(context: Context) {

    private var databseConfig: DatabaseConfig? = null

    init {
        databseConfig = DatabaseConfig.getInstance(context)
    }

    @SuppressLint("CheckResult")
    fun getWisata(responHandler: (List<Wisata>?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable{ databseConfig?.wisataDao()?.getData()}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responHandler(it)
            }, {
                errorHandler(it)
            })
    }

    @SuppressLint("CheckResult")
    fun insertWisata(item: Wisata, responHandler: (Unit?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable{ databseConfig?.wisataDao()?.insert(item)}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responHandler(it)
            }, {
                errorHandler(it)
            })
    }

    @SuppressLint("CheckResult")
    fun updateWisata(item: Wisata, responHandler: (Unit?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable{ databseConfig?.wisataDao()?.update(item)}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responHandler(it)
            }, {
                errorHandler(it)
            })
    }

    @SuppressLint("CheckResult")
    fun deleteWisata(item: Wisata, responHandler: (Unit?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable{ databseConfig?.wisataDao()?.delete(item)}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responHandler(it)
            }, {
                errorHandler(it)
            })
    }

    //user
    @SuppressLint("CheckResult")
    fun showUser(responHandler: (List<User>?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable { databseConfig?.userDao()?.getData() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let { it1 -> responHandler(it1) }
            }, {
                errorHandler(it)
            })
    }

    @SuppressLint("CheckResult")
    fun addUser(item: User, responHandler: (Unit?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable { databseConfig?.userDao()?.insert(item) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responHandler(it)
            }, {
                errorHandler(it)
            })
    }

    @SuppressLint("CheckResult")
    fun getDataEmail(email: String, responHandler: (User) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable { databseConfig?.userDao()?.getDataEmail(email) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let { it1 -> responHandler(it1) }
                Log.d("TAG", " getUser$it")
            }, {
                errorHandler(it)
                Log.d("TAG", "getUser" + it.message.toString())
            })
    }

    @SuppressLint("CheckResult")
    fun deleteUser(responHandler: (Unit?) -> Unit, errorHandler: (Throwable) -> Unit) {
        Observable.fromCallable { databseConfig?.userDao()?.delete() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responHandler(it)
            }, {
                errorHandler(it)
            })
    }
}