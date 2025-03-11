package com.example.thinkr.ui.landing

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.thinkr.R
import com.example.thinkr.app.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import org.koin.androidx.compose.koinViewModel

@Composable
fun LandingScreen(
    viewModel: LandingScreenViewModel = koinViewModel(),
    navigateToHome: () -> Unit,
    onSignOut: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            viewModel.onGoogleSignInResult(account, onSignOut)
            Log.d("ServerScreen", "Sign-in successful: ${account?.email}")
        } catch (e: ApiException) {
            Log.e("ServerScreen", "Sign-in failed: ${e.statusCode}", e)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkSignedIn(navigateToHome)
    }

    if (GoogleSignIn.getLastSignedInAccount(LocalContext.current) != null && !viewModel.userSignedOut()) {
        GoogleSignIn.getLastSignedInAccount(LocalContext.current)
            ?.let { viewModel.onGoogleSignInResult(it, onSignOut) }
    }

    LaunchedEffect(state.value.isAuthenticated) {
        if (state.value.isAuthenticated) {
            navigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
            .testTag(tag = "landing_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(100.dp)
                .padding(bottom = 20.dp)
                .testTag(tag = "app_logo")
        )
        Text(
            text = "Welcome to Thinkr",
            modifier = Modifier.testTag(tag = "welcome_text")
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (context is MainActivity) {
                    signInLauncher.launch(context.googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .testTag(tag = "google_sign_in_button")
        ) {
            Text(text = "Sign in with Google")
        }
        Text(
            text = state.value.error ?: "",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (state.value.isLoading) {
            CircularProgressIndicator()
        }
    }
}
