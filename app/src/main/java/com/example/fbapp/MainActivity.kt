package com.example.fbapp

import android.accounts.Account
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private val callbackManager by lazy {
        CallbackManager.Factory.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        facebookLogin()

        ivFacebook.setOnClickListener {
            facebookCircle()
        }

        btnGoogle.setOnClickListener {
            loginGoogle()
        }
    }

    private fun facebookCircle() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
    }

//    fun check() {
//        val accessToken = AccessToken.getCurrentAccessToken()
//        val isLoggedIn = accessToken != null && !accessToken.isExpired
//
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//    }

    private fun facebookLogin() {

        btnFacebook.setReadPermissions("email");
        btnFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.e("Facebook Token", loginResult?.accessToken?.token.toString())
            }

            override fun onCancel() {
                Log.e("Login", "OnCancel")
            }

            override fun onError(exception: FacebookException) {
                Log.e("Login", "OnError -> $exception")
            }
        })
    }

    private fun loginGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FACEBOOK) {
            Log.e("ActivityResultFacebook", "resultCode -> $resultCode")
        }

        if (requestCode == GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.e("ActivityResultGoogle", "resultCode -> $resultCode")
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
//            Log.e("Google", "loginGoogle() -> $account")
            // Signed in successfully, show authenticated UI.
//            updateUI(account)
            getUserInfo()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
    }

    private fun getUserInfo() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)

        CoroutineScope(IO).launch {
            val scope = "oauth2:profile email"
            val accountDetails = Account("yberezhnyi@gmail.com", GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
            val token = GoogleAuthUtil.getToken(this@MainActivity, accountDetails, scope)
            Log.e("Google token", token)
        }



        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val serverAuthCode = acct.serverAuthCode
            Log.e(
                "GoogleAccountDetail", "personName -> $personName," +
                        " personGivenName ->$personGivenName," +
                        "personId -> $personId," +
                        "personEmail -> $personEmail," +
                        "account -> $serverAuthCode," +
                        "personFamilyName -> $personFamilyName"
            )


        }

    }

    companion object {
        private const val GOOGLE = 1
        private const val FACEBOOK = 2
    }

}