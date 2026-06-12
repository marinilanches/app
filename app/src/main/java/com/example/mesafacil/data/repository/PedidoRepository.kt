package com.example.mesafacil.data.repository

import com.example.mesafacil.data.model.Pedido
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PedidoRepository {

    private val db = Firebase.firestore

    fun salvarPedido(
        pedido: Pedido,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onError(it.message ?: "Erro desconhecido")
            }
    }
}