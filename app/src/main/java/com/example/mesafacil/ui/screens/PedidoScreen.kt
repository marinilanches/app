package com.example.mesafacil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mesafacil.data.models.Adicional
import com.example.mesafacil.data.models.ItemPedido
import com.example.mesafacil.data.models.Mesa
import com.example.mesafacil.data.models.MenuItem
import com.example.mesafacil.data.models.Pedido

@Composable
fun PedidoScreen(
    mesa: Mesa,
    pedidos: List<Pedido>,
    menuItems: List<MenuItem>,
    adicionais: List<Adicional>,
    onAdicionarItem: (item: ItemPedido) -> Unit,
    onEnviarPedido: (itens: List<ItemPedido>, observacoes: String) -> Unit,
    onVoltar: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Lanches") }
    var itensSelecionados by remember { mutableStateOf<List<ItemPedido>>(emptyList()) }
    var observacoes by remember { mutableStateOf("") }
    var showAdicionaisDialog by remember { mutableStateOf(false) }
    var itemSelecionado by remember { mutableStateOf<MenuItem?>(null) }
    var itemAdicionaisSelected by remember { mutableStateOf<List<Adicional>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mesa ${mesa.numero} - Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Menu
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                // Categorias
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.15f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(listOf("Lanches", "Bebidas", "Sobremesas")) { category ->
                        Button(
                            onClick = { selectedCategory = category },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCategory == category)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(category)
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Items da categoria
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(menuItems.filter { it.categoria == selectedCategory }) { item ->
                        MenuItemCard(
                            item = item,
                            onClick = {
                                itemSelecionado = item
                                showAdicionaisDialog = true
                            }
                        )
                    }
                }
            }

            Divider(modifier = Modifier
                .fillMaxHeight()
                .width(1.dp))

            // Carrinho
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Itens Selecionados",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Items
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(itensSelecionados) { item ->
                        ItemCarrinho(
                            item = item,
                            onRemove = {
                                itensSelecionados = itensSelecionados.filter { it.id != item.id }
                            }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Total
                Text(
                    text = "Total: R$ ${String.format("%.2f", itensSelecionados.sumOf { it.valorTotal() })}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

                // Observacoes
                OutlinedTextField(
                    value = observacoes,
                    onValueChange = { observacoes = it },
                    label = { Text("Observações") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { itensSelecionados = emptyList() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Limpar")
                    }
                    Button(
                        onClick = { onEnviarPedido(itensSelecionados, observacoes) },
                        modifier = Modifier.weight(1f),
                        enabled = itensSelecionados.isNotEmpty()
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }

    // Dialog de Adicionais
    if (showAdicionaisDialog && itemSelecionado != null) {
        AdicionaisDialog(
            item = itemSelecionado!!,
            adicionais = adicionais,
            onDismiss = { showAdicionaisDialog = false },
            onConfirm = { adicionaisSelecionados, observacao ->
                val novoItem = ItemPedido(
                    id = itemSelecionado!!.id + System.currentTimeMillis(),
                    nome = itemSelecionado!!.nome,
                    categoria = itemSelecionado!!.categoria,
                    valorUnitario = itemSelecionado!!.valor,
                    adicionais = adicionaisSelecionados,
                    observacoes = observacao
                )
                itensSelecionados = itensSelecionados + novoItem
                showAdicionaisDialog = false
                itemSelecionado = null
                itemAdicionaisSelected = emptyList()
            }
        )
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = item.nome,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "R$ ${String.format("%.2f", item.valor)}",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ItemCarrinho(
    item: ItemPedido,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${item.quantidade}x ${item.nome}",
                    fontWeight = FontWeight.Bold
                )
                if (item.adicionais.isNotEmpty()) {
                    Text(
                        text = item.adicionais.map { it.nome }.joinToString(", "),
                        fontSize = 12.sp
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "R$ ${String.format("%.2f", item.valorTotal())}",
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remover")
                }
            }
        }
    }
}

@Composable
fun AdicionaisDialog(
    item: MenuItem,
    adicionais: List<Adicional>,
    onDismiss: () -> Unit,
    onConfirm: (adicionais: List<Adicional>, observacao: String) -> Unit
) {
    var selectedAdicionais by remember { mutableStateOf<List<Adicional>>(emptyList()) }
    var observacao by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.nome) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionais")
                LazyColumn {
                    items(adicionais) { adicional ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedAdicionais.contains(adicional),
                                onCheckedChange = {
                                    selectedAdicionais = if (it) {
                                        selectedAdicionais + adicional
                                    } else {
                                        selectedAdicionais - adicional
                                    }
                                }
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(adicional.nome)
                                Text(
                                    text = "R$ ${String.format("%.2f", adicional.valor)}",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Observações")
                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedAdicionais, observacao) }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
