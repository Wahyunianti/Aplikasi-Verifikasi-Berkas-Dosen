package com.example.sitedos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sitedos.admin.AdminActivity
import com.example.sitedos.databinding.ActivityLoginBinding
import com.example.sitedos.dosen.DosenActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class ActivityLogin : AppCompatActivity(), View.OnClickListener {
    lateinit var b : ActivityLoginBinding
    lateinit var db : FirebaseFirestore
    lateinit var SessionManager : SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(this)
        setContentView(b.root)

        b.btLogin.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        if(SessionManager.isLoggedIn()){
            Toast.makeText(this,"Login",Toast.LENGTH_SHORT).show()
            if(SessionManager.getRole() ?: "" == "Admin"){
               AdminAct()
            } else {
                UserAct()
            }
        }
    }

    fun AdminAct(){
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }

    fun UserAct(){
        val intent = Intent(this, DosenActivity::class.java)
        startActivity(intent)
    }

    fun User(username : String, password : String){
        db.collection("te_user")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { user ->
                if (user.size() > 0 ) {
                    for (u in user) {
                        val role = u.get("role").toString()
                        if(role == "Admin"){
                            AdminAct()
                        } else {
                            UserAct()
                        }
                        SessionManager = SessionManager(this)
                        SessionManager.saveLoginDetails(b.edUsername.text.toString(), b.edPassword.text.toString(),role,u.get("id_user").toString())
                        updateToken(u.get("id_user").toString())
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun updateToken(id_user : String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            val hm = HashMap<String,Any>()
            hm.set("token", it.result)
            db.collection("te_user").document(id_user)
                .update(hm)
                .addOnSuccessListener { user ->
                    Log.d("Token updated : ", it.result)
                }
        }
    }


    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btLogin->{
                User(b.edUsername.text.toString(), b.edPassword.text.toString())
            }
        }
    }
}