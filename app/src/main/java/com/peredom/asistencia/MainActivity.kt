package com.peredom.asistencia

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    lateinit var txtBienvenida: TextView
    lateinit var imgUser: ImageView
    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtId: TextView
    lateinit var btnLogout: Button
    lateinit var btnRevoke: Button

    lateinit var googleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtBienvenida = txt_bienvenida_app
        imgUser = img_user
        txtName = name_user
        txtEmail = email_user
        txtId = id_user
        btnLogout = btn_log_out
        btnRevoke = btn_revoke
        //////////////////////////////////////////
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        //////////////////////////////////////////
        setUpActions()
    }

    override fun onStart() {
        super.onStart()
        val opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            val result = opr.get()
            handleSignInResult(result)
        } else {
            opr.setResultCallback {
                handleSignInResult(it)
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) { }

    fun setUpActions() {
        btnLogout.setOnClickListener {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback { status ->
                if (status.isSuccess) {
                    goLogInScreen()
                } else {
                    Toast.makeText(this, "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRevoke.setOnClickListener {
            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback { status ->
                if (status.isSuccess) {
                    goLogInScreen()
                } else {
                    Toast.makeText(this, "No revoke", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            txtName.text = account?.displayName
            txtEmail.text = account?.email
            txtId.text = account?.id
            Picasso.get().load(account?.photoUrl.toString()).into(imgUser)
        } else {
            goLogInScreen()
        }
    }

    private fun goLogInScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent )
    }
}
