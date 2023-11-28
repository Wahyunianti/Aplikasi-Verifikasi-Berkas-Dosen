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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentTambahdtdosenadmBinding
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

class frag_tambah_dosen: Fragment(), View.OnClickListener  {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentTambahdtdosenadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var bundle : Bundle
    lateinit var SessionManager : SessionManager
    private var imageUri: Uri? = null
    var id_user = ""
    var dosen_id = ""
    var totalDosen = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentTambahdtdosenadmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        id_user = SessionManager.getIdUser() ?: ""
        uri = Uri.EMPTY
        bundle = Bundle()
        b.btnNext.setOnClickListener(this)
        b.btnBack.setOnClickListener(this)
        b.btnUpload.setOnClickListener(this)
        return b.root
    }

    override fun onStart() {
        super.onStart()
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
                b.txNamaFoto.setText(uri.toString())
                Picasso.get().load(uri).into(b.imgFoto)
            }else{
                b.txNamaFoto.setText("loaded")
            }
        }
    }

    fun UploadData(nama : String, nidn : String, tgl : String, email : String,
                   alamat : String, pdd : String, jbt : String){
        if(uri != null && fileUpload != ""){
            val fileRef = storage.reference.child(fileUpload)
            val uploadImage = fileRef.putFile(uri)
            uploadImage.continueWithTask {
                    task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener {
                db.collection("te_dosen")
                    .get()
                    .addOnSuccessListener { documents ->
                        val ID = db.collection("te_dosen").document()
                        val hm = HashMap<String, Any>()
                        hm.put("id_dosen", ID.id)
                        hm.put("id_user", id_user)
                        hm.put("fileRef", fileUpload)
                        hm.put("nama_dosen", nama)
                        hm.put("nidn", nidn)
                        hm.put("tgl_lahir", tgl)
                        hm.put("email", email)
                        hm.put("alamat", alamat)
                        hm.put("pendidikan", pdd)
                        hm.put("jabatan", jbt)
                        hm.put("foto", it.result.toString())

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
        } else {

            db.collection("te_dosen")
                .get()
                .addOnSuccessListener { documents ->
                    val ID = db.collection("te_dosen").document()
                    val hm = HashMap<String, Any>()
                    hm.put("id_dosen", ID.id)
                    hm.put("id_user", id_user)
                    hm.put("nama_dosen", nama)
                    hm.put("nidn", nidn)
                    hm.put("tgl_lahir", tgl)
                    hm.put("email", email)
                    hm.put("alamat", alamat)
                    hm.put("pendidikan", pdd)
                    hm.put("jabatan", jbt)

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
    }


    override fun onClick(p0: View?) {

        when(p0?.id){
            com.example.sitedos.R.id.btnNext -> {
                val nama = b.edNamaDsn.text.toString()
                val nidn = b.edNidnDsn.text.toString()
                val ttl = b.edTtlDsn.text.toString()
                val eml = b.edEmlDsn.text.toString()
                val alm = b.edAlmDsn.text.toString()
                val pdd = b.edddDsn.text.toString()
                val jbt = b.edJbtDsn.text.toString()
                if (nama.isNotBlank() && nidn.isNotBlank() && ttl.isNotBlank() && eml.isNotBlank()
                    && alm.isNotBlank() && pdd.isNotBlank() && jbt.isNotBlank()) {
                    UploadData(nama, nidn, ttl, eml, alm, pdd, jbt)
                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragNext = frag_tambahfl_dosen()
                    bundle.putString("nidn", nidn)
                    fragNext.arguments = bundle
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragNext).commit()
                } else {
                    Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }

            }
            com.example.sitedos.R.id.btnBack -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragDosen = frag_datadosen()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragDosen).commit()
            }
            com.example.sitedos.R.id.btnUpload -> {
//                resultLauncher.launch("image/*")
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.setType("image/*")
                startActivityForResult(intent, 100)
            }
        }
    }

}