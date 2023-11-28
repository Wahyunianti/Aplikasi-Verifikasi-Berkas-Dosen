package com.example.sitedos.admin

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.sitedos.R
import com.squareup.picasso.Picasso
import java.io.File

class AdapterDosen(
    private val context: Context,
    private val Dosenlist: List<Map<String, Any>>,
    private val onItemClick: (String) -> Unit,
    private val onEditClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : SimpleAdapter(context, Dosenlist,
    R.layout.dosenadm_data, arrayOf("nama_dosen", "jabatan", "foto"), intArrayOf(
        R.id.txNamaDosen,
        R.id.txJbtDosen,
        R.id.imgDosen
    )) {
    var uri = Uri.EMPTY
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)

        val currentDosen = Dosenlist[position]
        val usernidn = currentDosen["nidn"] as String
        val userId = currentDosen["id_dosen"] as String
        val file = currentDosen["foto"] as String
        uri = Uri.parse(file)

        val imv = view.findViewById<ImageView>(R.id.imgDosen)
        Picasso.get().load(uri).into(imv)

        val btnDetail = view.findViewById<Button>(R.id.btnDetailDosen)
        btnDetail.setOnClickListener {
            onItemClick(usernidn)
        }

        val btnEdit = view.findViewById<Button>(R.id.btnEditDosen)
        btnEdit.setOnClickListener {
            onEditClick(userId)
        }

        val btnHapus = view.findViewById<Button>(R.id.btnDeleteDosen)
        btnHapus.setOnClickListener {
            onDeleteClick(usernidn)
        }

        return view
    }
}