package com.multitodo.app.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.multitodo.app.admin.AdminViewModel
import com.multitodo.app.admin.Formulario
import com.multitodo.app.composables.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFormEditScreen(
    navController: NavController,
    formId: String?,
    adminViewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val formularioState by adminViewModel.formulario.collectAsState()
    
    // Estados locales para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var tiktok by remember { mutableStateOf("") }
    var facebook by remember { mutableStateOf("") }

    LaunchedEffect(formId) {
        formId?.let { adminViewModel.cargarFormulario(it) }
    }

    // Actualizar estados locales cuando el formulario se carga
    LaunchedEffect(formularioState) {
        formularioState?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            telefono = it.telefono
            instagram = it.instagramLink
            tiktok = it.tiktokLink
            facebook = it.facebookLink
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = "Editar Negocio")
        }
    ) { paddingValues ->
        if (formularioState == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Negocio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Redes Sociales", style = MaterialTheme.typography.titleSmall)
                }

                item {
                    OutlinedTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        label = { Text("Instagram (Link o @usuario)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = tiktok,
                        onValueChange = { tiktok = it },
                        label = { Text("TikTok (Link o @usuario)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = facebook,
                        onValueChange = { facebook = it },
                        label = { Text("Facebook (Link o Usuario)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val updatedForm = formularioState!!.copy(
                                nombre = nombre,
                                descripcion = descripcion,
                                telefono = telefono,
                                instagramLink = instagram,
                                tiktokLink = tiktok,
                                facebookLink = facebook
                            )
                            adminViewModel.actualizarFormulario(updatedForm)
                            Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}
