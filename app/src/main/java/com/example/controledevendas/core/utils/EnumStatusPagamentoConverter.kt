package com.example.controledevendas.core.utils

import androidx.room.TypeConverter
import com.example.controledevendas.core.data.enums.StatusPagamento

object EnumStatusPagamentoConverter {
    @TypeConverter
    fun fromStatus(status: StatusPagamento): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(statusString: String): StatusPagamento {
        return try {
            enumValueOf<StatusPagamento>(statusString)
        } catch (e: IllegalArgumentException) {
            StatusPagamento.PENDENTE
        }
    }
}