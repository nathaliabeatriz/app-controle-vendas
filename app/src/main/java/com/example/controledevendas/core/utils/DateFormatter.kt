package com.example.controledevendas.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(data: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return formatter.format(data)
}