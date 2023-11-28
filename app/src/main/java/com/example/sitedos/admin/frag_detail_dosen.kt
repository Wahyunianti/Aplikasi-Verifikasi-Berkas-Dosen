 package com.example.sitedos.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sitedos.R
import com.example.sitedos.databinding.FragmentDetaildtdosenadmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class frag_detail_dosen : Fragment(), View.OnClickListener {
    lateinit var thisParent : AdminActivity
    lateinit var b : FragmentDetaildtdosenadmBinding
    lateinit var db : FirebaseFirestore
    lateinit var ft : FragmentTransaction
    lateinit var bundle : Bundle
    lateinit var fragDosen: frag_datadosen
    lateinit var fragFile: frag_detailfl_dosen
    var niddn = ""
    var uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as AdminActivity
        b = FragmentDetaildtdosenadmBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        bundle = Bundle()
        niddn = arguments?.getString("nidn")!!.toString()
        b.btnKembali.setOnClickListener(this)
        b.btnFile.setOnClickListener(this)

        return b.root
    }

    override fun onStart() {
        super.onStart()
        showData(niddn)
    }

    fun showData(id : String) {
        db.collection("te_dosen")
            .whereEqualTo("nidn", id)
            .get()
            .addOnSuccessListener { document ->
                for (doc in document){
                    b.txDtNmDosen.setText(doc.get("nama_dosen").toString())
                    b.txDtNidnDosen.setText(doc.get("nidn").toString())
                    b.txDtTtlDosen.setText(doc.get("tgl_lahir").toString())
                    b.txDtEmailDosen.setText(doc.get("email").toString())
                    b.txDtAlmtDosen.setText(doc.get("alamat").toString())
                    b.txDtPddDosen.setText(doc.get("pendidikan").toString())
                    b.txDtJbtDosen.setText(doc.get("jabatan").toString())
                    uri = Uri.parse(doc.get("foto").toString())
                    Picasso.get().load(uri).into(b.imgFoto)
                }
            }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            com.example.sitedos.R.id.btnKembali-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                fragDosen = frag_datadosen()
                ft.addToBackStack(null)
                ft.replace(R.id.frameHomeAdm, fragDosen)
                ft.commit()
            }
            com.example.sitedos.R.id.btnFile-> {
                ft = requireActivity().supportFragmentManager.beginTransaction()
                fragFile = frag_detailfl_dosen()
                bundle.putString("nidn",niddn)
                fragFile.arguments = bundle
                ft.addToBackStack(null)
                ft.replace(R.id.frameHomeAdm, fragFile)
                ft.commit()
            }
        }
    }
}