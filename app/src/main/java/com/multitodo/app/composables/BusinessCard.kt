package com.multitodo.app.composables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.multitodo.app.R
import com.multitodo.app.admin.Formulario
import com.multitodo.app.utils.FavoritesManager

// Función para el enlace de WhatsApp (Solo números, sin +)
private fun formatForWhatsApp(phone: String): String {
    val cleanPhone = phone.replace(Regex("[^0-9]"), "") // Quita cualquier cosa que no sea número
    return if (cleanPhone.startsWith("0")) {
        "593${cleanPhone.substring(1)}"
    } else if (cleanPhone.startsWith("593")) {
        cleanPhone
    } else {
        "593$cleanPhone"
    }
}

// Función para llamadas (Puede llevar +)
private fun formatForCall(phone: String): String {
    return if (phone.startsWith("0")) {
        "+593${phone.substring(1)}"
    } else if (phone.startsWith("593")) {
        "+$phone"
    } else if (phone.startsWith("+")) {
        phone
    } else {
        "+593$phone"
    }
}

private fun buildSocialUrl(usernameOrUrl: String, baseUrl: String): String {
    return if (usernameOrUrl.startsWith("http", ignoreCase = true)) {
        usernameOrUrl
    } else {
        baseUrl + usernameOrUrl.removePrefix("@")
    }
}

@Composable
fun BusinessCard(business: Formulario, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }
    var isFavorite by remember { mutableStateOf(favoritesManager.isFavorite(business.id)) }
    
    val whatsAppPhone = formatForWhatsApp(business.telefono)
    val callPhone = formatForCall(business.telefono)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = business.logoUrl,
                    contentDescription = "Logo de ${business.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.logo)
                )
                
                // BOTÓN DE FAVORITOS
                IconButton(
                    onClick = {
                        favoritesManager.toggleFavorite(business.id)
                        isFavorite = favoritesManager.isFavorite(business.id)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = business.nombre, fontWeight = FontWeight.Bold, maxLines = 1)
                
                if (business.categoria.isNotBlank() && business.categoria != "N/A") {
                    Text(
                        text = business.categoria,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callPhone"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Llamar")
                    }

                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$whatsAppPhone"))
                        context.startActivity(intent)
                    }) {
                        Image(painter = painterResource(id = R.drawable.whatsapp), contentDescription = "WhatsApp", modifier = Modifier.size(24.dp))
                    }

                    if (business.instagramLink.isNotBlank()) {
                        IconButton(onClick = {
                            val fullUrl = buildSocialUrl(business.instagramLink, "https://www.instagram.com/")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                            context.startActivity(intent)
                        }) {
                            Image(painter = painterResource(id = R.drawable.instagram), contentDescription = "Instagram", modifier = Modifier.size(24.dp))
                        }
                    }

                    if (business.tiktokLink.isNotBlank()) {
                        IconButton(onClick = {
                            val fullUrl = buildSocialUrl(business.tiktokLink, "https://www.tiktok.com/@")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                            context.startActivity(intent)
                        }) {
                            Image(painter = painterResource(id = R.drawable.tiktok), contentDescription = "TikTok", modifier = Modifier.size(24.dp))
                        }
                    }

                    if (business.facebookLink.isNotBlank()) {
                        IconButton(onClick = {
                            val fullUrl = buildSocialUrl(business.facebookLink, "https://www.facebook.com/")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                            context.startActivity(intent)
                        }) {
                            Image(painter = painterResource(id = R.drawable.facebook), contentDescription = "Facebook", modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}
