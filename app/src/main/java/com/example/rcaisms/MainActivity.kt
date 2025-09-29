package com.example.rcaisms
    
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.rcaisms.ui.components.PhoneField
import com.example.rcaisms.data.receiver.SmsReceiverManager
import com.example.rcaisms.data.sender.sendMessage
import com.example.rcaisms.ui.theme.RCAIsmsTheme

data class Message(val text: String, val isMine: Boolean)

// MainActivity: typical Compose entry point for an Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // app uses edge-to-edge UI
        setContent {
            RCAIsmsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessageScreen() // main Composable screen
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen() {
    val phoneNumberText = remember { mutableStateOf("") }
    val messageText = remember { mutableStateOf("") }
    val context = LocalContext.current

    // List of messages (mutable state)
    val messages = remember { mutableStateListOf<Message>() }

    // launcher to request permissions at runtime
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        // check whether RECEIVE_SMS permission was granted and show a Toast if not
        val granted = perms[Manifest.permission.RECEIVE_SMS] == true
        if (!granted) {
            Toast.makeText(
                context,
                "RECEIVE_SMS permission is required to receive messages",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Check current permission states
    val receiveSmsGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECEIVE_SMS
    ) == PackageManager.PERMISSION_GRANTED

    val readSmsGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED

    // Launch the permission dialog once if not granted
    val alreadyRequested = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!alreadyRequested.value && (!receiveSmsGranted || !readSmsGranted)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                )
            )
            alreadyRequested.value = true
        }
    }

    // If we are here: set up SmsReceiverManager (assumes this class handles broadcast registration)
    val smsManager = remember { SmsReceiverManager(context) }

    // Register/unregister the receiver using DisposableEffect for proper lifecycle handling
    DisposableEffect(key1 = smsManager) {
        // Callback invoked when an SMS arrives. 'from' may be null.
        val callback: (String?, String) -> Unit = { from, body ->
            // Build display text: "from: body" or just "body" if from is null/empty
            val display = if (!from.isNullOrEmpty()) "$from: $body" else body
            messages.add(Message(display, false)) // add incoming message to state list
        }
        smsManager.register(callback)
        onDispose {
            smsManager.unregister() // ensure receiver is unregistered to avoid leaks
        }
    }

    // Main UI (permissions are assumed granted at this point)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.size(40.dp))

        Text(
            text = "ReadyChatAI SMS",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Image(
            painter = painterResource(R.drawable.w_ua6dzstuoz_lylkzcy6yrmuc8ocb_4),
            contentDescription = null, // consider providing a meaningful description for accessibility
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.size(20.dp))

        // List of received/sent messages
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(messages) { msg ->
                MessageItem(msg)
            }
        }

        // PhoneField is a custom composable — shows masked phone input
        PhoneField(
            phone = phoneNumberText.value,
            mask = "(000) 000 00 00",
            maskNumber = '0',
            label = { Text(text = "Phone Number") },
            onPhoneChanged = { phoneNumberText.value = it },
        )

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },
            label = { Text(text = "Message") },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.size(90.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // On click: send the message
                    sendMessage(phoneNumberText.value, messageText.value, context)
                    // Add the sent message to the list as "mine"
                    messages.add(Message(messageText.value, true))
                    messageText.value = "" // clear input after send
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = "Send Message", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.size(80.dp))
    }
}

@Composable
fun MessageItem(msg: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            // different colors depending on whether the message is mine or incoming
            color = if (msg.isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            tonalElevation = 2.dp
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(8.dp),
                color = if (msg.isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RCAIsmsTheme {
        MessageScreen()
    }
}
