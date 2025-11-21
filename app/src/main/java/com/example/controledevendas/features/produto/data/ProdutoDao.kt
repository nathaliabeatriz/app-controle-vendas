package com.example.controledevendas.features.produto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.controledevendas.core.data.relations.ProdutoMovimentacao
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(produto: Produto): Long

    @Delete
    suspend fun delete(produto: Produto)

    @Update
    suspend fun update(produto: Produto)

    @Query("SELECT * FROM produtos ORDER BY nome ASC")
    fun getAllProdutos(): Flow<List<Produto>>

    @Query("SELECT * FROM produtos WHERE idProduto = :id")
    fun getProdutoById(id: Long): Flow<Produto?>

    @Transaction
    @Query("SELECT * FROM produtos")
    fun getMovimentacoesByProduto(): Flow<List<ProdutoMovimentacao?>>
}