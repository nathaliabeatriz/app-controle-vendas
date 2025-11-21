package com.example.controledevendas.features.produto.ui.fragments

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.controledevendas.R
import com.example.controledevendas.databinding.ProdutoFragmentSaveBinding
import com.example.controledevendas.features.produto.ui.ProdutoViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlin.getValue
import kotlin.text.append
import kotlin.text.format

@AndroidEntryPoint
class SaveProdutoFragment : Fragment(){
    private val viewModel: ProdutoViewModel by viewModels()
    private val args: SaveProdutoFragmentArgs by navArgs()
    private var _binding: ProdutoFragmentSaveBinding? = null
    private val binding get() = _binding!!
    private var cameraImageUri: Uri? = null
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()) { uri: Uri? ->
        //Verifica se o usuário realmente selecionou uma imagem
        uri?.let { imagemUri ->
            Glide.with(this)
                .load(imagemUri)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.imagePreview) // (o ID do ImageView)

            viewModel.setImagemSelecionada(imagemUri)
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        // O callback da câmera só diz se foi sucesso (true) ou cancelado (false)
        if (success) {
            cameraImageUri?.let {
                binding.imagePreview.setImageURI(it)
                viewModel.setImagemSelecionada(it)
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permissão foi dada agora. Tente abrir a câmera de novo.
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.produto_fragment_save, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProduto(args.idProduto)
        addPriceMask()
        setupObservers()
        insertProductListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // como a interface carrega mais rápido que uma consulta no banco deve existir um observador para atualizar os campos de produto
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.produto.collect { produto ->
                        produto?.let {
                            // Se o produto não for nulo, preenche os campos da UI
                            binding.editTextName.setText(it.nome)
                            val locale = Locale("pt", "BR")
                            val currencyFormat = NumberFormat.getCurrencyInstance(locale)
                            val formattedPreco = currencyFormat.format(it.preco)
                            binding.editTextPreco.setText(formattedPreco)
                            binding.editTextDescricao.setText(it.descricao)
                            if (it.urlImg != null) {
                                val context = binding.root.context
                                val file = File(context.filesDir, it.urlImg)

                                Glide.with(context)
                                    .load(file) // Carrega o ARQUIVO
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(binding.imagePreview)
                            }

                            // Altera o texto do título e do botão para o modo de edição
                            binding.includeTitle.title = getString(R.string.editar_produto)
                            binding.buttonAddClient.text = getString(R.string.atualizar)
                        }
                    }
                }

                launch {
                    viewModel.savedStatus.collect { saved ->
                        if (saved) {
                            showChooseRegistrarMovimDialog()
                        }
                    }
                }
            }
        }
    }

    fun insertProductListener(){
        binding.buttonAddClient.setOnClickListener {
            val nome = binding.editTextName.text.toString()
            val precoString = binding.editTextPreco.text.toString().replace(Regex("[^\\d]"), "")
            val preco = if (precoString.isEmpty()) 0.0 else precoString.toDouble() / 100.0
            val descricao = binding.editTextDescricao.text.toString()
            if(nome.isEmpty()){
                binding.editTextName.error = "Nome inválido"
            }
            else{
                viewModel.saveProduto(nome, preco, descricao)
            }
        }
        binding.buttonSelecionarFoto.setOnClickListener {
            showChooseImageDialog()
        }
    }

    private fun showChooseRegistrarMovimDialog(){
        val options = arrayOf("Sim", "Não")

        AlertDialog.Builder(requireContext())
            .setTitle("Deseja adicionar uma entrada para este produto?")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        val action = SaveProdutoFragmentDirections.actionSaveProductToSaveEntrada(
                            idProduto = viewModel.produto.value!!.idProduto
                        )
                        findNavController().navigate(action)
                    }
                    1 -> {
                        dialog.cancel()
                        findNavController().popBackStack()
                        viewModel.navigationConcluded()
                    }
                }
            }
            .setOnCancelListener{
                findNavController().popBackStack()
                viewModel.navigationConcluded()
            }
            .show()
    }

    private fun showChooseImageDialog() {
        val opcoes = arrayOf("Tirar Foto", "Escolher da Galeria")

        AlertDialog.Builder(requireContext())
            .setTitle("Selecionar Imagem")
            .setItems(opcoes) { dialog, which ->
                when (which) {
                    0 -> {
                        checkCameraPermission()
                    }
                    1 -> {
                        galleryLauncher.launch("image/*")
                    }
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        when {
            // 1. Permissão já concedida
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permissão já existe, abra a câmera
                launchCamera()
            }
            // Permissão ainda não foi pedida
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        // Cria um arquivo temporário no cache do app
        val file = File(requireContext().cacheDir, "images/IMG_${System.currentTimeMillis()}.jpg")
        if (file.parentFile?.exists() == false) file.parentFile?.mkdirs()

        // Cria a URI para a câmera usando o FileProvider
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        // Lança a câmera, dizendo a ela para salvar a foto nessa uri
        cameraLauncher.launch(cameraImageUri)
    }

    private fun addPriceMask() {
        binding.editTextPreco.addTextChangedListener(object : TextWatcher {
            private val locale = Locale("pt", "BR")
            private val currencyFormat = NumberFormat.getCurrencyInstance(locale)
            private var isUpdating = false

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                if (isUpdating) {
                    return
                }
                isUpdating = true

                val cleanString = editable.toString().replace(Regex("[^\\d]"), "")

                // Converte a string limpa para BigDecimal para cálculos precisos
                val parsed = if (cleanString.isEmpty()) {
                    BigDecimal.ZERO
                } else {
                    BigDecimal(cleanString).divide(BigDecimal(100), 2, BigDecimal.ROUND_FLOOR)
                }
                val formatted = currencyFormat.format(parsed)
                editable?.clear()
                editable?.append(formatted)
                isUpdating = false
            }
        })
    }
}