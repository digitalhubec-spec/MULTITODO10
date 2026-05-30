package com.multitodo.app.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multitodo.app.composables.BusinessCard

@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel(),
    viewState: String // "pendiente" o "aprobado"
) {
    val formularios by viewModel.formularios.collectAsState()
    val totalVisitas by viewModel.totalVisitasGlobales.collectAsState()
    val totalNegocios by viewModel.totalNegociosActivos.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewState) {
        viewModel.cargarFormularios(viewState)
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
        // --- BUSCADOR UNIVERSAL (Solo para aprobados) ---
        if (viewState == "aprobado") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar por nombre, especialidad o categoría...") },
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

            // --- RESUMEN DE ESTADÍSTICAS ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Negocios Activos", style = MaterialTheme.typography.labelMedium)
                        Text("$totalNegocios", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Visitas Totales HUB", style = MaterialTheme.typography.labelMedium)
                        Text("$totalVisitas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredFormularios, key = { it.id }) { formulario ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
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
                        
                        if (viewState == "aprobado") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Visibility, 
                                        contentDescription = null, 
                                        tint = Color.Gray, 
                                        modifier = Modifier.size(16.dp)
                                    )
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
                                    IconButton(onClick = { showDeleteDialog = formulario.id }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
