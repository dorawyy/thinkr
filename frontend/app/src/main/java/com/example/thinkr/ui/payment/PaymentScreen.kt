package com.example.thinkr.ui.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

/**
 * Composable that displays the payment screen for premium subscription.
 *
 * This screen allows users to start a free trial of premium features. It shows informational
 * text, a button to start the free trial, and displays any error messages if the subscription
 * process fails.
 *
 * @param paymentViewModel ViewModel that manages payment and subscription processes.
 * @param navToProfile Callback function to navigate back to the profile screen.
 */
@Composable
fun PaymentScreen(
    paymentViewModel: PaymentViewModel = koinViewModel(),
    navToProfile: () -> Unit
) {
    val state by paymentViewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = navToProfile) {
            Text(text = "Back")
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thanks for being an early Thinkr user! Please enjoy a free trial of premium features.",
                textAlign = TextAlign.Center
            )
            TextButton(onClick = { paymentViewModel.subscribeUser(onSuccess = navToProfile) }) {
                Text(
                    text = "Start free trial!"
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}
