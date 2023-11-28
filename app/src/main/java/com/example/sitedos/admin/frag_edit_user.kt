package com.example.sitedos.admin

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentEditdtuseradmBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class frag_edit_user : Fragment(), View.OnClickListener {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentEditdtuseradmBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var adapterRole : ArrayAdapter<String>
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var SessionManager : SessionManager
    var id_user = ""
    var dataRole = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentEditdtuseradmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        id_user = arguments?.getString("id_user")!!.toString()
        uri = Uri.EMPTY

        val roles = listOf<String>("Admin","Dosen")
        dataRole.addAll(roles)
        adapterRole = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            roles
        )
        adapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        b.sptRole.adapter = adapterRole


        b.btnSave.setOnClickListener(this)
        b.btnBack.setOnClickListener(this)
        return b.root
    }


    fun UploadData(nama : String, email : String, uname : String, pw : String,
                   role : String){
                val ID = db.collection("te_user").document(id_user)
                val hm = HashMap<String, Any>()
                hm.put("nama_user", nama)
                hm.put("email", email)
                hm.put("username", uname)
                hm.put("password", pw)
                hm.put("role", role)

                    ID.update(hm)
                        .addOnSuccessListener {
                            Toast.makeText(thisParent, "Data Anda Diupdate", Toast.LENGTH_SHORT).show()
                        }


    }

    override fun onStart() {
        super.onStart()
        showData(id_user)
    }

    fun showData(id : String) {
        db.collection("te_user")
            .whereEqualTo("id_user", id)
            .get()
            .addOnSuccessListener { document ->
                for (doc in document){
                    b.edtNamaUsr.setText(doc.get("nama_user").toString())
                    b.edtEmail.setText(doc.get("email").toString())
                    b.edtUname.setText(doc.get("username").toString())
                    b.edtPw.setText(doc.get("password").toString())
                    for (i in 0 until dataRole.size){
                        if (dataRole.get(i) == doc.getString("role")){
                            b.sptRole.setSelection(i)
                        }
                    }
                }
            }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnSave->{
                UploadData(
                            b.edtNamaUsr.text.toString(),
                            b.edtEmail.text.toString(),
                            b.edtUname.text.toString(),
                            b.edtPw.text.toString(),
                            dataRole.get(b.sptRole.selectedItemPosition)

                )
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragNext = frag_user()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragNext).commit()
                showData(id_user)
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