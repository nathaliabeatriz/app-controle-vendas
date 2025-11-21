package com.example.controledevendas.features.produto.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.controledevendas.core.data.relations.ProdutoMovimentacao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ProdutoRepository @Inject constructor(private val produtoDao: ProdutoDao,
                                            @ApplicationContext private val context: Context) {
    val allProdutos: Flow<List<Produto>> = produtoDao.getAllProdutos()
    val movimentacoesByProduto: Flow<List<ProdutoMovimentacao?>> = produtoDao.getMovimentacoesByProduto()

    suspend fun insert(produto: Produto): Long{
        return produtoDao.insert(produto)
    }

    suspend fun delete(produto: Produto){
        produtoDao.delete(produto)
    }

    suspend fun update(produto: Produto){
        produtoDao.update(produto)
    }

    fun getProdutoById(id: Long): Flow<Produto?> {
        return produtoDao.getProdutoById(id)
    }

    fun uriToBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    fun saveImage(bitmap: Bitmap, productName: String): String? {
        var nomeArquivo: String? = null

        nomeArquivo = "IMG_${productName}_${System.currentTimeMillis()}.jpg"

        // 'context.filesDir' é o diretório interno e privado do seu app
        val file = File(context.filesDir, nomeArquivo)

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return nomeArquivo
    }

    fun deleteImage(fileName: String?){
        if (fileName != null) {
            try{
                val file = File(context.filesDir, fileName)
                if(file.exists()){
                    file.delete()
                }

            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}