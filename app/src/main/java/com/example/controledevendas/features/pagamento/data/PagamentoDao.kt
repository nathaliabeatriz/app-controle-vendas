package com.example.controledevendas.features.pagamento.data

import android.renderscript.Long4
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.controledevendas.core.data.relations.PagamentoDetalhes
import kotlinx.coroutines.flow.Flow

@Dao
interface PagamentoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pagamento: Pagamento): Long
    @Update
    suspend fun update(pagamento: Pagamento)
    @Delete
    suspend fun delete(pagamento: Pagamento)

    @Query("SELECT * FROM pagamentos ORDER BY idPagamento DESC")
    @Transaction
    fun getAllPagamentosComDetalhes(): Flow<List<PagamentoDetalhes>>

    @Query("SELECT * FROM pagamentos WHERE idPagamento = :idPagamento")
    suspend fun getPagamentoById(idPagamento: Long): PagamentoDetalhes?
}