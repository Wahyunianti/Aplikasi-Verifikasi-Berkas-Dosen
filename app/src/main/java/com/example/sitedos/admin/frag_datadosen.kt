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
import com.example.sitedos.databinding.FragmentDatadosenadmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class frag_datadosen : Fragment(), View.OnClickListener {
    lateinit var b : FragmentDatadosenadmBinding
    lateinit var thisParent : AdminActivity
    lateinit var db : FirebaseFirestore
    lateinit var SessionManager : SessionManager
    lateinit var Dosenadapter : SimpleAdapter
    lateinit var ft : FragmentTransaction
    lateinit var bundle : Bundle
    lateinit var alertDialog: AlertDialog
    lateinit var alDosen : ArrayList<HashMap<String,Any>>

    var niddn = ""
    var id_dosen = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentDatadosenadmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        alDosen = ArrayList()

        bundle = Bundle()
        b.btnTambahDsn.setOnClickListener(this)
        return b.root
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnTambahDsn -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val frag_tambahds = frag_tambah_dosen()
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, frag_tambahds).commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        show()
    }

    fun show(){
        db.collection("te_dosen")
            .get()
            .addOnSuccessListener {
                    doc ->
                alDosen.clear()
                for (dox in doc) {
                    val hm = HashMap<String, Any>()
                    hm["id_dosen"] = dox.get("id_dosen").toString()
                    hm["nama_dosen"] = dox.get("nama_dosen").toString()
                    hm["nidn"] = dox.get("nidn").toString()
                    hm["tgl_lahir"] = dox.get("tgl_lahir").toString()
                    hm["email"] = dox.get("email").toString()
                    hm["alamat"] = dox.get("alamat").toString()
                    hm["pendidikan"] = dox.get("pendidikan").toString()
                    hm["jabatan"] = dox.get("jabatan").toString()
                    hm["foto"] = dox.get("foto").toString()
                    Log.d("value : ", hm.toString())

                    alDosen.add(hm)
                }
                updateAdapter()
            }
    }

    fun updateAdapter(){
        Dosenadapter = AdapterDosen(thisParent, alDosen, onItemClick = {
                id ->
            niddn = id
            ft = requireActivity().supportFragmentManager.beginTransaction()
            val fragDetailDosen = frag_detail_dosen()
            bundle.putString("nidn",niddn)
            fragDetailDosen.arguments = bundle
            ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragDetailDosen)
            ft.commit()
        }, onEditClick = {
                id ->
            id_dosen = id
            ft = requireActivity().supportFragmentManager.beginTransaction()
            val fragEditDosen = frag_edit_dosen()
            bundle.putString("id_dosen",id_dosen)

            fragEditDosen.arguments = bundle
            ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragEditDosen)
            ft.commit()
        }, onDeleteClick = {
                id ->
            niddn = id
            alertDialog = AlertDialog.Builder(thisParent)
                .setTitle("Konfirmasi penghapusan data")
                .setMessage("Yakin hapus data? ")
                .setPositiveButton("Ya",delete)
                .setNegativeButton("Tidak",null)
                .show()
        })
        b.lvDataDosen.adapter = Dosenadapter
        Dosenadapter.notifyDataSetChanged()
    }

    val delete = object : DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
            DeleteDosen(niddn)
        }

    }

    fun DeleteDosen(nidn : String){
        db.collection("te_dosen")
            .whereEqualTo("nidn", nidn)
            .get()
            .addOnSuccessListener {
                    documents ->
//                for (doc in documents) {
//                    db.collection("te_dosen").document(doc.id)
//                        .delete()
//                        .addOnSuccessListener {
//                            Toast.makeText(thisParent, "Data Terhapus", Toast.LENGTH_SHORT).show()
//                            show()
//                        }
//                }

                val storageRef = FirebaseStorage.getInstance().reference

                for (doc in documents) {
                    val fileUrl = doc.getString("fileRef")

                    if (fileUrl != null) {
                        val fileRef = storageRef.child(fileUrl)

                        fileRef.delete()
                            .addOnSuccessListener {
                                db.collection("te_dosen").document(doc.id)
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
}