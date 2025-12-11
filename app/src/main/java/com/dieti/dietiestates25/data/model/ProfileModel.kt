package com.dieti.dietiestates25.data.model

import com.dieti.dietiestates25.data.model.modelsource.DefaultPhonePrefix
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix


data class ProfileData(
    val name: String = "Lorenzo",
    val email: String = "LorenzoTrignano@gmail.com",
    val selectedPrefix: PhonePrefix = DefaultPhonePrefix,
    val phoneNumberWithoutPrefix: String = "123456789"
) {
    val fullPhoneNumber: String
        get() = "${selectedPrefix.prefix}${phoneNumberWithoutPrefix}"
}