package com.multitodo.app.screens.form

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.utils.EcuadorData
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalFormScreen(navController: NavController, category: String?, specialty: String?) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val storage = Firebase.storage
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var aboutMe by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var instagramLink by remember { mutableStateOf("") }
    var tiktokLink by remember { mutableStateOf("") }
    var facebookLink by remember { mutableStateOf("") }
    var websiteLink by remember { mutableStateOf("") }

    // Estados para imágenes
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var galleryUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isUploading by remember { mutableStateOf(false) }

    // --- SECCIÓN DE UBICACIÓN ---
    val mainCities = EcuadorData.ciudadesPrincipales
    val selectedMainCities = remember { mutableStateMapOf<String, Boolean>() }
    
    val additionalCities = remember { mutableStateListOf<String>() }
    var showOtherLocations by remember { mutableStateOf(false) }
    var selectedProvincia by remember { mutableStateOf("") }
    var selectedCiudadOther by remember { mutableStateOf("") }
    
    var provinciaExpanded by remember { mutableStateOf(false) }
    var ciudadExpanded by remember { mutableStateOf(false) }

    // Lanzadores para seleccionar fotos
    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        logoUri = uri
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        galleryUris = uris.take(5) 
        if (uris.size > 5) {
            Toast.makeText(context, "Solo se guardarán las primeras 5 fotos", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { AppTopBar(navController = navController, title = "Añadir Perfil") }
    ) { paddingValues ->
        if (isUploading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Enviando registro y fotos...", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // --- SECCIÓN DE CIUDADES PRINCIPALES ---
                item {
                    Text("Selecciona las ciudades donde brindas servicio:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Ciudades Principales:", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    
                    mainCities.forEach { city ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = selectedMainCities[city] ?: false, onCheckedChange = { selectedMainCities[city] = it })
                            Text(city)
                        }
                    }

                    if (additionalCities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ciudades Adicionales (Máx 2):", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                        additionalCities.forEach { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(city, fontWeight = FontWeight.Medium)
                                IconButton(onClick = { additionalCities.remove(city) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    
                    if (additionalCities.size < 2) {
                        TextButton(onClick = { showOtherLocations = !showOtherLocations }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showOtherLocations) "Ocultar buscador" else "Agregar ciudad de otra provincia")
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Has alcanzado el límite de ciudades adicionales", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }

                if (showOtherLocations && additionalCities.size < 2) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Buscar en otras provincias:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            
                            // Dropdown Provincia
                            ExposedDropdownMenuBox(
                                expanded = provinciaExpanded,
                                onExpandedChange = { provinciaExpanded = !provinciaExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedProvincia,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Provincia") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinciaExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = provinciaExpanded,
                                    onDismissRequest = { provinciaExpanded = false }
                                ) {
                                    EcuadorData.provincias.forEach { provincia ->
                                        DropdownMenuItem(
                                            text = { Text(provincia) },
                                            onClick = {
                                                selectedProvincia = provincia
                                                selectedCiudadOther = ""
                                                provinciaExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Dropdown Ciudad
                            if (selectedProvincia.isNotBlank()) {
                                ExposedDropdownMenuBox(
                                    expanded = ciudadExpanded,
                                    onExpandedChange = { ciudadExpanded = !ciudadExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedCiudadOther,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Ciudad/Cantón") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ciudadExpanded) },
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = ciudadExpanded,
                                        onDismissRequest = { ciudadExpanded = false }
                                    ) {
                                        EcuadorData.ciudadesPorProvincia[selectedProvincia]?.forEach { ciudad ->
                                            DropdownMenuItem(
                                                text = { Text(ciudad) },
                                                onClick = {
                                                    val cityToAdd = ciudad.uppercase()
                                                    if (!additionalCities.contains(cityToAdd) && !mainCities.contains(cityToAdd)) {
                                                        additionalCities.add(cityToAdd)
                                                        selectedCiudadOther = ""
                                                        showOtherLocations = false
                                                    } else {
                                                        Toast.makeText(context, "Esta ciudad ya está en tu lista", Toast.LENGTH_SHORT).show()
                                                    }
                                                    ciudadExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- DATOS BÁSICOS ---
                item {
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nombre del Negocio o Profesional *") }, modifier = Modifier.fillMaxWidth())
                }

                // --- LOGO Y GALERÍA ---
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Logo o Foto de Perfil (Opcional)", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (logoUri != null) {
                                AsyncImage(model = logoUri, contentDescription = null, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            }
                            Button(onClick = { logoLauncher.launch("image/*") }, modifier = Modifier.padding(top = 8.dp)) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (logoUri == null) "Seleccionar Foto" else "Cambiar Foto")
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Galería de Trabajos (Opcional, Máx 5)", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (galleryUris.isNotEmpty()) {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(galleryUris) { uri ->
                                        AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                            Button(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.padding(top = 8.dp)) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Añadir fotos")
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono de Contacto *") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Text("+593") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                }
                item {
                    OutlinedTextField(value = aboutMe, onValueChange = { aboutMe = it }, label = { Text("Descripción de servicios") }, modifier = Modifier.fillMaxWidth().height(120.dp))
                }

                item { Text("Redes Sociales y Web (Opcional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                item { 
                    OutlinedTextField(
                        value = websiteLink, 
                        onValueChange = { websiteLink = it }, 
                        label = { Text("Sitio Web (Link)") }, 
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                        placeholder = { Text("https://tuweb.com") }
                    ) 
                }
                item { OutlinedTextField(value = instagramLink, onValueChange = { instagramLink = it }, label = { Text("Instagram (Usuario o Link)") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = tiktokLink, onValueChange = { tiktokLink = it }, label = { Text("TikTok (Usuario o Link)") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = facebookLink, onValueChange = { facebookLink = it }, label = { Text("Facebook (Link)") }, modifier = Modifier.fillMaxWidth()) }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val citiesList = selectedMainCities.filter { it.value }.keys.toMutableList()
                            citiesList.addAll(additionalCities)

                            if (fullName.isBlank() || phone.isBlank()) {
                                Toast.makeText(context, "Nombre y Teléfono son obligatorios", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            
                            if (citiesList.isEmpty()) {
                                Toast.makeText(context, "Selecciona al menos una ciudad", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            isUploading = true
                            scope.launch {
                                try {
                                    val docId = UUID.randomUUID().toString()
                                    var logoUrl = ""

                                    if (logoUri != null) {
                                        val logoRef = storage.reference.child("logos/$docId")
                                        logoRef.putFile(logoUri!!).await()
                                        logoUrl = logoRef.downloadUrl.await().toString()
                                    }

                                    val galleryUrls = mutableListOf<String>()
                                    galleryUris.forEachIndexed { index, uri ->
                                        val ref = storage.reference.child("galerias/$docId/img_$index")
                                        ref.putFile(uri).await()
                                        val url = ref.downloadUrl.await().toString()
                                        galleryUrls.add(url)
                                    }

                                    val formSubmission = hashMapOf(
                                        "nombre" to fullName,
                                        "telefono" to phone,
                                        "categoria" to (category ?: "General"),
                                        "especialidad" to (specialty ?: ""),
                                        "ciudades" to citiesList,
                                        "provincia" to selectedProvincia,
                                        "descripcion" to aboutMe,
                                        "estado" to "pendiente",
                                        "logoUrl" to logoUrl,
                                        "imagenesUrls" to galleryUrls,
                                        "instagramLink" to instagramLink,
                                        "tiktokLink" to tiktokLink,
                                        "facebookLink" to facebookLink,
                                        "websiteLink" to websiteLink,
                                        "visitasTotales" to 0L,
                                        "orden" to 0,
                                        "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                    )

                                    db.collection("formularios").document(docId).set(formSubmission).await()
                                    
                                    Toast.makeText(context, "¡Registro enviado! Revisaremos tu solicitud.", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al subir datos.", Toast.LENGTH_LONG).show()
                                    e.printStackTrace()
                                } finally {
                                    isUploading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enviar Registro", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
