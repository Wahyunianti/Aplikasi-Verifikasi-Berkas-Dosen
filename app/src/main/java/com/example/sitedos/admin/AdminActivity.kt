package com.example.sitedos.admin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.ActivityLogin
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.ActivityAdminBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
NavigationBarView.OnItemSelectedListener {
    lateinit var b : ActivityAdminBinding
    private lateinit var frameHomeAdm : FrameLayout
    lateinit var ftrans : FragmentTransaction
    lateinit var db : FirebaseFirestore
    lateinit var AdminDashboard : AdminDashboard
    lateinit var frag_user : frag_user
    lateinit var frag_dtdosen: frag_datadosen
    lateinit var frag_tbdosen: frag_tambah_dosen
    lateinit var frag_verif: frag_verifikasi
    lateinit var SessionManager : SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(b.root)
        SessionManager = SessionManager(this)
        db = FirebaseFirestore.getInstance()
        if(SessionManager.isLoggedIn()){
            Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show()
        }
        AdminDashboard = AdminDashboard()
        frag_user = frag_user()
        frag_dtdosen = frag_datadosen()
        frag_tbdosen = frag_tambah_dosen()
        frameHomeAdm = b.frameHomeAdm
        frag_verif = frag_verifikasi()

        b.navbar.setOnItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
        frameHomeAdm.visibility = View.VISIBLE
//        ftrans = supportFragmentManager.beginTransaction()
//        ftrans.replace(R.id.frameHomeAdm, frag_tbdosen).commit()
//        frameHomeAdm.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        if (SessionManager.isLoggedIn()) {
//            val username = SessionManager.getUsername() ?: ""
//            val password = SessionManager.getPassword() ?: ""
//            SessionManager.saveLoginDetails(username, password)
//            Toast.makeText(thisParent,"login",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.dashboardAdm -> {
               ftrans = supportFragmentManager.beginTransaction()
               ftrans.replace(R.id.frameHomeAdm, AdminDashboard).commit()
               frameHomeAdm.visibility = View.VISIBLE
           }
           R.id.DataUser -> {
               ftrans = supportFragmentManager.beginTransaction()
               ftrans.replace(R.id.frameHomeAdm, frag_user).commit()
               frameHomeAdm.visibility = View.VISIBLE
           }

           R.id.DataDosen -> {
               ftrans = supportFragmentManager.beginTransaction()
               ftrans.replace(R.id.frameHomeAdm, frag_dtdosen).commit()
               frameHomeAdm.visibility = View.VISIBLE
           }
           R.id.DataApprove -> {
               ftrans = supportFragmentManager.beginTransaction()
               ftrans.replace(R.id.frameHomeAdm, frag_verif).commit()
               frameHomeAdm.visibility = View.VISIBLE
           }
       }
        return true
    }

}