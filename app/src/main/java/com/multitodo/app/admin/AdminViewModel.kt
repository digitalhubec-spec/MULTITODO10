package com.multitodo.app.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _formularios = MutableStateFlow<List<Formulario>>(emptyList())
    val formularios: StateFlow<List<Formulario>> = _formularios

    private val _formulario = MutableStateFlow<Formulario?>(null)
    val formulario: StateFlow<Formulario?> = _formulario

    // --- NUEVOS ESTADOS PARA ESTADÍSTICAS ---
    private val _totalVisitasGlobales = MutableStateFlow(0L)
    val totalVisitasGlobales: StateFlow<Long> = _totalVisitasGlobales

    private val _totalNegociosActivos = MutableStateFlow(0)
    val totalNegociosActivos: StateFlow<Int> = _totalNegociosActivos

    fun cargarFormularios(estado: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("formularios")
                    .whereEqualTo("estado", estado)
                    .get()
                    .await()

                val lista = snapshot.documents.mapNotNull { document ->
                    document.toObject(Formulario::class.java)?.copy(id = document.id)
                }
                _formularios.value = lista
                
                // Si estamos viendo aprobados, calculamos totales
                if (estado == "aprobado") {
                    _totalNegociosActivos.value = lista.size
                    _totalVisitasGlobales.value = lista.sumOf { it.visitasTotales }
                }
            } catch (e: Exception) {
                _formularios.value = emptyList()
            }
        }
    }

    fun cargarFormulario(id: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("formularios").document(id).get().await()
                _formulario.value = snapshot.toObject(Formulario::class.java)?.copy(id = snapshot.id)
            } catch (e: Exception) {
                _formulario.value = null
            }
        }
    }

    fun aprobarFormulario(id: String) {
        viewModelScope.launch {
            try {
                db.collection("formularios").document(id).update("estado", "aprobado").await()
                // Recargar para actualizar listas
                cargarFormularios("pendiente")
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun rechazarFormulario(id: String) {
        viewModelScope.launch {
            try {
                db.collection("formularios").document(id).delete().await()
                cargarFormularios("pendiente")
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun actualizarFormulario(formulario: Formulario) {
        if (formulario.id.isEmpty()) return
        viewModelScope.launch {
            try {
                db.collection("formularios").document(formulario.id).set(formulario).await()
            } catch (e: Exception) {
                // Manejar el error
            }
        }
    }
}
