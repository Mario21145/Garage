package com.example.garage.datasets

import org.intellij.lang.annotations.Language
import java.util.Locale

class Dataset {


    val language = Locale.getDefault().language

    val typeFuels: List<String> by lazy {
        if (language == "it") {
            listOf("benzina", "ibrida completa", "ibrida leggera", "ibrida plug-in", "diesel")
        } else {
            listOf("gasoline", "full-hybrid", "mild-hybrid", "plugin-hybrid", "diesel")
        }
    }
}