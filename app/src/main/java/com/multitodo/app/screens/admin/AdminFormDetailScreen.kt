package com.multitodo.app.screens.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.multitodo.app.R
import com.multitodo.app.admin.AdminViewModel
import com.multitodo.app.composables.AppTopBar

private fun formatPhoneNumber(phone: String): String {
    return if (phone.startsWith("0")) {
        "+593${phone.substring(1)}"
    } else {
        if (phone.startsWith("+593")) phone else "+593$phone"
    }
}

private fun enviarEmail(context: Context, destinatario: String, asunto: String, mensaje: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
        putExtra(Intent.EXTRA_SUBJECT, asunto)
        putExtra(Intent.EXTRA_TEXT, mensaje)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se encontró una aplicación de correo", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFormDetailScreen(navController: NavController, formId: String?, adminViewModel: AdminViewModel = viewModel()) {
    LaunchedEffect(formId) {
        formId?.let { adminViewModel.cargarFormulario(it) }
    }

    val formulario by adminViewModel.formulario.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = "Detalle de Solicitud")
        }
    ) { paddingValues ->
        if (formulario == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val form = formulario!!
            val formattedPhone = formatPhoneNumber(form.telefono)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // LOGO
                item {
                    AsyncImage(
                        model = form.logoUrl,
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.logo)
                    )
                }

                item {
                    Text(
                        text = form.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${form.categoria} - ${form.especialidad}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // GALERÍA DE IMÁGENES
                if (form.imagenesUrls.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Galería de fotos enviada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(form.imagenesUrls) { imageUrl ->
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
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
                                    text = "Descripción del negocio",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(text = form.descripcion, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${form.telefono}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Llamar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Llamar")
                        }
                        
                        if (form.email.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    enviarEmail(context, form.email, "Contacto desde MULTITODO", "Hola ${form.nombre},")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Email, contentDescription = "Email")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Email")
                            }
                        }
                    }
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            onClick = { 
                                formId?.let { 
                                    adminViewModel.aprobarFormulario(it)
                                    // Enviar Email de Bienvenida
                                    if (form.email.isNotBlank()) {
                                        enviarEmail(
                                            context, 
                                            form.email, 
                                            "¡Felicidades! Bienvenido a MULTITODO 🚀", 
                                            "Hola ${form.nombre},\n\nNos complace informarte que tu solicitud ha sido aprobada. ¡Felicidades por formar parte de la Convergencia en nuestra comunidad!\n\nTu perfil ya está activo y visible para todos los usuarios. Esperamos que sea de gran provecho para tu crecimiento.\n\nSaludos,\nEquipo MULTITODO"
                                        )
                                    }
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Text("Aprobar")
                        }
                        
                        Button(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            onClick = { 
                                formId?.let { 
                                    // Enviar Email de Rechazo/Sugerencia
                                    if (form.email.isNotBlank()) {
                                        enviarEmail(
                                            context, 
                                            form.email, 
                                            "Actualización sobre tu solicitud en MULTITODO", 
                                            "Hola ${form.nombre},\n\nGracias por tu interés en nuestra plataforma. Por el momento, tu solicitud no ha sido aprobada.\n\nTe invitamos a intentarlo de nuevo, asegurándote de ser más claro con la información enviada o de adjuntar fotos de mejor calidad. Si tienes dudas, puedes comunicarte directamente con nosotros respondiendo a este correo.\n\nSaludos,\nEquipo MULTITODO"
                                        )
                                    }
                                    adminViewModel.rechazarFormulario(it)
                                    navController.popBackStack()
                                }
                            }, 
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Rechazar")
                        }
                    }
                }   
            }
        }
    }
}
