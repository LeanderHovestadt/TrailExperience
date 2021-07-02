package com.example.android.trailexperience.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.android.trailexperience.R
import com.example.android.trailexperience.databinding.ActivityAuthenticationBinding
import com.example.android.trailexperience.tours.ToursActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate called")

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { launchSignInFlow() }

        launcher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
                ) { res ->
            this.onSignInResult(res)
        }
    }

    private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult?) {
        Timber.i("onSignInResult called")

        val response = res?.idpResponse
        if (res?.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Timber.i(
                "Successfully signed in user ${user?.displayName}")

            Toast.makeText(this, "Welcome ${user?.displayName} :)", Toast.LENGTH_LONG).show()
            updateUI(user)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button.
            Timber.i("Sign in unsuccessful ${res?.idpResponse?.error?.errorCode}")
            Toast.makeText(this, "Sign in unsuccessful.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null)
        {
            launchToursActivity()
        }
    }

    private fun launchSignInFlow() {
        Timber.d("launchSignInFlow called with API_KEY ${getString(R.string.google_maps_key)}")

        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_logo_mountains_256) // Set logo drawable
            .setTheme(R.style.Theme_TrailExperience) // Set theme
            .build()

        // Create and launch sign-in intent.
        Timber.i("launching intent")
        launcher.launch(signInIntent)
    }

    private fun launchToursActivity(){
        Timber.i("launching Tours Activity")
        val intent = Intent(this, ToursActivity::class.java)
        startActivity(intent)
        finish()
    }
}
