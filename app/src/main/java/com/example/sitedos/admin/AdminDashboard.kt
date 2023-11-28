package com.example.sitedos.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.example.sitedos.ActivityLogin
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentDashboardadmBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdminDashboard : Fragment(){
    lateinit var thisParent : AdminActivity
    private lateinit var b : FragmentDashboardadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var bundle: Bundle
    lateinit var SessionManager : SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentDashboardadmBinding.inflate(inflater, container, false)
        SessionManager = SessionManager(thisParent)
        db = FirebaseFirestore.getInstance()
        bundle = Bundle()
        return b.root
    }

    override fun onStart() {
        super.onStart()
        showData()
    }

    fun showData(){
        db.collection("te_dosen")
            .whereEqualTo("jabatan", "Lektor Kepala")
            .get()
            .addOnSuccessListener { document ->
                val totalDosenCount = document.size()
                b.txLK.setText(totalDosenCount.toString())

            }

        db.collection("te_dosen")
            .whereEqualTo("jabatan", "Lektor")
            .get()
            .addOnSuccessListener { document ->
                val totalDosenCount = document.size()
                b.txL.setText(totalDosenCount.toString())

            }

        db.collection("te_dosen")
            .whereEqualTo("jabatan", "Guru Besar")
            .get()
            .addOnSuccessListener { document ->
                val totalDosenCount = document.size()
                b.txGB.setText(totalDosenCount.toString())

            }

        db.collection("te_dosen")
            .get()
            .addOnSuccessListener { document ->
                val totalDosenCount = document.size()
                b.txDT.setText(totalDosenCount.toString())
                b.txDTPS.setText(totalDosenCount.toString())

            }
    }

}