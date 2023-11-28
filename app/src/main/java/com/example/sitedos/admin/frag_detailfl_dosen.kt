package com.example.sitedos.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.R
import com.example.sitedos.SessionManager
import com.example.sitedos.databinding.FragmentDetaildtdosenadmBinding
import com.example.sitedos.databinding.FragmentViewfldosenadmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.HashMap

class frag_detailfl_dosen: Fragment(), View.OnClickListener  {

    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentViewfldosenadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var ft : FragmentTransaction
    lateinit var fragDosen: frag_datadosen
    lateinit var Fileadapter : SimpleAdapter
    lateinit var alFile : ArrayList<HashMap<String, Any>>
    lateinit var SessionManager : SessionManager
    lateinit var bundle : Bundle
    var niddn = ""
    var uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentViewfldosenadmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        SessionManager = SessionManager(thisParent)
        alFile = ArrayList()
        bundle = Bundle()
        niddn = arguments?.getString("nidn")!!.toString()
        b.btnBack.setOnClickListener(this)
        b.btnTambahFile.setOnClickListener(this)
        return b.root
    }

    override fun onStart() {
        super.onStart()
        show(niddn)
    }

    fun show(id : String){
        db.collection("te_berkas")
            .whereEqualTo("nidn", id)
            .get()
            .addOnSuccessListener {
                    doc ->
                alFile.clear()
                for (dox in doc) {
                    val hm = HashMap<String, Any>()
                    hm["nidn"] = dox.get("nidn").toString()
                    hm["nama_file"] = dox.get("nama_file").toString()
                    hm["keterangan"] = dox.get("keterangan").toString()
                    hm["file"] = dox.get("file").toString()
                    hm["status"] = dox.get("status").toString()
                    Log.d("value : ", hm.toString())
                    alFile.add(hm)
                }
                updateAdapter()
            }

        db.collection("te_dosen")
            .whereEqualTo("nidn", id)
            .get()
            .addOnSuccessListener { document ->
                for (doc in document){
                    b.txNamaDosen.setText(doc.get("nama_dosen").toString())
                }

            }
    }

    fun updateAdapter(){
        Fileadapter =
            AdapterFile(thisParent, alFile, onItemClick = { file ->
               val intent = Intent(Intent.ACTION_VIEW).setData(
                    Uri.parse(file)
                )
                context?.startActivity(intent)
            })
        b.lvDataFile.adapter = Fileadapter
        Fileadapter.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnBack-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                fragDosen = frag_datadosen()
                ft.addToBackStack(null)
                ft.replace(R.id.frameHomeAdm, fragDosen)
                ft.commit()
            }
            com.example.sitedos.R.id.btnTambahFile-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                val fragNext = frag_tambahfl_dosen()
                bundle.putString("nidn", niddn)
                fragNext.arguments = bundle
                ft.addToBackStack(null)
                ft.replace(com.example.sitedos.R.id.frameHomeAdm, fragNext).commit()
            }
        }
    }

}