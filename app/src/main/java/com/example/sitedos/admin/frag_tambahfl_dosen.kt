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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class frag_tambahfl_dosen: Fragment(), View.OnClickListener   {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentTambahfldosenadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var bundle : Bundle
    lateinit var SessionManager : SessionManager
    var niddn = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentTambahfldosenadmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        niddn = arguments?.getString("nidn")!!.toString()
        uri = Uri.EMPTY
        bundle = Bundle()
        b.btnTambahlagi.setOnClickListener(this)
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

    fun UploadData(nama_file : String, ket : String){
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
                    hm.put("nidn", niddn)
                    hm.put("nama_file", nama_file)
                    hm.put("fileRef", fileUpload)
                    hm.put("keterangan", ket)
                    hm.put("file", it.result.toString())
                    hm.put("status", "acc")
                    ID.set(hm)
                        .addOnSuccessListener {
                            Toast.makeText(thisParent, "Data Anda Ditambahkan", Toast.LENGTH_SHORT).show()
                        }

                }
        } else {
            val ID = db.collection("te_berkas").document()
            val hm = HashMap<String, Any>()
            hm.put("id_file", ID.id)
            hm.put("nidn", niddn)
            hm.put("nama_file", nama_file)
            hm.put("keterangan", ket)
            hm.put("status", "acc")

            ID.set(hm)
                .addOnSuccessListener {
                    Toast.makeText(thisParent, "Data Anda Ditambahkan", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnTambahlagi -> {
                val namaFile = b.edNamaFile.text.toString()
                val ketDsn = b.edKetDsn.text.toString()
                if (namaFile.isNotBlank() && ketDsn.isNotBlank()) {
                UploadData(namaFile, ketDsn)

                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragLagi = frag_tambahfl_dosen()
                    bundle.putString("nidn", niddn)
                    fragLagi.arguments = bundle
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragLagi).commit()
                } else {
                    Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }

            }
            com.example.sitedos.R.id.btnSelesai -> {
                val namaFile = b.edNamaFile.text.toString()
                val ketDsn = b.edKetDsn.text.toString()

                if (namaFile.isNotBlank() && ketDsn.isNotBlank()) {
                    UploadData(namaFile, ketDsn)
                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragDosen = frag_datadosen()
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragDosen).commit()
                } else {
                    ft = requireActivity().supportFragmentManager.beginTransaction()
                    val fragDosen = frag_datadosen()
                    ft.addToBackStack(null)
                    ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragDosen).commit()
                }
            }
            com.example.sitedos.R.id.btnUploadFile -> {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.setType("application/pdf")
                Toast.makeText(thisParent, "Upload berhasil", Toast.LENGTH_SHORT).show()
                startActivityForResult(intent, 100)
            }
        }
    }

}