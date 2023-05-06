package com.akash.searchContact.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ContactRequest(
    var name: String?,
    var dialCode: String?,

    var contactNumber: String?
) : Serializable
