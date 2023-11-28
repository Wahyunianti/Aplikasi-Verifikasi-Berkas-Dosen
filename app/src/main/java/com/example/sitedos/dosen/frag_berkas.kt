package com.example.sitedos.dosen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentBerkasdsnBinding
import com.example.sitedos.databinding.FragmentDashboarddsnBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class frag_berkas : Fragment(), View.OnClickListener {
    lateinit var thisParent : DosenActivity
    private lateinit var b : FragmentBerkasdsnBinding
    lateinit var db : FirebaseFirestore
    lateinit var ft : FragmentTransaction
    lateinit var storage : FirebaseStorage
    lateinit var bundle: Bundle
    lateinit var SessionManager : SessionManager
    var user = ""
    var fileUpload = ""
    var uri = Uri.EMPTY
    var baru = Uri.EMPTY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as DosenActivity
        b = FragmentBerkasdsnBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        bundle = Bundle()
        user = SessionManager.getUsername() ?: ""
        b.btnDetaildsn.setOnClickListener(this)
        b.btnDetailkpt.setOnClickListener(this)
        b.btnDetailjbt.setOnClickListener(this)
        b.btnDetailijz.setOnClickListener(this)
        return b.root
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnDetaildsn-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragBkDosen = frag_berkas_dosendsn()
                bundle.putString("nama_file", "Sertifikat Dosen")
                fragBkDosen.arguments = bundle
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBkDosen)
                ft.commit()
            }
            com.example.sitedos.R.id.btnDetailkpt-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragBkDosen = frag_berkas_dosendsn()
                bundle.putString("nama_file", "Sertifikat Kompetensi")
                fragBkDosen.arguments = bundle
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBkDosen)
                ft.commit()
            }
            com.example.sitedos.R.id.btnDetailjbt-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragBkDosen = frag_berkas_dosendsn()
                bundle.putString("nama_file", "Jabatan Fungsional")
                fragBkDosen.arguments = bundle
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBkDosen)
                ft.commit()
            }
            com.example.sitedos.R.id.btnDetailijz-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragBkDosen = frag_berkas_dosendsn()
                bundle.putString("nama_file", "Ijazah")
                fragBkDosen.arguments = bundle
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBkDosen)
                ft.commit()
            }
        }
    }
}