package com.pascal.wisataappfirebase.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.local.user.User
import com.pascal.wisataappfirebase.ui.home.HomeActivity
import com.pascal.wisataappfirebase.ui.main.MainActivity
import com.pascal.wisataappfirebase.viewModel.ViewModelUser
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var viewModel: ViewModelUser
    private lateinit var sharePref: SharedPreferences

    private var auth: FirebaseAuth? = null
    private var mGoogleSignInCLient: GoogleSignInClient? = null

    companion object {
        const val NAME = "LOGIN"
        const val LOGIN_SESSION = "login_session"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ViewModelUser::class.java)
        viewModel.showUserView()

        sharePref = requireActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        attachBtn()

        viewModel.responShowUser.observe(viewLifecycleOwner, Observer { showData(it) })
        viewModel.responEmail.observe(viewLifecycleOwner, Observer { showLogin(it) })
        viewModel.isError.observe(viewLifecycleOwner, Observer { showError(it) })

        initFirebase()
        initGmail()
    }

    private fun showData(it: List<User>?) {
        if (sharePref.getInt(LOGIN_SESSION, 0) == 1) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(LOGIN_SESSION, it?.get(0)?.nama)
            startActivity(intent)
            activity?.finish()
        } else {
            Toast.makeText(context, "Login Here", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError(it: Throwable?) {
        Toast.makeText(context, "email atau password salah", Toast.LENGTH_SHORT).show()
    }

    private fun showLogin(it: User) {
        Toast.makeText(context, "Selamat Datang", Toast.LENGTH_SHORT).show()

        val email = main_email.text.toString()
        val password = main_password.text.toString()

        if (password == it.password) {
            sharePref.edit().putInt(LOGIN_SESSION, 1).commit()
            Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()

            val bundle = bundleOf(LOGIN_SESSION to it.nama)
            navController.navigate(R.id.action_loginFragment_to_homeActivity, bundle)

            requireActivity().finish()

        } else {
            Toast.makeText(context, "email & password salah", Toast.LENGTH_SHORT).show()
        }


    }

    private fun attachBtn() {
        btn_login.setOnClickListener {
            val email = main_email.text.toString()
            val password = main_password.text.toString()

            loginValidation(email, password)
        }

        btn_register.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        login_google.setOnClickListener {
            val signIn = mGoogleSignInCLient?.signInIntent
            startActivityForResult(signIn, 1)
        }
    }

    private fun loginValidation(email: String, password: String) {

        Log.d("TAG", "lloginValidation $email dan $password")

        if (email.isNotEmpty()) {
            Log.d("TAG", "lloginValidation2 $email dan $password")

            viewModel.getDataEmail(email).observe(viewLifecycleOwner, Observer { user ->
                Log.d("TAG", "lloginValidation3 $email dan $password")
                if (password == user.password) {
                    Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "email & password salah", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "email & password tidak boleh kosong", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun initGmail() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInCLient = context?.let { GoogleSignIn.getClient(it, gso) }
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()

        if (auth?.currentUser?.email?.isNotEmpty() ?: false) {
            startActivity(Intent(context, HomeActivity::class.java))
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            // handleSignInResult(task)
            firebaseAuthWithGoogle(account.idToken ?: "")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(Activity(),
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success")
                        val user = auth?.currentUser
                        startActivity(Intent(context, HomeActivity::class.java))
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                        Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}