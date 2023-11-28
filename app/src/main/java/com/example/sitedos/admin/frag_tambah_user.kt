package com.example.sitedos.admin

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentTambahdtuseradmBinding
import com.example.sitedos.databinding.FragmentTambahfldosenadmBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class frag_tambah_user: Fragment(), View.OnClickListener  {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentTambahdtuseradmBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var bundle : Bundle
    lateinit var adapterRole : ArrayAdapter<String>
    lateinit var SessionManager : SessionManager
    private var imageUri: Uri? = null
    var id_user = ""
    var dosen_id = ""
    var totalDosen = ""
    var totalDosen2 = ""
    var dataRole = mutableListOf<String>()
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentTambahdtuseradmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        id_user = SessionManager.getIdUser() ?: ""
        uri = Uri.EMPTY
        bundle = Bundle()

        val roles = listOf<String>("Admin","Dosen")
        dataRole.addAll(roles)
        adapterRole = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            roles
        )
        adapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.spRole.adapter = adapterRole

        b.btnSave.setOnClickListener(this)
        b.btnBack.setOnClickListener(this)
        return b.root
    }

    override fun onStart() {
        super.onStart()
    }

    fun UploadData(nama : String, email : String, uname : String, pw : String,
                   role : String){
                db.collection("te_user")
                    .get()
                    .addOnSuccessListener { documents ->
                        val ID = db.collection("te_user").document()
                        val hm = HashMap<String, Any>()
                        hm.put("id_user", ID.id)
                        hm.put("nama_user", nama)
                        hm.put("email", email)
                        hm.put("username", uname)
                        hm.put("password", pw)
                        hm.put("role", role)
                        hm.put("token", "")

                        ID.set(hm)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    thisParent,
                                    "Data Anda Ditambahkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                }





    override fun onClick(p0: View?) {

        when(p0?.id){
            com.example.sitedos.R.id.btnSave -> {
                val nama = b.edNamaUsr.text.toString()
                val email = b.edEmail.text.toString()
                val uname = b.edUname.text.toString()
                val pwd = b.edPw.text.toString()
                val role = dataRole.get(b.spRole.selectedItemPosition)
                if (nama.isNotBlank() && email.isNotBlank() && uname.isNotBlank() && pwd.isNotBlank()) {
                    UploadData(nama, email, uname, pwd, role)
                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragNext = frag_user()
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragNext).commit()
                } else {
                    Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            com.example.sitedos.R.id.btnBack -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragNext = frag_user()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragNext).commit()
            }
        }
    }

}