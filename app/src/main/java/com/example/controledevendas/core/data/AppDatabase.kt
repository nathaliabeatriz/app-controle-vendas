package com.example.controledevendas.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.cliente.data.ClienteDao

@Database(entities = [Cliente::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
}