package com.multitodo.app.listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.multitodo.app.admin.Formulario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListingsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _formularios = MutableStateFlow<List<Formulario>>(emptyList())
    val formularios: StateFlow<List<Formulario>> = _formularios

    /**
     * Carga los formularios desde Firestore que coinciden con la categoría, ciudad y tienen estado "aprobado".
     */
    fun cargarFormulariosAprobados(categoria: String, ciudad: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("formularios")
                    .whereEqualTo("categoria", categoria)
                    .whereEqualTo("ciudad", ciudad)
                    .whereEqualTo("estado", "aprobado")
                    .get()
                    .await()

                _formularios.value = snapshot.documents.mapNotNull { document ->
                    document.toObject(Formulario::class.java)?.copy(id = document.id)
                }
            } catch (e: Exception) {
                // En un caso real, deberías loggear el error o mostrar un mensaje al usuario
                _formularios.value = emptyList()
            }
        }
    }
}
