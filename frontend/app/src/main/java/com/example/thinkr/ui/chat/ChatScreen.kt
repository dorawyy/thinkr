package com.example.thinkr.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.thinkr.data.models.Document
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    document: Document,
    account: GoogleSignInAccount,
    navController: NavController,
    viewModel: ChatViewModel = koinViewModel()
) {
    val chatState = viewModel.state.collectAsState().value
    val messageText = remember { mutableStateOf(value = "") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (chatState.sessionId.isNotEmpty()) {
            viewModel.loadChatSession(sessionId = chatState.sessionId)
        } else {
            account.id?.let { viewModel.createChatSession(it) }
        }
    }

    // Scroll to bottom when new message is added
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("Chat") },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        viewModel.deleteChatSession()
                        navController.popBackStack()
                    }
                ) {
                    Text(
                        text = "Delete chat"
                    )
                }
            }
        )

        // Messages List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatState.messages) { message ->
                    ChatMessageItem(message)
                }
            }
        }

        // Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()  // Adjusts for the navigation bar
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText.value,
                    onValueChange = { messageText.value = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type a message") },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                IconButton(
                    onClick = {
                        if (messageText.value.isNotBlank()) {
                            viewModel.onSendMessage(messageText.value)
                            messageText.value = ""
                            coroutineScope.launch {
                                if (chatState.messages.isNotEmpty()) {
                                    listState.animateScrollToItem(chatState.messages.size)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
