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

class AdapterUser(
    private val context: Context,
    private val Userlist: List<Map<String, Any>>,
    private val onEditClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : SimpleAdapter(context, Userlist,
    R.layout.useradm_data, arrayOf("nama_user", "role"), intArrayOf(
        R.id.txNamaUsr,
        R.id.txRole
    )) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)

        val currentUser = Userlist[position]
        val userId = currentUser["id_user"] as String

        val btnEdit = view.findViewById<Button>(R.id.btnEditUsr)
        btnEdit.setOnClickListener {
            onEditClick(userId)
        }

        val btnHapus = view.findViewById<Button>(R.id.btnDeleteUsr)
        btnHapus.setOnClickListener {
            onDeleteClick(userId)
        }

        return view
    }
}