package com.example.thinkr.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thinkr.ui.payment.PaymentViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = koinViewModel(),
    isSubscribed: Boolean,
    onPressBack: () -> Unit,
    onSelectPremium: () -> Unit
) {
    val profileState by profileViewModel.state.collectAsState()

    profileViewModel.getProfile()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onPressBack) {
                Text(text = "Back")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Username",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = profileState.username)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Email",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = profileState.email)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Subscription Plan",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (profileViewModel.isPremium() || isSubscribed) "Premium" else "Regular",
                textAlign = TextAlign.Center
            )
            if (!profileViewModel.isPremium() && !isSubscribed) {
                TextButton(onClick = onSelectPremium) {
                    Text(text = "Get Premium Plan")
                }
            }
        }
    }
}
