package com.example.sitedos.dosen

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
import com.example.sitedos.databinding.FragmentEditdtdosendsnBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class frag_edit_dosendsn : Fragment(), View.OnClickListener {
    lateinit var thisParent : DosenActivity
    lateinit var b : FragmentEditdtdosendsnBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var SessionManager : SessionManager
    var id_user = ""
    var niddn = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as DosenActivity
        b = FragmentEditdtdosendsnBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        niddn = arguments?.getString("nidn")!!.toString()
        id_user = SessionManager.getUsername() ?: ""
        uri = Uri.EMPTY
        b.btnSimpan.setOnClickListener(this)
        b.btnBacks.setOnClickListener(this)
        return b.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 100){
            if(data != null){
                uri = data.data!!
                val cursor = context?.contentResolver?.query(uri, null, null, null, null)
                val File_Name = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor!!.moveToFirst()
                fileUpload = cursor.getString(File_Name)
//                Picasso.get().load(uri).into(b.imageView2)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        showData(niddn)
    }

    fun UploadData(nama : String, nidn : String, tgl : String, email : String,
                   alamat : String, pdd : String, jbt : String){

        val query = db.collection("te_dosen").whereEqualTo("nidn", niddn)
        query.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    val hm = HashMap<String, Any>()
                    hm.put("nama_dosen", nama)
                    hm.put("nidn", nidn)
                    hm.put("tgl_lahir", tgl)
                    hm.put("email", email)
                    hm.put("alamat", alamat)
                    hm.put("pendidikan", pdd)
                    hm.put("jabatan", jbt)

                    doc.reference.update(hm)
                        .addOnSuccessListener {
                            Toast.makeText(thisParent, "Data Anda Diupdate", Toast.LENGTH_SHORT).show()
                         }
                        .addOnFailureListener { e ->
                        }
                } else {
                    Toast.makeText(thisParent, "Dokumen tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
            }
//
//                val ID = db.collection("te_dosen").document(id_user)
//                val hm = HashMap<String, Any>()
//                hm.put("nama_dosen", nama)
//                hm.put("nidn", nidn)
//                hm.put("tgl_lahir", tgl)
//                hm.put("email", email)
//                hm.put("alamat", alamat)
//                hm.put("pendidikan", pdd)
//                hm.put("jabatan", jbt)
//
//                    ID.update(hm)
//                        .addOnSuccessListener {
//                            Toast.makeText(thisParent, "Data Anda sudah masuk ke dalam antrian", Toast.LENGTH_SHORT).show()
//                        }
    }


    fun showData(id : String) {
        db.collection("te_dosen")
            .whereEqualTo("nidn", id)
            .get()
            .addOnSuccessListener { document ->
                for (doc in document){
                    b.edtsNamaDsn.setText(doc.get("nama_dosen").toString())
                    b.edtsNidnDsn.setText(doc.get("nidn").toString())
                    b.edtsTtlDsn.setText(doc.get("tgl_lahir").toString())
                    b.edtsAlmDsn.setText(doc.get("alamat").toString())
                    b.edtsPddDsn.setText(doc.get("pendidikan").toString())
                    b.edtsJbtDsn.setText(doc.get("jabatan").toString())
                    b.edtsEmlDsn.setText(doc.get("email").toString())
                }
            }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnSimpan->{
                UploadData(
                            b.edtsNamaDsn.text.toString(),
                            b.edtsNidnDsn.text.toString(),
                            b.edtsTtlDsn.text.toString(),
                            b.edtsEmlDsn.text.toString(),
                            b.edtsAlmDsn.text.toString(),
                            b.edtsPddDsn.text.toString(),
                            b.edtsJbtDsn.text.toString()
                )
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragDosen = DosenDashboard()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragDosen).commit()
                showData(id_user)
            }
            com.example.sitedos.R.id.btnBacks -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragDosen = DosenDashboard()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragDosen).commit()
            }
        }
    }

}