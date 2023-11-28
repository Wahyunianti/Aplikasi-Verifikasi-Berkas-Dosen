package com.example.sitedos.admin

import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.ActivityLogin
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentVerifikasiadmBinding
import com.example.sitedos.dosen.AdapterVerif
import com.example.sitedos.dosen.DosenActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class frag_verifikasi : Fragment(), View.OnClickListener {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentVerifikasiadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var Verifadapter : SimpleAdapter
    lateinit var alertDialog: AlertDialog
    lateinit var storage : FirebaseStorage
    lateinit var ft : FragmentTransaction
    lateinit var uri : Uri
    lateinit var SessionManager : SessionManager
    lateinit var alVerif : ArrayList<HashMap<String,Any>>
    var id_user = ""
    var niddn = ""
    var fileUpload = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentVerifikasiadmBinding.inflate(inflater, container, false)
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
            .whereEqualTo("status", "no")
            .get()
            .addOnSuccessListener {
                    doc ->
                alVerif.clear()
                for (dox in doc) {
                    val hm = HashMap<String, Any>()
                    hm["file"] = dox.get("file").toString()
                    hm["nidn"] = dox.get("nidn").toString()
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
            AdapterVerified(thisParent, alVerif, onItemClick = { file ->
                val intent = Intent(Intent.ACTION_VIEW).setData(
                    Uri.parse(file)
                )
                context?.startActivity(intent)
            }, onVerifClick = { nidn ->
                niddn = nidn
                alertDialog = AlertDialog.Builder(thisParent)
                    .setTitle("Konfirmasi Verifikasi File")
                    .setMessage("Data telah memenuhi? ")
                    .setPositiveButton("Ya",acc)
                    .setNegativeButton("Tidak",null)
                    .show()
            })
        b.lvDataVerif.adapter = Verifadapter
        Verifadapter.notifyDataSetChanged()
    }

    val acc = object : DialogInterface.OnClickListener{
        override fun onClick(p0: DialogInterface?, p1: Int) {
            AccBerkas(niddn)
        }

    }

    fun AccBerkas(nidn : String){
        db.collection("te_berkas")
            .whereEqualTo("nidn", nidn)
            .get()
            .addOnSuccessListener {
                    documents ->
                for (doc in documents) {
//                    db.collection("te_berkas").document(doc.id)
//                        .delete()
//                        .addOnSuccessListener {
//                            Toast.makeText(thisParent, "Data Terhapus", Toast.LENGTH_SHORT).show()
//                            show()
//                        }
                    val docRef = db.collection("te_berkas").document(doc.id)
                    val updates = mapOf(
                        "status" to "acc"
                    )
                    docRef.update(updates)
                        .addOnSuccessListener {
                            // Update successful
                            Toast.makeText(thisParent, "Berkas Berhasil Diverifikasi", Toast.LENGTH_SHORT).show()
                            ApprovedMessage(doc.get("id_user").toString(), doc.get("nama_file").toString())
                            show()
                        }
                        .addOnFailureListener { e ->
                            // Handle any errors
                            println("Error verifikasi: $e")
                        }
                }



            }
    }

    fun ApprovedMessage(id_user: String, judul: String){
        db.collection("te_user")
            .whereEqualTo("id_user", id_user)
            .get()
            .addOnSuccessListener { user ->
                for (get in user){
                    var token = get.get("token").toString()
                    sendFCMMessage(token, "Berkas '${judul}' Berhasil diverifikasi", "Silahkan cek data Anda!")
                }
            }
    }

    fun sendFCMMessage(targetToken: String, title: String, message: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val serverKey =
                "AAAAQMQBC7U:APA91bHSUwCYX7Dd1lDu9N3qVeONBhc1O9VjJT8CvsDFQl2fw2IdYFAXrud7jY0yecbASPiP1OQBETWWpPVpnbZ7Krdhpl0H_ofbgi9JL1Z7c8vjgnQBuzuRBr4vKNjHpSAKtPyHZdM7"  // Replace with your FCM server key

            val client = OkHttpClient()

            val json = """
        {
            "to": "$targetToken",
            "notification": {
                "title": "$title",
                "body": "$message"
            }
        }
    """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization", "key=$serverKey")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                    println("FCM message sending failed: ${response.code} ${response.message}")
                } else {
                    // Handle the success
                    println("FCM message sent successfully")
                }
            }
        }
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