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
import com.example.sitedos.databinding.FragmentEditdtdosenadmBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class frag_edit_dosen : Fragment(), View.OnClickListener {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentEditdtdosenadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var SessionManager : SessionManager
    var id_user = ""
    var id_dosen = ""
    var niddn = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentEditdtdosenadmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        id_dosen = arguments?.getString("id_dosen")!!.toString()
        id_user = SessionManager.getIdUser() ?: ""
        uri = Uri.EMPTY
        b.btnSimpan.setOnClickListener(this)
        b.btnUpload2.setOnClickListener(this)
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
                b.txNamaFoto.setText(uri.toString())
                Picasso.get().load(uri).into(b.imageView2)
            }

        }
    }

    fun UploadData(nama : String, tgl : String, email : String,
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
                val ID = db.collection("te_dosen").document(id_dosen)
                val hm = HashMap<String, Any>()
                hm.put("nama_dosen", nama)
                hm.put("fileRef", fileUpload)
                hm.put("tgl_lahir", tgl)
                hm.put("email", email)
                hm.put("alamat", alamat)
                hm.put("pendidikan", pdd)
                hm.put("jabatan", jbt)
                hm.put("foto", it.result.toString())

                    ID.update(hm)
                        .addOnSuccessListener {
                            Toast.makeText(thisParent, "Data Anda Diupdate", Toast.LENGTH_SHORT).show()
                        }

                }
        } else {

            val ID = db.collection("te_dosen").document(id_dosen)
            val hm = HashMap<String, Any>()
            hm.put("nama_dosen", nama)
            hm.put("tgl_lahir", tgl)
            hm.put("email", email)
            hm.put("alamat", alamat)
            hm.put("pendidikan", pdd)
            hm.put("jabatan", jbt)

            ID.update(hm)
                .addOnSuccessListener {
                    Toast.makeText(thisParent, "Data Anda Diupdate", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onStart() {
        super.onStart()
        showData(id_dosen)
    }

    fun showData(id : String) {
        db.collection("te_dosen")
            .whereEqualTo("id_dosen", id)
            .get()
            .addOnSuccessListener { document ->
                for (doc in document){
                    b.edtNamaDsn.setText(doc.get("nama_dosen").toString())
                    b.edtNidnDsn.setText(doc.get("nidn").toString())
                    b.edtTtlDsn.setText(doc.get("tgl_lahir").toString())
                    b.edtAlmDsn.setText(doc.get("alamat").toString())
                    b.edtPddDsn.setText(doc.get("pendidikan").toString())
                    b.edtJbtDsn.setText(doc.get("jabatan").toString())
                    b.edtEmlDsn.setText(doc.get("email").toString())
                }
            }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnSimpan->{
                UploadData(
                            b.edtNamaDsn.text.toString(),
                            b.edtTtlDsn.text.toString(),
                            b.edtEmlDsn.text.toString(),
                            b.edtAlmDsn.text.toString(),
                            b.edtPddDsn.text.toString(),
                            b.edtJbtDsn.text.toString()
                )

                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragDosen = frag_datadosen()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragDosen).commit()
                showData(id_dosen)
            }
            com.example.sitedos.R.id.btnBacks -> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragDosen = frag_datadosen()
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragDosen).commit()
            }
            com.example.sitedos.R.id.btnUpload2 -> {
                val storageRef = FirebaseStorage.getInstance().reference
                db.collection("te_dosen")
                    .whereEqualTo("id_dosen", id_dosen)
                    .get()
                    .addOnSuccessListener { document ->
                        for (doc in document) {
                            val fileUrl = doc.getString("fileRef")

                            if (fileUrl != null) {
                                val fileRef = storageRef.child(fileUrl )
                                fileRef.delete()
                            }}}
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.setType("image/*")
                startActivityForResult(intent, 100)

            }
        }
    }

}