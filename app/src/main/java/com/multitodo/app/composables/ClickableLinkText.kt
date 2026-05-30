package com.multitodo.app.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun ClickableLinkText(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(vertical = 4.dp)) {
        Text(
            text = text,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Bold
        )
    }
}
