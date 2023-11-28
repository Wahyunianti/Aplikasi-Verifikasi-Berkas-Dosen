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

class AdapterVerif(
    private val context: Context,
    private val Veriflist: List<Map<String, Any>>,
    private val onItemClick: (String) -> Unit
) : SimpleAdapter(context, Veriflist,
    R.layout.verifiedfiledsn_data, arrayOf("nama_file", "keterangan"), intArrayOf(
        R.id.txNamaFile,
        R.id.txKeterangan
    )) {
    var uri = Uri.EMPTY
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)

        val currentFile = Veriflist[position]
        val file = currentFile["file"] as String
        uri = Uri.parse(file)

        val imv = view.findViewById<ImageView>(R.id.imgFile)
        imv.setImageResource(R.drawable.pdf)

        val imv2 = view.findViewById<ImageView>(R.id.imgVerif)
        imv2.setImageResource(R.drawable.checkbook)

        val open = view.findViewById<TextView>(R.id.txNamaFile)
        open.setOnClickListener {
            onItemClick(file)
        }

        return view
    }
}