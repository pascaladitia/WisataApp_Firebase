package com.pascal.wisataappfirebase.ui.home.fragment

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import com.pascal.wisataappfirebase.model.online.WisataFirebase
import com.pascal.wisataappfirebase.ui.adapter.FirebaseAdapter
import com.pascal.wisataappfirebase.ui.adapter.WisataAdapter
import com.pascal.wisataappfirebase.viewModel.ViewModelWisata
import kotlinx.android.synthetic.main.fragment_wisata.*


class WisataFragment : Fragment() {

    private lateinit var viewModel: ViewModelWisata
    private lateinit var navController: NavController
    private var reference: DatabaseReference? = null
    private var db: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wisata, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance()
        reference = db?.getReference("images")

        viewModel = ViewModelProviders.of(this).get(ViewModelWisata::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        checkConnection()
        initButton()
    }

    private fun checkConnection() {
        val connMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo

        if (networkInfo != null && networkInfo!!.isConnected()) {
            getFirebase()
        } else {
            attachObserve()
            viewModel.getWisataView()
        }
    }

    private fun initButton() {
        btn_add.setOnClickListener {
            navController.navigate(R.id.action_wisataFragment_to_mapsActivity)
        }
    }

    private fun getFirebase() {
        progress_wisata.visibility = View.GONE
        val dataWisata = ArrayList<WisataFirebase>()

        reference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(snapshot: DatabaseError) {
                Toast.makeText(context, snapshot.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (datas in snapshot.children) {
                    val key = datas.key
                    val image = datas.child("image").value.toString()
                    val nama = datas.child("name").value.toString()
                    val description = datas.child("description").value.toString()
                    val location = datas.child("location").value.toString()
                    val lat = datas.child("lat").value.toString()
                    val lon = datas.child("lon").value.toString()

                    val wisata = WisataFirebase(image, nama, description, location, lat, lon, key)
                    dataWisata.add(wisata)
                    showData(dataWisata)
                }
            }

            private fun showData(dataWisata: ArrayList<WisataFirebase>) {
                recycler_wisata.adapter = FirebaseAdapter(dataWisata, object : FirebaseAdapter.OnClickListener {
                        override fun update(item: WisataFirebase?) {
                            val bundle = bundleOf("firebase" to item)
                            navController.navigate(R.id.action_wisataFragment_to_mapsActivity, bundle)

                            attachObserve()
                        }

                        override fun delete(item: WisataFirebase?) {
                            AlertDialog.Builder(context).apply {
                                setTitle("Hapus")
                                setMessage("Yakin Hapus Siswa?")
                                setCancelable(false)

                                setPositiveButton("Yes") { dialogInterface, i ->
                                    reference?.child(item?.key ?: "")?.removeValue()
                                    checkConnection()
                                }
                                setNegativeButton("Cancel") { dialogInterface, i ->
                                    dialogInterface?.dismiss()
                                }
                            }.show()
                        }

                        override fun detail(item: WisataFirebase?) {
                            val bundle = bundleOf("firebase" to item)
                            navController.navigate(R.id.action_wisataFragment_to_detailActivity, bundle)
                        }

                    })
            }
        })
    }

    private fun attachObserve() {
        viewModel.responGet.observe(viewLifecycleOwner, Observer { showSiswaView(it) })
        viewModel.isError.observe(viewLifecycleOwner, Observer { showError(it) })
        viewModel.responDelete.observe(viewLifecycleOwner, Observer { showDeleteSiswa(it) })
    }

    private fun showSiswaView(it: List<Wisata>?) {
        progress_wisata.visibility = View.GONE
        recycler_wisata.adapter = WisataAdapter(it, object : WisataAdapter.OnClickListener {

            override fun update(item: Wisata?) {
                val bundle = bundleOf("data" to item)
                navController.navigate(R.id.action_wisataFragment_to_mapsActivity, bundle)
            }

            override fun delete(item: Wisata?) {
                AlertDialog.Builder(context).apply {
                    setTitle("Hapus")
                    setMessage("Yakin Hapus Siswa?")
                    setCancelable(false)

                    setPositiveButton("Yes") { dialogInterface, i ->
                        viewModel.deleteWisataView(item!!)
                    }
                    setNegativeButton("Cancel") { dialogInterface, i ->
                        dialogInterface?.dismiss()
                    }
                }.show()
            }

            override fun detail(item: Wisata?) {
                val bundle = bundleOf("data" to item)
                navController.navigate(R.id.action_wisataFragment_to_detailActivity, bundle)
            }

        })
    }

    private fun showError(it: Throwable?) {
        progress_wisata.visibility = View.GONE
        Toast.makeText(context, it?.message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteSiswa(it: Unit?) {
        progress_wisata.visibility = View.GONE
        Toast.makeText(context, "Siswa berhasil dihapus", Toast.LENGTH_SHORT).show()
        viewModel.getWisataView()
    }

    override fun onResume() {
        super.onResume()
        checkConnection()
    }
}