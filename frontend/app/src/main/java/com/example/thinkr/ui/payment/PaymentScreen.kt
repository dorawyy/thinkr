package com.example.thinkr.ui.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaymentScreen(
    paymentViewModel: PaymentViewModel = koinViewModel(),
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val state by paymentViewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onBack) {
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
            TextButton(
                onClick = {
                    paymentViewModel.subscribeUser()
                    onConfirm()
                }
            ) {
                Text(
                    text = "Start free trial!"
                )
            }
//            Text(text = "Payment information")
//            Spacer(modifier = Modifier.height(16.dp))
//            OutlinedTextField(
//                value = state.cardNumber,
//                onValueChange = { viewModel.onCardNumberChange(it) },
//                label = { Text(text = "Card Number") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = state.cardExpiration,
//                onValueChange = { viewModel.onExpirationChange(it) },
//                label = { Text(text = "MM/YY") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = state.cardCvc,
//                onValueChange = { viewModel.onCvcChange(it) },
//                label = { Text(text = "CVC") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = state.cardBillingAddress,
//                onValueChange = { viewModel.onBillingAddressChange(it) },
//                label = { Text(text = "Billing Address") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            TextButton(onClick = onConfirm) {
//                Text(text = "Pay")
//            }
        }
    }
}
