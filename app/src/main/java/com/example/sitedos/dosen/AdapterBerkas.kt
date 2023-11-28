package com.example.sitedos.dosen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.sitedos.R
import com.squareup.picasso.Picasso
import java.io.File

class AdapterBerkas(
    private val context: Context,
    private val Berkaslist: List<Map<String, Any>>,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : SimpleAdapter(context, Berkaslist,
    R.layout.viewfiledsn_data, arrayOf("nama_file", "keterangan", "status"), intArrayOf(
        R.id.txNamaFile,
        R.id.txKeterangan,
        R.id.txStatus,
    )) {
    var uri = Uri.EMPTY
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)

        val currentFile = Berkaslist[position]
        val file = currentFile["file"] as String
        val nidn = currentFile["nidn"] as String

        uri = Uri.parse(file)

        val imv = view.findViewById<ImageView>(R.id.imgFile)
        imv.setImageResource(R.drawable.pdf)

        val imv2 = view.findViewById<ImageView>(R.id.imgDell)
        imv2.setImageResource(R.drawable.delete)

        val open = view.findViewById<TextView>(R.id.txStatus)
        open.setOnClickListener {
            onItemClick(file)
        }

        val del = view.findViewById<ImageView>(R.id.imgDell)
        del.setOnClickListener {
            onDeleteClick(nidn)
        }

        return view
    }
}