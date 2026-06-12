package com.example.mesafacil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.mesafacil.ui.screens.LoginScreen
import com.example.mesafacil.ui.screens.MesasScreen
import com.example.mesafacil.ui.theme.MesaFacilTheme
import com.example.mesafacil.ui.viewmodels.AuthViewModel
import com.example.mesafacil.ui.viewmodels.AuthState
import com.example.mesafacil.ui.viewmodels.MesaViewModel
import com.example.mesafacil.utils.FirebaseInitializer

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private val mesaViewModel: MesaViewModel by lazy {
        ViewModelProvider(this).get(MesaViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseInitializer.initialize(this)

        setContent {
            MesaFacilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        authViewModel = authViewModel,
                        mesaViewModel = mesaViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    mesaViewModel: MesaViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val mesas by mesaViewModel.mesas.collectAsState()

    when (authState) {
        is AuthState.Success -> {
            if (currentUser != null) {
                MesasScreen(
                    mesas = mesas,
                    onMesaClick = { },
                    onAbrirMesa = { mesa, pessoas ->
                        mesaViewModel.abrirMesa(mesa.id, pessoas, currentUser!!.id, currentUser!!.name)
                    },
                    onUnirMesas = { selectedMesas ->
                        mesaViewModel.unirMesas(
                            selectedMesas.map { it.id },
                            currentUser!!.id,
                            currentUser!!.name
                        )
                    },
                    onLogout = { authViewModel.logout() }
                )
            }
        }
        else -> {
            LoginScreen(
                onLoginSuccess = { },
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                isLoading = authState is AuthState.Loading,
                errorMessage = (authState as? AuthState.Error)?.message
            )
        }
    }
}
