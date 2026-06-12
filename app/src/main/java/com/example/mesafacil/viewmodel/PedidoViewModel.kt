package com.example.mesafacil.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.mesafacil.data.model.Pedido
import com.example.mesafacil.data.repository.PedidoRepository

class PedidoViewModel : ViewModel() {

    private val repository = PedidoRepository()

    var mensagem by mutableStateOf("")
        private set

    var carregando by mutableStateOf(false)
        private set

    fun salvarPedido() {

        carregando = true

        val pedido = Pedido(
            cliente = "Matheus",
            total = 50.0,
            status = "novo"
        )

        repository.salvarPedido(
            pedido,
            onSuccess = {
                carregando = false
                mensagem = "Pedido enviado com sucesso!"
            },
            onError = {
                carregando = false
                mensagem = "Erro: $it"
            }
        )
    }
}