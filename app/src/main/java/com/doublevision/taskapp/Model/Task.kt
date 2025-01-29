package com.doublevision.taskapp.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Task(
    val titulo: String = "",
    val descricao:String? = "",
    val data_limite: Date? = null,
    val status_conclusao:Boolean = false,
    @ServerTimestamp
    val dataCriacao: Date? = null,
)

