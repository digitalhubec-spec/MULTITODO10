package com.multitodo.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.multitodo.app.admin.Formulario
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.composables.BusinessCard
import com.multitodo.app.ui.theme.MULTITODOAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiStoreScreen(navController: NavController, cityName: String?) {
    var businesses by remember { mutableStateOf<List<Formulario>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val db = Firebase.firestore

    LaunchedEffect(cityName) {
        if (cityName != null) {
            db.collection("formularios")
                .whereArrayContains("ciudades", cityName)
                .whereEqualTo("estado", "aprobado")
                .whereEqualTo("categoria", "MULTISTORE")
                .get()
                .addOnSuccessListener { documents ->
                    businesses = documents.map { doc ->
                        doc.toObject<Formulario>().copy(id = doc.id)
                    }
                }
        }
    }

    // Filtro inteligente: busca en nombre, especialidad y descripción (servicios/productos)
    val filteredBusinesses = remember(searchQuery, businesses) {
        if (searchQuery.isBlank()) {
            businesses
        } else {
            businesses.filter {
                it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.especialidad.contains(searchQuery, ignoreCase = true) ||
                it.descripcion.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(navController = navController, title = cityName ?: "Tiendas", showLogout = true)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Busca productos, servicios o tiendas...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredBusinesses) { business ->
                    BusinessCard(
                        business = business,
                        modifier = Modifier.clickable { navController.navigate("formulario_detail/${business.id}") }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MultiStoreScreenPreview() {
    MULTITODOAppTheme {
        MultiStoreScreen(rememberNavController(), "GUAYAQUIL")
    }
}
