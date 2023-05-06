package com.akash.searchContact.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.akash.searchContact.R
import com.akash.searchContact.model.ContactRequest
import com.akash.searchContact.util.Utility


class ContactListAdapter(
    var context: Context,
    var contactRequestList: ArrayList<ContactRequest?>,
) : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_invite_follower, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        callInvitedUserResponse(holder, position)
    }

    private fun callInvitedUserResponse(holder: ViewHolder, position: Int) {

        val imageBite = Utility.getUserNameAsImageInAdapter(
            context, holder, contactRequestList[position]?.name
        )
        if (imageBite != null) {
            holder.imageUser?.setImageBitmap(imageBite)
        }

        holder.txtName?.text = contactRequestList[position]?.name
        holder.txtFollowerNumber?.text = "${contactRequestList[position]?.dialCode} ${contactRequestList[position]?.contactNumber}"
    }

    override fun getItemCount(): Int {
        return contactRequestList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageUser: ImageView? = null
        var txtName: TextView? = null
        var txtFollowerNumber: TextView? = null

        var layoutRequest: ConstraintLayout? = null

        init {
            imageUser = itemView.findViewById(R.id.img_follow_user)
            txtName = itemView.findViewById(R.id.txt_follower_name)
            txtFollowerNumber = itemView.findViewById(R.id.txt_follower_number)
        }
    }
}