package com.akash.searchContact.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.akash.searchContact.R
import com.akash.searchContact.model.ContactModel

object Utility {
    fun getUserNameAsImageInAdapter(
        context: Context, holder: RecyclerView.ViewHolder, name: String?
    ): Bitmap? {
        val first: String
        val spiltName = name?.split(" ")
        try {
            if (spiltName?.size!! > 1) {
                first = spiltName[0][0].uppercaseChar().toString() + spiltName[1][0].uppercaseChar()
                    .toString()
            } else {
                first = spiltName[0][0].uppercaseChar().toString()
            }
            return textAsBitmap(
                first, 85f, ContextCompat.getColor(context, R.color.primaryDarkColor)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun textAsBitmap(text: String?, textSize: Float, textColor: Int): Bitmap? {
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
            canvas.drawText(text, 35F, baseline, paint)
        }
        return image
    }

    fun fetchContacts(context: Context): List<ContactModel> {
        val contacts = mutableListOf<ContactModel>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        while (cursor?.moveToNext() == true) {
            val id =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID))
            val name =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

            contacts.add(ContactModel(id, name, phoneNumber))
        }
        val distinctContacts: MutableList<ContactModel> = contacts.distinctBy { it.ContactName } as MutableList<ContactModel>
        cursor?.close()

        return distinctContacts
    }
}