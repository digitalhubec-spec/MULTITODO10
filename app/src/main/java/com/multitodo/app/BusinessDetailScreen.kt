
package com.multitodo.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.multitodo.app.admin.Formulario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailScreen(navController: NavController, businessId: String) {
    val db = FirebaseFirestore.getInstance()
    var business by remember { mutableStateOf<Formulario?>(null) }
    val context = LocalContext.current

    LaunchedEffect(businessId) {
        db.collection("formularios").document(businessId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    business = document.toObject(Formulario::class.java)?.copy(id = document.id)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(business?.nombre ?: "Detalles", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            business?.let {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        if (it.logoUrl.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(it.logoUrl),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(8.dp)
                            )
                        }
                        Text(text = it.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = it.especialidad, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    Text(text = "Información de Contacto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Teléfono: ${it.telefono}")
                    it.descripcion.takeIf { it.isNotBlank() }?.let { desc ->
                        Text(text = "Acerca de: $desc")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Redes Sociales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (it.instagramLink.isNotBlank()) {
                            SocialMediaIcon(R.drawable.ic_instagram, "Instagram") { openUrl(context, it.instagramLink) }
                        }
                        if (it.tiktokLink.isNotBlank()) {
                            SocialMediaIcon(R.drawable.ic_tiktok, "TikTok") { openUrl(context, it.tiktokLink) }
                        }
                        if (it.facebookLink.isNotBlank()) {
                            SocialMediaIcon(R.drawable.ic_facebook, "Facebook") { openUrl(context, it.facebookLink) }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                 item {
                    Button(
                        onClick = { openWhatsApp(context, it.telefono) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_whatsapp), contentDescription = "WhatsApp")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contactar por WhatsApp")
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialMediaIcon(iconRes: Int, contentDescription: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
    )
}

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

private fun openWhatsApp(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phone")
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle exception
    }
}
