package com.example.sitedos.dosen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.ActivityLogin
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentVerifikasidsnBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class frag_verifikasidsn : Fragment(), View.OnClickListener {
    lateinit var thisParent : DosenActivity
    lateinit var b : FragmentVerifikasidsnBinding
    lateinit var db : FirebaseFirestore
    lateinit var Verifadapter : SimpleAdapter
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var SessionManager : SessionManager
    lateinit var alVerif : ArrayList<HashMap<String,Any>>
    var id_user = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as DosenActivity
        b = FragmentVerifikasidsnBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        alVerif = ArrayList()
        SessionManager = SessionManager(thisParent)
        storage = Firebase.storage
        id_user = SessionManager.getUsername() ?: ""
        b.btnLogout.setOnClickListener(this)
        uri = Uri.EMPTY
        return b.root
    }

    override fun onStart() {
        super.onStart()
        show()
    }


    fun show(){
        db.collection("te_berkas")
            .whereEqualTo("nidn", id_user)
            .whereEqualTo("status", "acc")
            .get()
            .addOnSuccessListener {
                    doc ->
                alVerif.clear()
                for (dox in doc) {
                    val hm = HashMap<String, Any>()
                    hm["file"] = dox.get("file").toString()
                    hm["nama_file"] = dox.get("nama_file").toString()
                    hm["keterangan"] = dox.get("keterangan").toString()
                    hm["status"] = dox.get("status").toString()
                    Log.d("value : ", hm.toString())

                    alVerif.add(hm)
                }
                updateAdapter()
            }
    }

    fun updateAdapter(){
        Verifadapter =
            AdapterVerif(thisParent, alVerif, onItemClick = { file ->
                val intent = Intent(Intent.ACTION_VIEW).setData(
                    Uri.parse(file)
                )
                context?.startActivity(intent)
            })
        b.lvDataFile.adapter = Verifadapter
        Verifadapter.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnLogout -> {
                val intent= Intent(thisParent, ActivityLogin::class.java) //belum
                val docRef = db.collection("te_user").document(SessionManager.getIdUser() ?: "")
                val updates = mapOf(
                    "token" to FieldValue.delete()
                )
                docRef.update(updates)
                    .addOnSuccessListener {
                        // Update successful
                        println("Field deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors
                        println("Error deleting field: $e")
                    }
                SessionManager = SessionManager(thisParent)
                SessionManager.clearLoginDetails()
                if (!SessionManager.isLoggedIn()) {
                    Toast.makeText(thisParent, "Logout", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            }
        }
    }

}