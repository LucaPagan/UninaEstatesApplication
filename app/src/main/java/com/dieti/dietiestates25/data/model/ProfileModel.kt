package com.dieti.dietiestates25.data.model

import com.dieti.dietiestates25.data.model.modelsource.DefaultPhonePrefix
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix


data class ProfileData(
    val name: String,
    val email: String,
    val selectedPrefix: PhonePrefix = DefaultPhonePrefix,
    val phoneNumberWithoutPrefix: String
) {
    val fullPhoneNumber: String
        get() = "${selectedPrefix.prefix}${phoneNumberWithoutPrefix}"
}