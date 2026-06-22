package com.multitodo.app.listings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.multitodo.app.R
import com.multitodo.app.admin.Formulario
import com.multitodo.app.composables.AppTopBar

// Función para limpiar y formatear el número para WhatsApp
private fun formatForWhatsApp(phone: String): String {
    val clean = phone.replace(Regex("[^0-9]"), "")
    return if (clean.startsWith("0")) {
        "593${clean.substring(1)}"
    } else if (clean.startsWith("593")) {
        clean
    } else {
        "593$clean"
    }
}

private fun buildSocialUrl(usernameOrUrl: String, baseUrl: String): String {
    return if (usernameOrUrl.startsWith("http", ignoreCase = true)) {
        usernameOrUrl
    } else {
        baseUrl + usernameOrUrl.removePrefix("@")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(navController: NavController, city: String?, category: String?, specialty: String?) {
    var businesses by remember { mutableStateOf<List<Formulario>>(emptyList()) }
    val db = Firebase.firestore
    val context = LocalContext.current

    LaunchedEffect(city, category, specialty) {
        if (city != null && category != null && specialty != null) {
            db.collection("formularios")
                .whereArrayContains("ciudades", city)
                .whereEqualTo("estado", "aprobado")
                .whereEqualTo("categoria", category)
                .whereEqualTo("especialidad", specialty)
                .orderBy("orden", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    businesses = documents.map { doc ->
                        doc.toObject<Formulario>().copy(id = doc.id)
                    }
                }
                .addOnFailureListener {
                    // Fallback sin orden por si falta el índice compuesto en Firestore
                    db.collection("formularios")
                        .whereArrayContains("ciudades", city)
                        .whereEqualTo("estado", "aprobado")
                        .whereEqualTo("categoria", category)
                        .whereEqualTo("especialidad", specialty)
                        .get()
                        .addOnSuccessListener { documents ->
                            businesses = documents.map { doc ->
                                doc.toObject<Formulario>().copy(id = doc.id)
                            }
                        }
                }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = "$specialty en $city", showLogout = true)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(businesses) { business ->
                val wsPhone = formatForWhatsApp(business.telefono)
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("formulario_detail/${business.id}") },
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = business.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (business.especialidad.isNotBlank() && business.especialidad != "N/A") {
                            Text(
                                text = business.especialidad,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             // Call Icon
                            if (business.telefono.isNotBlank()) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Llamar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { 
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${business.telefono}")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "No se pudo abrir el marcador", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            // WhatsApp Icon arreglado
                            if (business.telefono.isNotBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.whatsapp),
                                    contentDescription = "Abrir WhatsApp",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { 
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    data = Uri.parse("https://wa.me/$wsPhone")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            // Instagram, TikTok, Facebook... (resto del código igual)
                            if (business.instagramLink.isNotBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.instagram),
                                    contentDescription = "Abrir Instagram",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { 
                                            val fullUrl = buildSocialUrl(business.instagramLink, "https://www.instagram.com/")
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                                            context.startActivity(intent)
                                        }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            if (business.tiktokLink.isNotBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.tiktok),
                                    contentDescription = "Abrir TikTok",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { 
                                            val fullUrl = buildSocialUrl(business.tiktokLink, "https://www.tiktok.com/@")
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                                            context.startActivity(intent)
                                        }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            if (business.facebookLink.isNotBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.facebook),
                                    contentDescription = "Abrir Facebook",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { 
                                            val fullUrl = buildSocialUrl(business.facebookLink, "https://www.facebook.com/")
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                                            context.startActivity(intent)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
