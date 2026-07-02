package com.multitodo.app.admin

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Formulario(
    var id: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
    val slogan: String = "",
    val categoria: String = "",
    val especialidad: String = "",
    val ciudades: List<String> = emptyList(),
    val provincia: String = "", // Nueva propiedad para organizar por provincia
    val descripcion: String = "",
    val estado: String = "pendiente",
    val logoUrl: String = "",
    val imagenesUrls: List<String> = emptyList(),
    val instagramLink: String = "",
    val tiktokLink: String = "",
    val facebookLink: String = "",
    val websiteLink: String = "", // Nuevo campo para sitio web
    val visitasTotales: Long = 0,
    val orden: Int = 0,
    val fechaAprobacion: Date? = null,
    @ServerTimestamp
    val timestamp: Date? = null
)
