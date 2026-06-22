package com.multitodo.app.utils

object EcuadorData {
    val provincias = listOf(
        "Azuay", "Bolívar", "Cañar", "Carchi", "Chimborazo", "Cotopaxi", "El Oro", "Esmeraldas", 
        "Galápagos", "Guayas", "Imbabura", "Loja", "Los Ríos", "Manabí", "Morona Santiago", 
        "Napo", "Orellana", "Pastaza", "Pichincha", "Santa Elena", "Santo Domingo de los Tsáchilas", 
        "Sucumbíos", "Tungurahua", "Zamora Chinchipe"
    )

    val ciudadesPrincipales = listOf(
        "BABAHOYO", "MILAGRO", "GUAYAQUIL"
    )

    val ciudadesPorProvincia = mapOf(
        "Azuay" to listOf("Cuenca", "Gualaceo", "Paute", "Sigsig", "Girón", "Nabón", "Pucará", "San Fernando", "Santa Isabel", "Oña", "Chordeleg", "El Pan", "Sevilla de Oro", "Guachapala", "Camilo Ponce Enríquez"),
        "Bolívar" to listOf("Guaranda", "Chillanes", "Chimbo", "Echeandía", "San Miguel", "Caluma", "Las Naves"),
        "Cañar" to listOf("Azogues", "Biblián", "Cañar", "La Troncal", "El Tambo", "Deleg", "Suscal"),
        "Carchi" to listOf("Tulcán", "Bolívar", "Espejo", "Mira", "Montúfar", "San Pedro de Huaca"),
        "Chimborazo" to listOf("Riobamba", "Alausí", "Colta", "Chambo", "Chunchi", "Guamote", "Guano", "Pallatanga", "Penipe", "Cumandá"),
        "Cotopaxi" to listOf("Latacunga", "La Maná", "Pangua", "Pujilí", "Salcedo", "Saquisilí", "Sigchos"),
        "El Oro" to listOf("Machala", "Arenillas", "Atahualpa", "Balsas", "Chilla", "El Guabo", "Huaquillas", "Marcabelí", "Pasaje", "Piñas", "Santa Rosa", "Zaruma", "Las Lajas", "Portovelo"),
        "Esmeraldas" to listOf("Esmeraldas", "Eloy Alfaro", "Muisne", "Quinindé", "San Lorenzo", "Atacames", "Rioverde"),
        "Galápagos" to listOf("San Cristóbal", "Isabela", "Santa Cruz"),
        "Guayas" to listOf("Guayaquil", "Durán", "Milagro", "Daule", "Samborondón", "Alfredo Baquerizo Moreno (Juján)", "Balao", "Balzar", "Colimes", "El Empalme", "El Triunfo", "Naranjal", "Naranjito", "Palestina", "Pedro Carbo", "Santa Lucía", "Salitre", "San Jacinto de Yaguachi", "Playas", "Simón Bolívar", "Marcelino Maridueña", "Nobol", "Lomas de Sargentillo", "Antonio Elizalde (Bucay)", "Isidro Ayora"),
        "Imbabura" to listOf("Ibarra", "Antonio Ante", "Cotacachi", "Otavalo", "Pimampiro", "San Miguel de Urcuquí"),
        "Loja" to listOf("Loja", "Calvas", "Catamayo", "Celica", "Chaguarpamba", "Espíndola", "Gonzanamá", "Macará", "Paltas", "Puyango", "Saraguro", "Sozoranga", "Zapotillo", "Pindal", "Olmedo", "Quilanga"),
        "Los Ríos" to listOf("Babahoyo", "Baba", "Montalvo", "Puebloviejo", "Quevedo", "Urdaneta", "Ventanas", "Vinces", "Palenque", "Buena Fe", "Valencia", "Mocache", "Quinsaloma"),
        "Manabí" to listOf("Portoviejo", "Manta", "Chone", "El Carmen", "Bahía de Caráquez", "Jipijapa", "Montecristi", "Bolívar", "Flavio Alfaro", "Junín", "Paján", "Pichincha", "Rocafuerte", "Santa Ana", "Sucre", "Tosagua", "24 de Mayo", "Pedernales", "Olmedo", "Puerto López", "Jama", "Jaramijó", "San Vicente"),
        "Morona Santiago" to listOf("Macas", "Gualaquiza", "Limón Indanza", "Palora", "Santiago", "Sucúa", "Huamboya", "San Juan Bosco", "Taisha", "Logroño", "Pablo Sexto", "Tiwintza"),
        "Napo" to listOf("Tena", "Archidona", "El Chaco", "Quijos", "Carlos Julio Arosemena Tola"),
        "Orellana" to listOf("Puerto Francisco de Orellana (Coca)", "Agua Rico", "La Joya de los Sachas", "Loreto"),
        "Pastaza" to listOf("Puyo", "Mera", "Santa Clara", "Arajuno"),
        "Pichincha" to listOf("Quito", "Cayambe", "Mejía (Machachi)", "Pedro Moncayo (Tabacundo)", "Rumiñahui (Sangolquí)", "San Miguel de los Bancos", "Pedro Vicente Maldonado", "Puerto Quito"),
        "Santa Elena" to listOf("Santa Elena", "La Libertad", "Salinas"),
        "Santo Domingo de los Tsáchilas" to listOf("Santo Domingo", "La Concordia"),
        "Sucumbíos" to listOf("Nueva Loja (Lago Agrio)", "Cascales", "Cuyabeno", "Gonzalo Pizarro", "Putumayo", "Shushufindi", "Sucumbíos"),
        "Tungurahua" to listOf("Ambato", "Baños de Agua Santa", "Cevallos", "Mocha", "Patate", "Quero", "Pelileo", "Píllaro", "Tisaleo"),
        "Zamora Chinchipe" to listOf("Zamora", "Chinchipe", "Nangaritza", "Yacuambi", "Yantzaza", "El Pangui", "Centinela del Cóndor", "Palanda", "Paquisha")
    )
}
