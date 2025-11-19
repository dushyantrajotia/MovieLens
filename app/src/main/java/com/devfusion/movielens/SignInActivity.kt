package com.devfusion.movielens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// If you plan to re-enable Google Sign-In later, you can re-add those imports and code.

private const val TAG = "ML-DEBUG"

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Make these nullable; we'll check for null and give a clear error if the view id is missing.
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var signInButton: Button? = null
    private var goToSignUpText: TextView? = null
    // private var googleSignInButton: SignInButton? = null // optional for later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "SignInActivity onCreate START")
        try {
            setContentView(R.layout.sign_in)
        } catch (t: Throwable) {
            // If layout inflation fails, show and log it
            Toast.makeText(this, "Failed to inflate sign_in layout: ${t.message}", Toast.LENGTH_LONG).show()
            Log.e(TAG, "setContentView failed", t)
            throw t
        }
        Log.i(TAG, "SignInActivity setContentView OK")

        auth = FirebaseAuth.getInstance()

        // find views safely
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        goToSignUpText = findViewById(R.id.goToSignUp)
        // googleSignInButton = findViewById(R.id.googleSignInButton)

        // Validate that all required views exist
        if (emailEditText == null) {
            val msg = "emailEditText (R.id.emailEditText) not found in sign_in.xml"
            Log.e(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        if (passwordEditText == null) {
            val msg = "passwordEditText (R.id.passwordEditText) not found in sign_in.xml"
            Log.e(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        if (signInButton == null) {
            val msg = "signInButton (R.id.signInButton) not found in sign_in.xml"
            Log.e(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        if (goToSignUpText == null) {
            val msg = "goToSignUp (R.id.goToSignUp) not found in sign_in.xml"
            Log.e(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

        // Wire up actions (guarded)
        signInButton?.setOnClickListener {
            Log.i(TAG, "SignIn button clicked")
            signInWithEmail()
        }

        goToSignUpText?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Leave Google sign-in out for now. Re-enable after email sign-in works.
        Log.i(TAG, "SignInActivity onCreate END")
    }

    private fun signInWithEmail() {
        val email = emailEditText?.text?.toString()?.trim() ?: ""
        val password = passwordEditText?.text?.toString()?.trim() ?: ""

        Log.i(TAG, "Attempt signInWithEmail: email='${if (email.isNotEmpty()) email else "<empty>"}'")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Defensive: wrap the sign-in call in try/catch to catch unexpected runtime issues
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.i(TAG, "signInWithEmail addOnComplete: success=${task.isSuccessful}")
                    if (task.isSuccessful) {
                        // success: navigate to MainActivity (clear auth screens)
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
                    // catch network errors or other exceptions
                    Log.e(TAG, "signInWithEmail encountered failure", ex)
                    Toast.makeText(this, "Sign-in error: ${ex.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        } catch (t: Throwable) {
            Log.e(TAG, "Exception when calling signInWithEmailAndPassword", t)
            Toast.makeText(this, "Unexpected error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Optional: override onActivityResult if you later re-enable Google Sign-In.
}
