package com.devfusion.movielens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "ML-DEBUG"

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var googleSignInButton: LinearLayout // CHANGED from ConstraintLayout
    private lateinit var goToSignUpText: TextView
    private lateinit var forgotPasswordText: TextView

    private val RC_SIGN_IN = 9002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "SignInActivity onCreate START")
        try {
            setContentView(R.layout.sign_in)
        } catch (t: Throwable) {
            Toast.makeText(this, "Failed to inflate sign_in layout: ${t.message}", Toast.LENGTH_LONG).show()
            Log.e(TAG, "setContentView failed", t)
            throw t
        }
        Log.i(TAG, "SignInActivity setContentView OK")

        auth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        googleSignInButton = findViewById(R.id.googleSignInButton)
        goToSignUpText = findViewById(R.id.goToSignUp)
        forgotPasswordText = findViewById(R.id.forgotPassword)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up click listeners
        signInButton.setOnClickListener {
            Log.i(TAG, "SignIn button clicked")
            signInWithEmail()
        }

        googleSignInButton.setOnClickListener {
            Log.i(TAG, "Google SignIn button clicked")
            signInWithGoogle()
        }

        goToSignUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        forgotPasswordText.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        Log.i(TAG, "SignInActivity onCreate END")
    }

    private fun signInWithEmail() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        Log.i(TAG, "Attempt signInWithEmail: email='${if (email.isNotEmpty()) email else "<empty>"}'")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.i(TAG, "signInWithEmail addOnComplete: success=${task.isSuccessful}")
                    if (task.isSuccessful) {
                        Log.i(TAG, "Firebase sign-in successful, launching MainActivity")
                        startActivity(Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    } else {
                        val msg = task.exception?.message ?: "Unknown sign-in error"
                        Log.w(TAG, "signIn failed: $msg", task.exception)
                        Toast.makeText(this, "Authentication failed: $msg", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "signInWithEmail encountered failure", ex)
                    Toast.makeText(this, "Sign-in error: ${ex.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception when calling signInWithEmailAndPassword", t)
            Toast.makeText(this, "Unexpected error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign-in failed", e)
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    Log.e(TAG, "Firebase authentication failed", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}