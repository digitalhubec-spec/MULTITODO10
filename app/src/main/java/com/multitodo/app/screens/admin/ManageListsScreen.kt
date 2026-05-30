package com.multitodo.app.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageListsViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities = _cities.asStateFlow()

    private val _specialties = MutableStateFlow<List<String>>(emptyList())
    val specialties = _specialties.asStateFlow()
    
    private val _professions = MutableStateFlow<List<String>>(emptyList())
    val professions = _professions.asStateFlow()

    init {
        loadLists()
    }

    private fun loadLists() {
        viewModelScope.launch {
            db.collection("config").document("cities").get().addOnSuccessListener { doc ->
                _cities.value = doc.get("items") as? List<String> ?: emptyList()
            }
            db.collection("config").document("specialties").get().addOnSuccessListener { doc ->
                _specialties.value = doc.get("items") as? List<String> ?: emptyList()
            }
             db.collection("config").document("professions").get().addOnSuccessListener { doc ->
                _professions.value = doc.get("items") as? List<String> ?: emptyList()
            }
        }
    }

    fun addItem(collection: String, item: String, onResult: (Boolean, String) -> Unit) {
        if (item.isBlank()) {
            onResult(false, "El campo no puede estar vacío")
            return
        }
        val docRef = db.collection("config").document(collection)
        docRef.update("items", FieldValue.arrayUnion(item.uppercase()))
            .addOnSuccessListener {
                loadLists() // Refresh lists
                onResult(true, "¡'''$item''' añadido con éxito!")
            }
            .addOnFailureListener { e ->
                // If the document or field doesn't exist, create it
                docRef.set(mapOf("items" to listOf(item.uppercase())))
                    .addOnSuccessListener {
                        loadLists() // Refresh lists
                        onResult(true, "¡'''$item''' añadido con éxito!")
                    }
                    .addOnFailureListener { e2 ->
                        onResult(false, "Error: ${e2.message}")
                    }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageListsScreen(
    navController: NavController,
    viewModel: ManageListsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val cities by viewModel.cities.collectAsState()
    val specialties by viewModel.specialties.collectAsState()
    val professions by viewModel.professions.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Listas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ListManagementSection(
                    title = "Ciudades",
                    items = cities,
                    onAddItem = { newItem ->
                        viewModel.addItem("cities", newItem) { success, message ->
                             Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            item {
                ListManagementSection(
                    title = "Especialidades (MULTISALUD)",
                    items = specialties,
                    onAddItem = { newItem ->
                        viewModel.addItem("specialties", newItem) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            item {
                ListManagementSection(
                    title = "Profesiones (MULTISMART)",
                    items = professions,
                    onAddItem = { newItem ->
                        viewModel.addItem("professions", newItem) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ListManagementSection(
    title: String,
    items: List<String>,
    onAddItem: (String) -> Unit
) {
    var newItem by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = newItem,
            onValueChange = { newItem = it },
            label = { Text("Añadir nuevo item") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = {
                onAddItem(newItem)
                newItem = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Añadir")
        }

        Spacer(Modifier.height(8.dp))

        Text("Items existentes:", fontWeight = FontWeight.Medium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (items.isEmpty()) {
                    Text("No hay items en esta lista.")
                } else {
                    items.forEach { item ->
                        Text("- $item", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
