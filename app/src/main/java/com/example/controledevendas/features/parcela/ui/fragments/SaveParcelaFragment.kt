package com.example.controledevendas.features.parcela.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.controledevendas.R
import com.example.controledevendas.databinding.ParcelaFragmentSaveBinding
import com.example.controledevendas.features.parcela.ui.ParcelaViewModel
import com.example.controledevendas.features.venda.ui.fragments.InfoPagamentoVendaFragmentDirections
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.getValue
import kotlin.text.replace

@AndroidEntryPoint
class SaveParcelaFragment: Fragment() {
    private val viewModel: ParcelaViewModel by viewModels()
    private var _binding: ParcelaFragmentSaveBinding? = null
    private val binding get() = _binding!!
    private val args: SaveParcelaFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.parcela_fragment_save, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadValorPendente(args.idPagamento)
        addDateMask()
        addValueMask()
        insertListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun insertListeners(){
        binding.inputData.setEndIconOnClickListener {
            openCalendar()
        }
        binding.buttonAddParcela.setOnClickListener {
            val meioPagamento = binding.autoCompleteTextViewMeioPagamento.text.toString()
            val dataString = binding.editTextData.text.toString()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val data: Date? = try {
                sdf.parse(dataString)
            } catch (e: Exception) {
                null
            }
            val valorPagoString = binding.editTextValor.text.toString().replace(Regex("[^\\d]"), "")
            val valorPago = if (valorPagoString.isEmpty()) 0.0 else valorPagoString.toDouble() / 100.0
            if(data == null || meioPagamento.isEmpty() || valorPago <= 0){
                if(data == null) binding.editTextData.error = "Data inválida"
                if(meioPagamento.isEmpty()) binding.autoCompleteTextViewMeioPagamento.error = "Selecione um meio de pagamento"
                if(valorPago.toDouble() <= 0) binding.editTextValor.error = "Valor deve ser maior que 0"
            }
            else{
                viewModel.saveParcela(args.idPagamento, data, valorPago.toDouble(), meioPagamento)
                findNavController().popBackStack()
            }
        }
    }

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.meiosPagamento.collect {
                        populateSelectMeioPagamento()
                    }
                }
                launch {
                    viewModel.savedStatus.collect { saved ->
                        if (saved) {
                            val action = InfoPagamentoVendaFragmentDirections.actionVendaInfoPagamentoToNavigationListProducts()
                            findNavController().navigate(action)
                            viewModel.navigationConcluded()
                        }
                    }
                }
                launch {
                    viewModel.valorPendenteUi.collect {
                        val locale = Locale("pt", "BR")
                        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
                        val formattedPreco = currencyFormat.format(viewModel.valorPendenteUi.value)
                        binding.textViewValorPendente.text = formattedPreco
                    }
                }
            }
        }
    }

    private fun populateSelectMeioPagamento(){
        val options = viewModel.meiosPagamento.value.map{
            it.descricao
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)

        binding.autoCompleteTextViewMeioPagamento.setAdapter(adapter)
    }

    private fun openCalendar() {
        // Cria o DatePicker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Quando o usuário escolhe uma data e clica em "OK"
        datePicker.addOnPositiveButtonClickListener { selectionLong ->
            // O 'selectionLong' é o timestamp em UTC.

            val timeZone = TimeZone.getTimeZone("UTC")
            val offset = timeZone.getOffset(Date().time) * -1

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            sdf.timeZone = timeZone

            val dateFormatted = sdf.format(Date(selectionLong))

            binding.editTextData.setText(dateFormatted)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG")
    }

    private fun addDateMask() {
        binding.editTextData.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal = java.util.Calendar.getInstance()

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    var clean = s.toString().replace(Regex("[^\\d.]|\\."), "")
                    val cleanC = current.replace(Regex("[^\\d.]|\\."), "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                    } else {
                        // Lógica para validar dia/mês (opcional)
                        var day = Integer.parseInt(clean.substring(0, 2))
                        var mon = Integer.parseInt(clean.substring(2, 4))
                        var year = Integer.parseInt(clean.substring(4, 8))

                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal.set(java.util.Calendar.MONTH, mon - 1)
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal.set(java.util.Calendar.YEAR, year)

                        day = if (day > cal.getActualMaximum(java.util.Calendar.DATE)) cal.getActualMaximum(java.util.Calendar.DATE) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8))

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    binding.editTextData.setText(current)
                    binding.editTextData.setSelection(if (sel < current.length) sel else current.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun addValueMask() {
        binding.editTextValor.addTextChangedListener(object : TextWatcher {
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
                var parsed = if (cleanString.isEmpty()) {
                    BigDecimal.ZERO
                } else {
                    BigDecimal(cleanString).divide(BigDecimal(100), 2, BigDecimal.ROUND_FLOOR)
                }

                var valueDouble = parsed.toDouble()

                if(valueDouble > viewModel.valorPendente.value){
                    parsed = viewModel.valorPendente.value.toBigDecimal()
                    valueDouble = parsed.toDouble()
                }

                val formatted = currencyFormat.format(parsed)
                editable?.clear()
                editable?.append(formatted)
                isUpdating = false

                viewModel.setValorPendenteUi(valueDouble)
            }
        })
    }
}