package com.dieti.dietiestates25.ui.model.modelsource

import com.dieti.dietiestates25.R // Assicurati che R sia importabile da questo package
import com.dieti.dietiestates25.ui.model.PropertyModel // Importa il modello Property

val sampleListingProperties = listOf(
    PropertyModel(1, "450.000 €", "Appartamento", R.drawable.property1, "Napoli, Vomero"),
    PropertyModel(2, "380.000 €", "Appartamento", R.drawable.property2, "Napoli, Chiaia"),
    PropertyModel(3, "520.000 €", "Villa", R.drawable.property1, "Posillipo, Napoli"),
    PropertyModel(4, "290.000 €", "Attico", R.drawable.property2, "Napoli, Centro Storico"),
    PropertyModel(5, "210.000 €", "Bilocale", R.drawable.property1, "Napoli, Fuorigrotta"),
    PropertyModel(6, "600.000 €", "Appartamento", R.drawable.property2, "Napoli, San Ferdinando"),
    PropertyModel(7, "310.000 €", "Trivano", R.drawable.property1, "Napoli, Arenella")
)

// Potresti avere anche altri dati di esempio qui
// val sampleAgentProfiles = listOf(...)