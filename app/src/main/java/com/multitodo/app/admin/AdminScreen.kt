package com.multitodo.app.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multitodo.app.composables.BusinessCard
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel(),
    viewState: String // "pendiente", "aprobado" o "recordatorios"
) {
    val formularios by viewModel.formularios.collectAsState()
    val totalVisitas by viewModel.totalVisitasGlobales.collectAsState()
    val totalNegocios by viewModel.totalNegociosActivos.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewState) {
        if (viewState == "recordatorios") {
            viewModel.cargarFormularios("aprobado")
        } else {
            viewModel.cargarFormularios(viewState)
        }
    }

    // Filtro para el buscador
    val filteredFormularios = remember(searchQuery, formularios) {
        if (searchQuery.isBlank()) {
            formularios
        } else {
            formularios.filter {
                it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.especialidad.contains(searchQuery, ignoreCase = true) ||
                it.categoria.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Registro") },
            text = { Text("¿Estás seguro de que deseas eliminar este negocio? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.rechazarFormulario(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- BUSCADOR UNIVERSAL (Para aprobados y recordatorios) ---
        if (viewState == "aprobado" || viewState == "recordatorios") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar negocio por nombre...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        if (viewState == "recordatorios") {
            Text(
                text = "Lista de seguimiento de antigüedad",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.Gray
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredFormularios, key = { it.id }) { formulario ->
                if (viewState == "recordatorios") {
                    ReminderCard(formulario, navController)
                } else {
                    StandardAdminCard(formulario, viewState, navController, onDelete = { showDeleteDialog = it })
                }
            }
        }
    }
}

@Composable
fun StandardAdminCard(formulario: Formulario, viewState: String, navController: NavController, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Badge de Orden
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Posición: ${formulario.orden}",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            BusinessCard(
                business = formulario,
                modifier = Modifier.clickable {
                    if (viewState == "pendiente") {
                        navController.navigate("admin_form_detail/${formulario.id}")
                    } else {
                        navController.navigate("admin_form_edit/${formulario.id}")
                    }
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
            
            val fechaReferencia = if (viewState == "aprobado") formulario.fechaAprobacion else formulario.timestamp
            LifetimeReminder(fechaReferencia, viewState)

            if (viewState == "aprobado") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formulario.visitasTotales} visitas",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row {
                        IconButton(onClick = { navController.navigate("admin_form_edit/${formulario.id}") }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onDelete(formulario.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderCard(formulario: Formulario, navController: NavController) {
    val fecha = formulario.fechaAprobacion ?: formulario.timestamp
    if (fecha == null) return

    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val now = Date()
    val diffInMillis = now.time - fecha.time
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    val months = (diffInDays / 30).toInt()
    val years = months / 12

    val reminderInfo = when {
        months >= 12 && months % 12 == 0 && diffInDays % 365 < 30 -> Pair("¡ANIVERSARIO!", Color.Red)
        months == 6 && diffInDays % 180 < 30 -> Pair("¡6 MESES!", Color(0xFFF57C00))
        months == 3 && diffInDays % 90 < 30 -> Pair("¡3 MESES!", Color(0xFFFFA000))
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("admin_form_edit/${formulario.id}") },
        colors = if (reminderInfo != null) CardDefaults.cardColors(containerColor = reminderInfo.second.copy(alpha = 0.05f)) else CardDefaults.cardColors(),
        border = if (reminderInfo != null) BorderStroke(1.dp, reminderInfo.second) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = formulario.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "Inició: ${sdf.format(fecha)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                if (reminderInfo != null) {
                    Text(
                        text = reminderInfo.first,
                        color = reminderInfo.second,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Text(
                    text = when {
                        years >= 1 -> "$years año(s) y ${months % 12} mes(es)"
                        months >= 1 -> "$months mes(es)"
                        else -> "$diffInDays día(s)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LifetimeReminder(fecha: Date?, viewState: String) {
    if (fecha == null) return

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaFormateada = sdf.format(fecha)

    val now = Date()
    val diffInMillis = now.time - fecha.time
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    val months = (diffInDays / 30).toInt()
    val years = months / 12

    val timeText = when {
        years >= 1 -> "Hace $years año(s)"
        months >= 1 -> "Hace $months mes(es)"
        else -> "Hace $diffInDays día(s)"
    }

    val label = if (viewState == "aprobado") "Aprobado el" else "Cargado el"

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label: $fechaFormateada ($timeText)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        if (viewState == "aprobado") {
            val reminderInfo = when {
                months == 3 -> Pair("¡3 Meses!", Color(0xFFFFA000))
                months == 6 -> Pair("¡6 Meses!", Color(0xFFF57C00))
                months >= 12 && months % 12 == 0 -> Pair("¡Aniversario!", Color.Red)
                else -> null
            }

            if (reminderInfo != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = reminderInfo.second.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, reminderInfo.second)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = reminderInfo.second, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = reminderInfo.first, color = reminderInfo.second, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
