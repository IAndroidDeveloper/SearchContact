package com.akash.searchContact

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akash.searchContact.adapter.ContactListAdapter
import com.akash.searchContact.model.ContactModel
import com.akash.searchContact.model.ContactRequest
import com.akash.searchContact.util.Utility
import java.util.Locale

class MainActivity : AppCompatActivity() {
    var progressBar: ProgressBar? = null
    var itemListLayout: ConstraintLayout? = null
    var invitationList: RecyclerView? = null
    var searchPart: EditText? = null
    var imgUser: ImageView? = null
    var contacts: List<ContactModel> = arrayListOf()
    lateinit var contactListAdapter: ContactListAdapter
    private var contactRequestList: ArrayList<ContactRequest?> = arrayListOf()
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getContactsPermission()
        progressBar = findViewById(R.id.progrss_bar)
        itemListLayout = findViewById(R.id.itemList_layout)
        searchPart = findViewById(R.id.search_part)
        invitationList = findViewById(R.id.invitation_list)

        invitationList?.layoutManager = LinearLayoutManager(this)
        contactListAdapter = ContactListAdapter(
            this, contactRequestList
        )
        invitationList?.adapter = contactListAdapter
        clickListeners()
    }

    private fun clickListeners() {

        searchPart?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 3) {
                    checkContactAvail(s)
                } else if (s?.length!! == 0) {
                    contactRequestList.clear()
                    contactListAdapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun checkContactAvail(s: CharSequence?) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_CONTACTS
            )
        ) {
            getContactsPermission()
        } else {
            if (contacts.isNullOrEmpty()) contacts = Utility.fetchContacts(this)
        }
        contactRequestList.clear()
        for (contact in contacts) {
            var number: String? = contact.ContactNumber?.replace(" ", "")
            val contactNumber: String? = if (s.toString().matches("[0-9]+".toRegex())) {
                contact.ContactNumber?.replace(" ", "")
            } else {
                contact.ContactName?.replace(" ", "")?.lowercase(Locale.ROOT)
            }
            if (contactNumber?.contains(
                    s.toString().replace(" ", "").lowercase(Locale.ROOT)
                ) == true
            ) {
                number = if (number?.contains("+91") == true) number?.split("+91")?.get(1)
                else number
                contactRequestList.add(ContactRequest(contact.ContactName, "+91", number))
                this.runOnUiThread {
                    Log.i("inviteUseRequestList", "::${contactRequestList.size}")
                    contactListAdapter.notifyDataSetChanged()
                }
            }
        }


    }

    private fun requestContactPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            getContactsPermission()
        } else {
            if (contacts.isNullOrEmpty()) contacts = Utility.fetchContacts(this)
        }
    }

    private fun getContactsPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_CONTACTS
                )
            ) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Read contacts access needed")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setMessage("Please enable access to contacts.")
                builder.setOnDismissListener {}
                builder.show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS
                )
            }
        } else {
            contacts = Utility.fetchContacts(this)
        }

    }

    private fun getUserNameAsImage(context: Context, name: String?): Bitmap? {
        val first: String
        val spiltName = name?.split(" ")
        try {
            first = if (spiltName?.size!! > 1) {
                spiltName[0][0].uppercaseChar().toString() + spiltName[1][0].uppercaseChar()
                    .toString()
            } else {
                spiltName[0][0].uppercaseChar().toString()
            }
            return textAsBitmap(
                first, 85f, ContextCompat.getColor(context, R.color.primaryDarkColor)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun textAsBitmap(text: String?, textSize: Float, textColor: Int): Bitmap? {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.LEFT
        val baseline: Float = -paint.ascent() // ascent() is negative
        val width = (paint.measureText(text) + 80.85f).toInt() // round
        val height = (baseline + paint.descent() + 5.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        if (text != null) {
            canvas.drawText(text, 34F, baseline, paint)
        }
        return image
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this).create()
        alert.setTitle("Exit")
        alert.setMessage("Are you sure you want to exit?")
        alert.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)
        alert.setButton(
            DialogInterface.BUTTON_POSITIVE, "OK"
        ) { dialog: DialogInterface?, which: Int -> finishAffinity() }
        alert.setButton(
            DialogInterface.BUTTON_NEGATIVE, "Cancel"
        ) { dialog: DialogInterface?, which: Int -> alert.dismiss() }
        alert.show()
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val message = "Permission Granted"
                    val color = ContextCompat.getColor(this, R.color.primaryColor)
                    Toast.makeText(
                        this, message, Toast.LENGTH_LONG
                    ).show()
                    requestContactPermission()

                } else {
                    Toast.makeText(
                        this, "You have disabled a contacts permission", Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }
}