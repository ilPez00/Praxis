package com.praxis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Identity verification screen (face-detection stub).
 * Ported from praxis_webapp IdentityVerificationPage.tsx.
 *
 * The actual face-detection pipeline (CameraX + ML Kit TinyFaceDetector) is
 * deferred to a future sprint. For now the screen simulates verification
 * after a 2-second delay so the full UX flow can be tested.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityVerificationScreen(
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var status by remember { mutableStateOf(VerificationStatus.IDLE) }

    // Simulate face analysis completing after 2 s
    LaunchedEffect(status) {
        if (status == VerificationStatus.IN_PROGRESS) {
            delay(2_000)
            status = VerificationStatus.VERIFIED
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Identity Verification") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    InfoButton(
                        title = "Identity Verification",
                        description = "A quick on-device face scan that earns you a verified ✓ badge, visible to potential partners. Verification boosts match quality and community trust. All analysis runs locally — no biometric data ever leaves your phone."
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Camera preview placeholder
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(Color(0xFF1C1C1E), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    VerificationStatus.IDLE -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📸", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Camera preview", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Text(
                            "(CameraX integration — next sprint)",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 10.sp
                        )
                    }
                    VerificationStatus.IN_PROGRESS -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Analysing face…", color = Color.White, fontSize = 14.sp)
                    }
                    VerificationStatus.VERIFIED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF34C759)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Face Verified!",
                            color = Color(0xFF34C759),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    VerificationStatus.FAILED -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Verification failed", color = Color.Red, fontSize = 16.sp)
                        Text("Please try again", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Instructions card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Face Verification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    listOf(
                        "📸 Position your face in the frame",
                        "💡 Ensure good lighting",
                        "👀 Look directly at the camera",
                        "🔒 Analysis runs on-device"
                    ).forEach { tip ->
                        Text(
                            text = tip,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action button
            when (status) {
                VerificationStatus.IDLE -> Button(
                    onClick = { status = VerificationStatus.IN_PROGRESS },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Start Verification", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }

                VerificationStatus.IN_PROGRESS -> Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = false
                ) { Text("Verifying…", fontSize = 16.sp) }

                VerificationStatus.VERIFIED -> Button(
                    onClick = onVerified,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759))
                ) { Text("Continue ✓", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }

                VerificationStatus.FAILED -> Button(
                    onClick = { status = VerificationStatus.IDLE },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Try Again", fontSize = 16.sp) }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

enum class VerificationStatus { IDLE, IN_PROGRESS, VERIFIED, FAILED }
