package com.multitodo.app.utils

import android.content.Context
import android.content.SharedPreferences

class FavoritesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    fun toggleFavorite(businessId: String) {
        val favorites = getFavorites().toMutableSet()
        if (favorites.contains(businessId)) {
            favorites.remove(businessId)
        } else {
            favorites.add(businessId)
        }
        prefs.edit().putStringSet("favorite_ids", favorites).apply()
    }

    fun isFavorite(businessId: String): Boolean {
        return getFavorites().contains(businessId)
    }

    fun getFavorites(): Set<String> {
        return prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
    }
}
