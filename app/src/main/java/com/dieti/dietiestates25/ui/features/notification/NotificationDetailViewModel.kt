package com.dieti.dietiestates25.ui.features.notification

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.NotificationDetail
import com.dieti.dietiestates25.data.model.NotificationIconType
import com.dieti.dietiestates25.data.remote.NotificationDetailDTO
import com.dieti.dietiestates25.data.remote.ProposalResponseRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class NotificationDetailViewModel : ViewModel() {


}