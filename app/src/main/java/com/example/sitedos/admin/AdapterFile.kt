package com.example.sitedos.admin

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

class AdapterFile(
    private val context: Context,
    private val Filelist: List<Map<String, Any>>,
    private val onItemClick: (String) -> Unit
) : SimpleAdapter(context, Filelist,
    R.layout.viewfileadm_data, arrayOf("nama_file", "keterangan", "file"), intArrayOf(
        R.id.txNamaFile,
        R.id.txKeterangan,
        R.id.txOpen,
    )) {
    var uri = Uri.EMPTY
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)

        val currentFile = Filelist[position]
        val file = currentFile["file"] as String
        uri = Uri.parse(file)

        val imv = view.findViewById<ImageView>(R.id.imgFile)
        imv.setImageResource(R.drawable.pdf)

        val open = view.findViewById<TextView>(R.id.txOpen)
        open.setOnClickListener {
            onItemClick(file)
        }

        return view
    }
}