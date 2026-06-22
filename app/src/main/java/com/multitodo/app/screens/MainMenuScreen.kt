package com.multitodo.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.multitodo.app.R
import com.multitodo.app.admin.Formulario
import com.multitodo.app.composables.AppTopBar
import com.multitodo.app.composables.BusinessCard
import com.multitodo.app.composables.Category
import com.multitodo.app.composables.CategoryCard
import com.multitodo.app.composables.ClickableLinkText
import com.multitodo.app.utils.FavoritesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(navController: NavController) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }
    
    var searchQuery by remember { mutableStateOf("") }
    var allBusinesses by remember { mutableStateOf<List<Formulario>>(emptyList()) }
    var favoriteBusinesses by remember { mutableStateOf<List<Formulario>>(emptyList()) }
    val db = Firebase.firestore

    // Cargar datos y favoritos
    LaunchedEffect(Unit) {
        db.collection("formularios")
            .whereEqualTo("estado", "aprobado")
            .orderBy("orden", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val list = documents.map { doc ->
                    doc.toObject<Formulario>().copy(id = doc.id)
                }
                allBusinesses = list
                
                // Filtrar favoritos
                val favoriteIds = favoritesManager.getFavorites()
                favoriteBusinesses = list.filter { it.id in favoriteIds }
            }
            .addOnFailureListener {
                // Si falla por falta de índice, al menos cargamos sin orden para no romper la app
                db.collection("formularios")
                    .whereEqualTo("estado", "aprobado")
                    .get()
                    .addOnSuccessListener { documents ->
                        val list = documents.map { doc ->
                            doc.toObject<Formulario>().copy(id = doc.id)
                        }
                        allBusinesses = list
                        val favoriteIds = favoritesManager.getFavorites()
                        favoriteBusinesses = list.filter { it.id in favoriteIds }
                    }
            }
    }

    // Filtro universal en tiempo real
    val filteredResults = remember(searchQuery, allBusinesses) {
        if (searchQuery.isBlank()) emptyList()
        else {
            allBusinesses.filter {
                it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.especialidad.contains(searchQuery, ignoreCase = true) ||
                it.descripcion.contains(searchQuery, ignoreCase = true) ||
                it.categoria.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            AppTopBar(
                navController = navController,
                title = "HUB Digital",
                showBackButton = false,
                showLogout = true
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val wpIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/593993178196")
                    }
                    context.startActivity(wpIntent)
                },
                containerColor = Color(0xFF25D366),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.SupportAgent, contentDescription = null) },
                text = { Text("CASSIE", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = Color(0xFF2D2D2D)
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            // --- BARRA DE BÚSQUEDA UNIVERSAL ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("¿Qué buscas hoy? (Ej: Dentista)", color = Color.Gray) },
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
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (searchQuery.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (filteredResults.isEmpty()) {
                            item {
                                Text(
                                    "No se encontraron resultados para \"$searchQuery\"",
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        }
                        items(filteredResults) { business ->
                            SearchResultItem(business) {
                                navController.navigate("formulario_detail/${business.id}")
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // --- SECCIÓN DE FAVORITOS (Solo si existen) ---
                        if (favoriteBusinesses.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Mis Favoritos",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 24.dp)
                                ) {
                                    items(favoriteBusinesses) { business ->
                                        BusinessCard(
                                            business = business,
                                            modifier = Modifier
                                                .width(160.dp) // Tamaño fijo para la fila horizontal
                                                .clickable { navController.navigate("formulario_detail/${business.id}") }
                                        )
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp), thickness = 0.5.dp, color = Color.LightGray)
                            }
                        }

                        Text(
                            text = "CONVERGE CON NUESTRA COMUNIDAD, RESUELVE, CONECTA, TRANSFORMA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        val categories = listOf(
                            Category(
                                name = "MULTISALUD",
                                imageRes = R.drawable.logodoctor,
                                description = "Todo en beneficio para tu salud y la de los tuyos"
                            ),
                            Category(
                                name = "MULTISMART",
                                imageRes = R.drawable.logosmart,
                                description = "Profesionales listos para resolver cualquier problema"
                            ),
                            Category(
                                name = "MULTISTORE",
                                imageRes = R.drawable.emojitiendas,
                                description = "Todo en un solo lugar"
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CategoryCard(
                                category = categories[0],
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("city_list/${categories[0].name}") }
                            )
                            CategoryCard(
                                category = categories[1],
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("city_list/${categories[1].name}") }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CategoryCard(
                                category = categories[2],
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate("city_list/${categories[2].name}") }
                            )
                            Spacer(modifier = Modifier.weight(1f).padding(start = 16.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "¿Quieres formar parte de nuestra comunidad y hacer crecer tu negocio?",
                            textAlign = TextAlign.Center
                        )
                        ClickableLinkText("¡Únete aquí!") { navController.navigate("form_category_selection") }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "O envíanos tus sugerencias y comentarios",
                            textAlign = TextAlign.Center
                        )
                        ClickableLinkText("Escríbenos aquí") {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:digitalhubec@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Sugerencias para HUB Digital")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) { }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ClickableLinkText("Conoce más sobre nosotros") { navController.navigate("about") }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(business: Formulario, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = business.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (business.especialidad.isNotBlank() && business.especialidad != "N/A") {
                    Text(
                        text = business.especialidad,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (business.ciudades.isNotEmpty()) {
                    Text(
                        text = "📍 ${business.ciudades.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray)
        }
    }
}
