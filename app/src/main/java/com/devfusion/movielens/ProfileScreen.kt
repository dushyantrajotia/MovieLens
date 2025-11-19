package com.devfusion.movielens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Placeholder avatar
            Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(user?.displayName ?: "Anonymous", style = MaterialTheme.typography.titleMedium)
                Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }

        OutlinedButton(onClick = { /* navigate to Edit Profile screen — implement */ }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit profile")
        }

        Button(onClick = { /* open settings — implement theme change, updates */ }) {
            Text("Settings")
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(onClick = {
            FirebaseAuth.getInstance().signOut()
            // Send user back to SignInActivity
            ctx.startActivity(Intent(ctx, SignInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }) {
            Icon(Icons.Default.Logout, contentDescription = "Logout")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}
