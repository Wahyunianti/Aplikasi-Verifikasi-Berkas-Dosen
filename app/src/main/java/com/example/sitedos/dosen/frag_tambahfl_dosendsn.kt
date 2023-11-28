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
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentTambahdtdosenadmBinding
import com.example.sitedos.databinding.FragmentTambahfldosendsnBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class frag_tambahfl_dosendsn: Fragment(), View.OnClickListener   {
    lateinit var thisParent : DosenActivity
    lateinit var b : FragmentTambahfldosendsnBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var bundle : Bundle
    lateinit var SessionManager : SessionManager
    var id_user = ""
    var berkas = ""
    var file = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as DosenActivity
        b = FragmentTambahfldosendsnBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        id_user = SessionManager.getUsername() ?: ""
        berkas = SessionManager.getIdUser() ?: ""
        file = arguments?.getString("nama_file")!!.toString()
        uri = Uri.EMPTY
        bundle = Bundle()
        b.btnSelesai.setOnClickListener(this)
        b.btnUploadFile.setOnClickListener(this)
        return b.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if((resultCode == Activity.RESULT_OK) && (requestCode == 100)){
            if(data != null){
                uri = data.data!!
                val cursor = context?.contentResolver?.query(uri, null, null, null, null)
                val File_Name = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor!!.moveToFirst()
                fileUpload = cursor.getString(File_Name)
                b.txFile.setText(uri.toString())
            }
        }
    }

    fun UploadData(ket : String){
        if(uri != null && fileUpload != ""){
            val fileRef = storage.reference.child(fileUpload)
            val uploadTask = fileRef.putFile(uri)
            uploadTask.continueWithTask {  task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener {
                    val ID = db.collection("te_berkas").document()
                    val hm = HashMap<String, Any>()
                    hm.put("id_file", ID.id)
                    hm.put("id_user", berkas)
                    hm.put("nidn", id_user)
                    hm.put("nama_file", file)
                    hm.put("keterangan", ket)
                    hm.put("fileRef", fileUpload)
                    hm.put("file", it.result.toString())
                    hm.put("status", "no")
                    ID.set(hm)
                        .addOnSuccessListener {
                            Toast.makeText(thisParent, "Data Anda Ditambahkan", Toast.LENGTH_SHORT).show()
                        }

                }
        } else {
            val ID = db.collection("te_berkas").document()
            val hm = HashMap<String, Any>()
            hm.put("id_file", ID.id)
            hm.put("id_user", berkas)
            hm.put("nidn", id_user)
            hm.put("nama_file", file)
            hm.put("keterangan", ket)
            hm.put("status", "no")

            ID.set(hm)
                .addOnSuccessListener {
                    Toast.makeText(thisParent, "Data Anda Ditambahkan", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnSelesai -> {
                val ketDsn = b.edKetDsn.text.toString()
                if (ketDsn.isNotBlank()) {
                UploadData(ketDsn)
                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragBck = frag_berkas()
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBck).commit()
                } else {
                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragBck = frag_berkas()
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeDsn, fragBck).commit()
                }
            }
            com.example.sitedos.R.id.btnUploadFile -> {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.setType("application/pdf")
                startActivityForResult(intent, 100)
            }
        }
    }

}