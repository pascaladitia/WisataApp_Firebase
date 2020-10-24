package com.pascal.wisataappfirebase.ui.home.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.viewModel.ViewModelUser
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var viewModel: ViewModelUser

    private var auth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var client: GoogleSignInClient? = null
    private var user: String? = null

    companion object {
        const val NAME = "LOGIN"
        const val LOGIN_SESSION = "login_session"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        user = activity?.intent?.getStringExtra(LOGIN_SESSION)
        viewModel = ViewModelProviders.of(this).get(ViewModelUser::class.java)

        initGmail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        fragHome_logout.setOnClickListener(this)

        initRecent()
        attachObserver()
    }

    private fun attachObserver() {
        viewModel.responseDeleteUser.observe(viewLifecycleOwner, Observer { showDeleteUser(it) })
        viewModel.isError.observe(viewLifecycleOwner, Observer { showError(it) })
    }

    private fun initRecent() {
        val mUser = auth!!.currentUser
        val namaUser = mUser?.displayName

        if (user != null) {
            home_user.text = "Hi $user"
        } else {
            home_user.text = "Hi $namaUser"
        }
    }

    private fun initGmail() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = context?.let { GoogleSignIn.getClient(it, gso) }
    }

    private fun showDeleteUser(it: Unit) {
        Toast.makeText(context, "Anda berhasil logout", Toast.LENGTH_SHORT).show()
        activity?.onBackPressed()
    }

    private fun showError(it: Throwable?) {
        Toast.makeText(context, it?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fragHome_logout -> {
                AlertDialog.Builder(context).apply {
                    setTitle("Logout")
                    setMessage("Anda yakin ingin logout?")
                    setCancelable(false)

                    setPositiveButton("Yes") { dialogInterface, i ->

                        viewModel.deleteUserView()
                        requireActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
                            .putInt(LOGIN_SESSION, 0).commit()

                        client?.signOut()
                        auth?.signOut()
                        navController.navigate(R.id.action_homeFragment_to_loginActivity)
                        activity?.finish()
                    }

                    setNegativeButton("Cancel") { dialogInterface, i ->
                        dialogInterface?.dismiss()
                    }
                }.show()
            }
        }
    }
}