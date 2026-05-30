package com.multitodo.app.admin

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Formulario(
    var id: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "", // Añadido
    val slogan: String = "", // Añadido
    val categoria: String = "",
    val especialidad: String = "",
    val ciudades: List<String> = emptyList(),
    val descripcion: String = "",
    val estado: String = "pendiente",
    val logoUrl: String = "",
    val imagenesUrls: List<String> = emptyList(),
    val instagramLink: String = "",
    val tiktokLink: String = "",
    val facebookLink: String = "",
    val visitasTotales: Long = 0,
    @ServerTimestamp
    val timestamp: Date? = null
)
