package com.example.controledevendas.features.venda.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.controledevendas.R
import com.example.controledevendas.databinding.VendaFragmentInfoPagamentoBinding
import com.example.controledevendas.features.venda.ui.VendaViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.getValue

class InfoPagamentoVendaFragment: Fragment() {
    private val viewModel: VendaViewModel by hiltNavGraphViewModels(R.id.navigation_make_sale)
    private var _binding: VendaFragmentInfoPagamentoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.venda_fragment_info_pagamento, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        addDateMask()
        addDescontoMask()
        insertListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun insertListeners(){
        binding.inputData.setEndIconOnClickListener {
            openCalendar()
        }

        binding.buttonConfirmar.setOnClickListener {
            val dataString = binding.editTextData.text.toString()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val data: Date? = try {
                sdf.parse(dataString)
            } catch (e: Exception) {
                null
            }
            if(data == null){
                binding.editTextData.error = "Data inválida"
            }else{
                viewModel.saveVenda(data)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.totalComDesconto.collect {
                        val locale = Locale("pt", "BR")
                        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
                        val formattedPreco = currencyFormat.format(it)
                        binding.textViewTotal.text = formattedPreco
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
            }
        }
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

    private fun addDescontoMask() {
        binding.editTextDesconto.addTextChangedListener(object : TextWatcher {
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

                if(valueDouble > viewModel.total.value){
                    parsed = viewModel.total.value.toBigDecimal()
                    valueDouble = parsed.toDouble()
                }

                val formatted = currencyFormat.format(parsed)
                editable?.clear()
                editable?.append(formatted)
                isUpdating = false

                //atualiza o total de produtos
                viewModel.setDesconto(valueDouble)
            }
        })
    }

}