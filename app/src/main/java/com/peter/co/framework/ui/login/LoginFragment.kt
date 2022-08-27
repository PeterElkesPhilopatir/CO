package com.peter.co.framework.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.peter.co.R
import com.peter.co.databinding.FragmentLoginBinding
import com.peter.co.domain.models.User
import com.peter.co.framework.ui.CopticOrphansViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: CopticOrphansViewModel by activityViewModels()
    private lateinit var callbackManager: CallbackManager

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    private val RC_SIGN_IN = 9001

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        printHashKey()
        setupGoogleSignIn()
        observeViewModel()
        binding.btLoginByGoogle.setOnClickListener {
            googleSignIn()
        }
        callbackManager = CallbackManager.Factory.create()
        binding.btLoginByFacebook.setPermissions(listOf("email"))
        binding.btLoginByFacebook.setFragment(this)

        binding.btLoginByFacebook.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    val graphRequest =
                        GraphRequest.newMeRequest(result.accessToken) { fbresult, response ->
                            Log.i("coptic_orphans_login", fbresult.toString())
                        }
                    val userId = result.accessToken.userId
                    Log.i("coptic_orphans_login", "onSuccess: userId $userId")

                    val bundle = Bundle()
                    bundle.putString("fields", "id, email, first_name, last_name")


                    //Graph API to access the data of user's facebook account
                    val request = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { fbObject, response ->
                        Log.i("coptic_orphans_login", response.toString())

                        //For safety measure enclose the request with try and catch
                        try {
                            Log.i("coptic_orphans_login", "onSuccess: fbObject $fbObject")

                            val name = fbObject?.getString("name")
//                        val gender = fbObject?.getString("gender")

                            val id = fbObject?.getString("id")

//                        Log.i("coptic_orphans_login, "onSuccess: gender $gender")
                            Log.i("coptic_orphans_login", "onSuccess: email ")
                            viewModel.setUser(
                                User(
                                    id = id!!,
                                    name = name!!,
                                    loginMethod = "facebook",
                                    email = ""
                                )
                            )
                            if (findNavController().currentDestination!!.equals(NavDestination("LoginFragment")))
                                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())

                        } //If no data has been retrieve throw some error
                        catch (e: JSONException) {
                            Log.e("coptic_orphans_login", "error ${e.message.toString()}")
                        }

                    }
                    //Set the bundle's data as Graph's object data
                    request.parameters(bundle)

                    //Execute this Graph request asynchronously
                    request.executeAsync()

                }

                override fun onCancel() {
                    Log.i("coptic_orphans_login", "onCancel: called")
                }

                override fun onError(error: FacebookException) {
                    Log.e("coptic_orphans_login", "onError: called")
                }
            })


    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) {
            if (it.id.isNotEmpty())
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }
    }

    private fun setupGoogleSignIn() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context!!, gso)
        // [END config_signin]
        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.i("CO", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("CO", "Google sign in failed", e)
            }
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("CO", "signInWithCredential:success")
                    val user = auth.currentUser
                    viewModel.setUser(
                        User(
                            id = user!!.uid,
                            name = user.displayName!!,
                            loginMethod = user.providerId,
                            email = user.email!!
                        )
                    )

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("CO", "signInWithCredential:failure", task.exception)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun printHashKey() {
        try {
            val info = context!!.packageManager.getPackageInfo(
                "com.peter.co",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

}

private fun GraphRequest.parameters(bundle: Bundle) {
    Log.i("coptic_orphans_login", "bunlde ${bundle.toString()}")

}
