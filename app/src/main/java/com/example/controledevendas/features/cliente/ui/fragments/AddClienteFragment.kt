package com.example.controledevendas.features.cliente.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.controledevendas.R
import com.example.controledevendas.databinding.FragmentAddClientBinding
import com.example.controledevendas.features.cliente.ui.ClienteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddClienteFragment: Fragment() {
    private val viewModel: ClienteViewModel by activityViewModels()
    private val args: AddClienteFragmentArgs by navArgs()
    private var _binding: FragmentAddClientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_client, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.loadCliente(args.idCliente)
        phoneNumberMask()
        insertClientListener()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        // Observa o LiveData do cliente no ViewModel
        viewModel.cliente.observe(viewLifecycleOwner) { cliente ->
            cliente?.let {
                // Se o cliente não for nulo, preenche os campos da UI
                binding.editTextName.setText(it.nome)
                binding.editTextPhone.setText(it.telefone)

                // Altera o texto do título e do botão para o modo de edição
                binding.includeTitle.title = getString(R.string.editar_cliente) // Supondo que você tenha um component_title.xml com um TextView com id 'title'
                binding.buttonAddClient.text = getString(R.string.atualizar) // Crie este recurso de string
            }
        }
    }

    fun insertClientListener(){
        binding.buttonAddClient.setOnClickListener {
            val nome = binding.editTextName.text.toString()
            val telefone = binding.editTextPhone.text.toString()
            val cleanPhone = telefone.replace("[^0-9]".toRegex(), "")
            if(nome.isEmpty()){
                binding.editTextName.error = "Nome inválido"
            }
            else if(cleanPhone.length != 11){
                binding.editTextPhone.error = "Telefone deve conter 11 números"
            }
            else{
                viewModel.saveCliente(nome, telefone)
                findNavController().popBackStack()
            }
        }
    }

    fun phoneNumberMask(){
        binding.editTextPhone.addTextChangedListener(object : TextWatcher{
            private var editing = false
            private var oldText = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val cleanText = s.toString().replace("[^0-9]".toRegex(), "")
                if (editing || cleanText == oldText) {
                    return
                }
                editing = true

                val formattedText = StringBuilder()
                var index = 0
                for (char in "(##) #####-####".toCharArray()) {
                    if (index >= cleanText.length) {
                        break
                    }
                    if (char == '#') {
                        formattedText.append(cleanText[index])
                        index++
                    } else {
                        formattedText.append(char)
                    }
                }

                oldText = cleanText
                binding.editTextPhone.setText(formattedText.toString())
                binding.editTextPhone.setSelection(formattedText.length)

                editing = false
            }
        })
    }
}