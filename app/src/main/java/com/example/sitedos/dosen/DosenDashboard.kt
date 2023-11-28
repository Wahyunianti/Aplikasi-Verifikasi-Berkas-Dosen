package com.example.sitedos.dosen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.ActivityLogin
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.admin.frag_datadosen
import com.example.sitedos.admin.frag_detailfl_dosen
import com.example.sitedos.admin.frag_edit_dosen
import com.example.sitedos.databinding.FragmentDashboarddsnBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class DosenDashboard : Fragment(), View.OnClickListener {
    lateinit var thisParent : DosenActivity
    private lateinit var b : FragmentDashboarddsnBinding
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
        b = FragmentDashboarddsnBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        bundle = Bundle()
        user = SessionManager.getUsername() ?: ""
        b.btnEdit.setOnClickListener(this)
        b.btnEditProfil.setOnClickListener(this)
        return b.root
    }

    override fun onStart() {
        super.onStart()
        showData(user)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 100){
            if(data != null){
                baru = data.data!!
                val cursor = context?.contentResolver?.query(baru, null, null, null, null)
                val File_Name = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor!!.moveToFirst()
                fileUpload = cursor.getString(File_Name)
                Picasso.get().load(baru).into(b.imgFoto)
                UploadData()
            }
            }

        }


        fun UploadData(){
            if(baru != null && fileUpload != "") {
                val fileRef = storage.reference.child(fileUpload)
                val uploadImage = fileRef.putFile(baru)
                uploadImage.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    fileRef.downloadUrl
                }.addOnCompleteListener {
                    val query = db.collection("te_dosen").whereEqualTo("nidn", user)
                    query.get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val doc = documents.first()
                                val hm = HashMap<String, Any>()
                                hm.put("foto", it.result.toString())
                                hm.put("fileRef", fileUpload)

                                doc.reference.update(hm)
                                    .addOnSuccessListener {
                                        Toast.makeText(thisParent, "Data Anda sudah diupdate", Toast.LENGTH_SHORT).show()
                                        showData(user)
                                    }
                                    .addOnFailureListener { e ->
                                    }
                            } else {
                                Toast.makeText(thisParent, "Dokumen tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                        }

                }
            }
        }

    fun showData(id : String){
        db.collection("te_dosen")
            .whereEqualTo("nidn", id)
            .get()
            .addOnSuccessListener { document ->
                for (doc in document){
                    b.txNamaDosen.setText(doc.get("nama_dosen").toString())
                    b.txDtsNmDosen.setText(doc.get("nama_dosen").toString())
                    b.txDtsNidnDosen.setText(doc.get("nidn").toString())
                    b.txDtsTtlDosen.setText(doc.get("tgl_lahir").toString())
                    b.txDtsEmailDosen.setText(doc.get("email").toString())
                    b.txDtsAlmtDosen.setText(doc.get("alamat").toString())
                    b.txDtsPddDosen.setText(doc.get("pendidikan").toString())
                    b.txDtsJbtDosen.setText(doc.get("jabatan").toString())
                    uri = Uri.parse(doc.get("foto").toString())
                    Picasso.get().load(uri).into(b.imgFoto)
                }
            }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnEdit-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragEditDosen = frag_edit_dosendsn()
                bundle.putString("nidn", user)
                fragEditDosen.arguments = bundle
                ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragEditDosen)
                ft.commit()
            }
            com.example.sitedos.R.id.btnEditProfil-> {
                val storageRef = FirebaseStorage.getInstance().reference
                db.collection("te_dosen")
                    .whereEqualTo("nidn", user)
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