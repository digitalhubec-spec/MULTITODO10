package com.multitodo.app.listings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.multitodo.app.R
import com.multitodo.app.admin.Formulario
import com.multitodo.app.composables.AppTopBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Formato para WhatsApp (Sin +, sin espacios, con 593 al inicio)
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

// Formato para Llamadas (Con +)
private fun formatForCall(phone: String): String {
    val clean = phone.replace(Regex("[^0-9]"), "")
    return if (clean.startsWith("0")) {
        "+593${clean.substring(1)}"
    } else if (clean.startsWith("593")) {
        "+$clean"
    } else {
        "+593$clean"
    }
}

private fun buildSocialUrl(usernameOrUrl: String, baseUrl: String): String {
    if (usernameOrUrl.isBlank()) return ""
    return if (usernameOrUrl.startsWith("http", ignoreCase = true)) {
        usernameOrUrl
    } else {
        baseUrl + usernameOrUrl.removePrefix("@")
    }
}

class FormularioDetailViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _formulario = MutableStateFlow<Formulario?>(null)
    val formulario = _formulario.asStateFlow()

    private val _visitasHoy = MutableStateFlow(0L)
    val visitasHoy = _visitasHoy.asStateFlow()

    fun loadFormulario(formId: String) {
        viewModelScope.launch {
            db.collection("formularios").document(formId)
                .get()
                .addOnSuccessListener { document ->
                    val form = document.toObject<Formulario>()?.copy(id = document.id)
                    _formulario.value = form
                    registrarVisita(formId)
                    cargarEstadisticasHoy(formId)
                }
        }
    }

    private fun registrarVisita(formId: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val docRef = db.collection("formularios").document(formId)
        docRef.update("visitasTotales", FieldValue.increment(1))
        val statRef = docRef.collection("estadisticas").document(today)
        statRef.set(mapOf("visitas" to FieldValue.increment(1)), com.google.firebase.firestore.SetOptions.merge())
    }

    private fun cargarEstadisticasHoy(formId: String) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.collection("formularios").document(formId)
            .collection("estadisticas").document(today)
            .addSnapshotListener { snapshot, _ ->
                _visitasHoy.value = snapshot?.getLong("visitas") ?: 0L
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioDetailScreen(
    navController: NavController,
    formId: String?,
    viewModel: FormularioDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(formId) {
        formId?.let { viewModel.loadFormulario(it) }
    }

    val formulario by viewModel.formulario.collectAsState()
    val visitasHoy by viewModel.visitasHoy.collectAsState()
    val context = LocalContext.current
    val user = Firebase.auth.currentUser
    
    val isAdmin = user?.email == "vicocastillolopez27@gmail.com"

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = formulario?.nombre ?: "Detalle")
        }
    ) { paddingValues ->
        if (formulario == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val form = formulario!!
            val wsPhone = formatForWhatsApp(form.telefono)
            val callPhone = formatForCall(form.telefono)
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isAdmin) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Estadísticas de Tráfico", fontWeight = FontWeight.Bold)
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.5f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Hoy", style = MaterialTheme.typography.labelMedium)
                                        Text("$visitasHoy visitas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Total Histórico", style = MaterialTheme.typography.labelMedium)
                                        Text("${form.visitasTotales} visitas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    AsyncImage(
                        model = form.logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.logo)
                    )
                }

                item {
                    Text(
                        text = form.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (form.especialidad.isNotBlank() && form.especialidad != "N/A") {
                        Text(
                            text = form.especialidad,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (form.imagenesUrls.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Galería del Negocio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 8.dp)
                            ) {
                                items(form.imagenesUrls) { imageUrl ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Foto del negocio",
                                            modifier = Modifier
                                                .size(200.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (form.descripcion.isNotBlank()) {
                    item {
                         Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Acerca de",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = form.descripcion, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callPhone"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No se pudo realizar la llamada", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Llamar", fontSize = 12.sp)
                        }
                        
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$wsPhone"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color(0xFF25D366)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF25D366)
                            )
                        ) {
                             Image(
                                 painter = painterResource(id = R.drawable.whatsapp), 
                                 contentDescription = null, 
                                 modifier = Modifier.size(20.dp)
                             )
                             Spacer(modifier = Modifier.width(6.dp))
                             Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                item { 
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (form.instagramLink.isNotBlank()) {
                            SocialButton("Instagram", R.drawable.instagram, buildSocialUrl(form.instagramLink, "https://www.instagram.com/"))
                        }
                        if (form.tiktokLink.isNotBlank()) {
                            SocialButton("TikTok", R.drawable.tiktok, buildSocialUrl(form.tiktokLink, "https://www.tiktok.com/@"))
                        }
                        if (form.facebookLink.isNotBlank()) {
                            SocialButton("Facebook", R.drawable.facebook, buildSocialUrl(form.facebookLink, "https://www.facebook.com/"))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialButton(label: String, iconRes: Int, url: String) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir $label", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.DarkGray)
    }
}
