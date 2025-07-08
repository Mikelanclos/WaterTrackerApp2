package com.bignerdranch.android.watertrackerapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WaterTrackerApp() {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    val totalOuncesFlow = dataStore.totalOuncesFlow.collectAsState(initial = 0)
    val dailyGoalFlow = dataStore.dailyGoalFlow.collectAsState(initial = 64)
    val lastUpdatedFlow = dataStore.lastUpdatedFlow.collectAsState(initial = 0L)

    var goalInput by remember { mutableStateOf("") }

    val totalOunces = totalOuncesFlow.value
    val dailyGoal = dailyGoalFlow.value
    val lastUpdated = lastUpdatedFlow.value

    val rawProgress = if (dailyGoal > 0) {
        (totalOunces.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(targetValue = rawProgress, label = "progress")

    val lastUpdatedFormatted = remember(lastUpdated) {
        if (lastUpdated == 0L) ""
        else {
            val sdf = SimpleDateFormat("MMMM d, yyyy – h:mm a", Locale.getDefault())
            "Last Updated: ${sdf.format(Date(lastUpdated))}"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💧 Water Tracker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "💡 Please remember to hydrate daily for a healthier lifestyle!",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Medium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progress: $totalOunces oz / $dailyGoal oz",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    if (totalOunces >= dailyGoal) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🎉 Goal reached!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (lastUpdatedFormatted.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lastUpdatedFormatted,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 8, 16, 24).forEach { amount ->
                            Button(
                                onClick = {
                                    scope.launch {
                                        dataStore.saveTotalOunces((totalOunces + amount).coerceAtMost(dailyGoal))
                                    }
                                },
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("💦 $amount oz", fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch { dataStore.saveTotalOunces(0) }
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("🔁 Reset", fontSize = 16.sp)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it },
                        label = { Text("Set Daily Goal (oz)", fontSize = 16.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val parsed = goalInput.toIntOrNull()
                            if (parsed != null && parsed > 0) {
                                scope.launch {
                                    dataStore.saveDailyGoal(parsed)
                                    if (totalOunces > parsed) {
                                        dataStore.saveTotalOunces(parsed)
                                    }
                                    goalInput = ""
                                }
                            }
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("✅ Update Goal", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
