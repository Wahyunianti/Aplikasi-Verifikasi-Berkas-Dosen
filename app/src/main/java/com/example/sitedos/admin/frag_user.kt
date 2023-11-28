package com.example.sitedos.admin

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentDatauseradmBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class frag_user : Fragment(), View.OnClickListener {
    lateinit var b : FragmentDatauseradmBinding
    lateinit var thisParent : AdminActivity
    lateinit var db : FirebaseFirestore
    lateinit var SessionManager : SessionManager
    lateinit var Useradapter : SimpleAdapter
    lateinit var ft : FragmentTransaction
    lateinit var bundle : Bundle
    lateinit var alertDialog: AlertDialog
    lateinit var alUser : ArrayList<HashMap<String,Any>>

    var id_user = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentDatauseradmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        alUser= ArrayList()

        bundle = Bundle()
        b.btnTambahUsr.setOnClickListener(this)
        return b.root
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnTambahUsr -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val frag_tambahus = frag_tambah_user()
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, frag_tambahus).commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        show()
    }

    fun show(){
        db.collection("te_user")
            .get()
            .addOnSuccessListener {
                    doc ->
                alUser.clear()
                for (dox in doc) {
                    val hm = HashMap<String, Any>()
                    hm["id_user"] = dox.get("id_user").toString()
                    hm["nama_user"] = dox.get("nama_user").toString()
                    hm["username"] = dox.get("username").toString()
                    hm["email"] = dox.get("email").toString()
                    hm["password"] = dox.get("password").toString()
                    hm["role"] = dox.get("role").toString()
                    Log.d("value : ", hm.toString())
                    alUser.add(hm)
                }
                updateAdapter()
            }
    }

    fun updateAdapter(){
        Useradapter = AdapterUser(thisParent, alUser, onEditClick = {
                id ->
            id_user = id
            ft = requireActivity().supportFragmentManager.beginTransaction()
            val fragEditUser = frag_edit_user()
            bundle.putString("id_user",id_user)
            fragEditUser.arguments = bundle
            ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragEditUser)
            ft.commit()
        }, onDeleteClick = {
                id ->
            id_user= id
            alertDialog = AlertDialog.Builder(thisParent)
                .setTitle("Konfirmasi penghapusan data")
                .setMessage("Yakin hapus data? ")
                .setPositiveButton("Ya",delete)
                .setNegativeButton("Tidak",null)
                .show()
        })
        b.lvDataUser.adapter = Useradapter
        Useradapter.notifyDataSetChanged()
    }

    val delete = object : DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
            DeleteUser(id_user)
        }

    }

    fun DeleteUser(id_user : String){
        db.collection("te_user")
            .whereEqualTo("id_user", id_user)
            .get()
            .addOnSuccessListener {
                    documents ->
                for (doc in documents) {
                    db.collection("te_user").document(doc.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(thisParent, "Data Terhapus", Toast.LENGTH_SHORT).show()
                            show()
                        }
                }
            }
    }
}