package com.example.mesafacil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mesafacil.data.models.ItemPedido
import com.example.mesafacil.data.models.Pedido
import com.example.mesafacil.data.models.PedidoStatus
import com.example.mesafacil.data.repositories.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {
    private val pedidoRepository = PedidoRepository()

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPedidosByMesa(mesaId: String) {
        viewModelScope.launch {
            pedidoRepository.getPedidosByMesa(mesaId).collectLatest { pedidos ->
                _pedidos.value = pedidos
            }
        }
    }

    fun criarPedido(
        mesaId: String,
        numeroMesa: Int,
        itens: List<ItemPedido>,
        observacoes: String,
        garcomId: String,
        garcomNome: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = pedidoRepository.createPedido(
                mesaId, numeroMesa, itens, observacoes, garcomId, garcomNome
            )
            result.onFailure { error ->
                _error.value = error.message
            }
            _loading.value = false
        }
    }

    fun atualizarStatusPedido(pedidoId: String, status: PedidoStatus) {
        viewModelScope.launch {
            val result = pedidoRepository.updatePedidoStatus(pedidoId, status)
            result.onFailure { error ->
                _error.value = error.message
            }
        }
    }

    fun deletarPedido(pedidoId: String) {
        viewModelScope.launch {
            val result = pedidoRepository.deletePedido(pedidoId)
            result.onFailure { error ->
                _error.value = error.message
            }
        }
    }
}
