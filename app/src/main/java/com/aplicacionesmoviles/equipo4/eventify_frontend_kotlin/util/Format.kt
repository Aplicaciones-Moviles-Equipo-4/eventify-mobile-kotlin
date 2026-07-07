package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.util

import java.text.NumberFormat
import java.util.Locale

private val soles: NumberFormat = NumberFormat.getNumberInstance(Locale("es", "PE")).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

/** Formats an amount as Peruvian soles, e.g. 1500.0 -> "S/ 1,500.00". */
fun formatSoles(amount: Double): String = "S/ ${soles.format(amount)}"
