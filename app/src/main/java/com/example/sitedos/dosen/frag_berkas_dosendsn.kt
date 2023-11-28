package com.example.sitedos.dosen

import android.R
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.SessionManager
import com.example.sitedos.admin.AdapterFile
import com.example.sitedos.databinding.FragmentViewfldosendsnBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class frag_berkas_dosendsn : Fragment(), View.OnClickListener {
    lateinit var thisParent : DosenActivity
    lateinit var b : FragmentViewfldosendsnBinding
    lateinit var db : FirebaseFirestore
    lateinit var Berkasadapter : SimpleAdapter
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var bundle: Bundle
    lateinit var alertDialog: AlertDialog
    lateinit var uri : Uri
    lateinit var SessionManager : SessionManager
    lateinit var alBerkas : ArrayList<HashMap<String,Any>>
    var id_user = ""
    var file = ""
    var niddn = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as DosenActivity
        b = FragmentViewfldosendsnBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        alBerkas = ArrayList()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        file = arguments?.getString("nama_file")!!.toString()
        id_user = SessionManager.getUsername() ?: ""
        uri = Uri.EMPTY
        bundle = Bundle()
        b.btnTambahFile.setOnClickListener(this)
        b.btnBack.setOnClickListener(this)
        return b.root
    }

    override fun onStart() {
        super.onStart()
        show()
    }


    fun show(){
        db.collection("te_berkas")
            .whereEqualTo("nidn", id_user)
            .whereEqualTo("nama_file", file)
            .get()
            .addOnSuccessListener {
                    doc ->
                alBerkas.clear()
                for (dox in doc) {
                    val hm = HashMap<String, Any>()
                    hm["file"] = dox.get("file").toString()
                    hm["fileRef"] = dox.get("fileRef").toString()
                    hm["nidn"] = dox.get("nidn").toString()
                    hm["nama_file"] = dox.get("nama_file").toString()
                    hm["keterangan"] = dox.get("keterangan").toString()
                    hm["status"] = dox.get("status").toString()
                    Log.d("value : ", hm.toString())

                    alBerkas.add(hm)
                }
                updateAdapter()
            }
    }

    fun updateAdapter(){
        Berkasadapter =
            AdapterBerkas(thisParent, alBerkas, onItemClick = { file ->
                val intent = Intent(Intent.ACTION_VIEW).setData(
                    Uri.parse(file)
                )
                context?.startActivity(intent)
            }, onDeleteClick = { nidn ->
                niddn = nidn
                alertDialog = AlertDialog.Builder(thisParent)
                    .setTitle("Konfirmasi penghapusan data")
                    .setMessage("Yakin hapus data? ")
                    .setPositiveButton("Ya",delete)
                    .setNegativeButton("Tidak",null)
                    .show()
            })
        b.lvDataFile.adapter = Berkasadapter
        Berkasadapter.notifyDataSetChanged()
    }

    val delete = object : DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
            DeleteBerkas(niddn)
        }

    }

    fun DeleteBerkas(nidn : String){
        db.collection("te_berkas")
            .whereEqualTo("nidn", nidn)
            .get()
            .addOnSuccessListener {
                    documents ->
//                for (doc in documents) {
//                    db.collection("te_berkas").document(doc.id)
//                        .delete()
//                        .addOnSuccessListener {
//                            Toast.makeText(thisParent, "Data Terhapus", Toast.LENGTH_SHORT).show()
//                            show()
//                        }
//
//                }
                val storageRef = FirebaseStorage.getInstance().reference

                for (doc in documents) {
                    val fileUrl = doc.getString("fileRef")

                    if (fileUrl != null) {
                        val fileRef = storageRef.child(fileUrl)

                        fileRef.delete()
                            .addOnSuccessListener {
                                db.collection("te_berkas").document(doc.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(thisParent, "Data Terhapus", Toast.LENGTH_SHORT).show()
                                        show()
                                    }
                                    .addOnFailureListener { e ->
                                    }
                            }
                            .addOnFailureListener { e ->
                            }
                    }
                }

            }

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnTambahFile -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragTbh = frag_tambahfl_dosendsn()
                bundle.putString("nama_file", file)
                fragTbh.arguments = bundle
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragTbh).commit()
            }
            com.example.sitedos.R.id.btnBack -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragBack = frag_berkas()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBack).commit()
            }
        }
    }

}