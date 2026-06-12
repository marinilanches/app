package com.example.mesafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mesafacil.viewmodel.PedidoViewModel

@Composable
fun PedidoScreen(
    viewModel: PedidoViewModel = viewModel()
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text("Pedido da Mesa")

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            viewModel.salvarPedido()
        }) {
            Text("Enviar pedido")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(viewModel.mensagem)
    }
}