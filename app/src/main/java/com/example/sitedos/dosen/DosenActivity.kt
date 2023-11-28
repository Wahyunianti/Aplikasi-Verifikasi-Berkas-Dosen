package com.example.sitedos.dosen

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
import com.example.sitedos.admin.AdminDashboard
import com.example.sitedos.admin.frag_datadosen
import com.example.sitedos.admin.frag_user
import com.example.sitedos.admin.frag_verifikasi
import com.example.sitedos.databinding.ActivityDosenBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DosenActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NavigationBarView.OnItemSelectedListener {
    lateinit var b : ActivityDosenBinding
    private lateinit var frameHomeDsn : FrameLayout
    lateinit var ftrans : FragmentTransaction
    lateinit var db : FirebaseFirestore
    lateinit var DosenDashboard : DosenDashboard
    lateinit var frag_berkas : frag_berkas
    lateinit var frag_verif: frag_verifikasidsn
    lateinit var SessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(b.root)
        SessionManager = SessionManager(this)
        db = FirebaseFirestore.getInstance()
        if(SessionManager.isLoggedIn()){
            Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show()
        }
        DosenDashboard = DosenDashboard()
        frag_berkas = frag_berkas()
        frameHomeDsn = b.frameHomeDsn
        frag_verif = frag_verifikasidsn()

        b.navbar2.setOnItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
//        ftrans = supportFragmentManager.beginTransaction()
//        ftrans.replace(R.id.frameHomeDsn, DosenDashboard).commit()
        frameHomeDsn.visibility = View.VISIBLE
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
            R.id.dashboardDsn -> {
                ftrans = supportFragmentManager.beginTransaction()
                ftrans.replace(R.id.frameHomeDsn, DosenDashboard).commit()
                frameHomeDsn.visibility = View.VISIBLE
            }
            R.id.DataBerkas -> {
                ftrans = supportFragmentManager.beginTransaction()
                ftrans.replace(R.id.frameHomeDsn, frag_berkas).commit()
                frameHomeDsn.visibility = View.VISIBLE
            }

            R.id.DataApprove -> {
                ftrans = supportFragmentManager.beginTransaction()
                ftrans.replace(R.id.frameHomeDsn, frag_verif).commit()
                frameHomeDsn.visibility = View.VISIBLE
            }
        }
        return true
    }

}