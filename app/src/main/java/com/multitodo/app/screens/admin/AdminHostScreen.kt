package com.multitodo.app.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.multitodo.app.admin.AdminScreen
import com.multitodo.app.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHostScreen(navController: NavController, viewModel: AdminViewModel = viewModel()) {
    var viewState by remember { mutableStateOf("pendiente") } // "pendiente", "aprobado" o "recordatorios"
    var showMenu by remember { mutableStateOf(false) }
    
    val totalVisitas by viewModel.totalVisitasGlobales.collectAsState()
    val totalNegocios by viewModel.totalNegociosActivos.collectAsState()

    val screenTitle = when(viewState) {
        "pendiente" -> "Solicitudes Pendientes"
        "aprobado" -> "Registros Aprobados"
        "recordatorios" -> "Recordatorios de Tiempo"
        else -> "Administración"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Ver Pendientes") },
                            onClick = { 
                                viewState = "pendiente"
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver Aprobados") },
                            onClick = { 
                                viewState = "aprobado"
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Recordatorios") },
                            leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFFFA000)) },
                            onClick = { 
                                viewState = "recordatorios"
                                showMenu = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(text = { Text("Cerrar Sesión") }, onClick = {
                            Firebase.auth.signOut()
                            navController.navigate("login") { popUpTo(0) }
                        })
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            
            // --- PANEL DE ESTADÍSTICAS RÁPIDAS ---
            if (viewState == "aprobado" || viewState == "recordatorios") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Visitas App",
                        value = totalVisitas.toString(),
                        icon = Icons.Default.Visibility,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Negocios",
                        value = totalNegocios.toString(),
                        icon = Icons.Default.Business,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()){
                AdminScreen(navController = navController, viewState = viewState, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
    }
}
