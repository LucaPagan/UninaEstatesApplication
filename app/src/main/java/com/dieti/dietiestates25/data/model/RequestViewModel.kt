package com.dieti.dietiestates25.data.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class RequestViewModel : ViewModel() {

    private val _richieste = MutableStateFlow<List<Richiesta>>(emptyList())
    val richieste: StateFlow<List<Richiesta>> = _richieste

    init {
        Log.d("ViewModelDebug", "ViewModel init started.") // <-- Log 1
        caricaRichieste()
        Log.d("ViewModelDebug", "ViewModel init finished.") // <-- Log 3
    }

    private fun caricaRichieste() {
        Log.d("ViewModelDebug", "caricaRichieste started.") // <-- Log 2
        viewModelScope.launch {
            // Simula il caricamento di dati
            _richieste.value = listOf(
                Richiesta(
                    id = 1,
                    titolo = "Richiesta #1024",
                    descrizione = "Valutazione per appartamento in Via Roma, 10",
                    data = LocalDate.now().minusDays(2),
                    stato = StatoRichiesta.CONFERMATA
                ),
                Richiesta(
                    id = 2,
                    titolo = "Richiesta #1023",
                    descrizione = "Proposta di acquisto per villa a Posillipo",
                    data = LocalDate.now().minusDays(5),
                    stato = StatoRichiesta.IN_ACCETTAZIONE
                ),
                Richiesta(
                    id = 3,
                    titolo = "Richiesta #1021",
                    descrizione = "Documentazione per mutuo non approvata",
                    data = LocalDate.now().minusDays(10),
                    stato = StatoRichiesta.RIFIUTATA
                )
            )
        }
    }
}