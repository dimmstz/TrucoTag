package com.dimm.trucotag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Felt = Color(0xFF073D2D)
private val FeltDark = Color(0xFF05291F)
private val Gold = Color(0xFFE7BD62)
private val Cream = Color(0xFFFFF4D9)
private val Muted = Color(0xFFB9CDBF)
private val Red = Color(0xFFB94139)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("trucotag", MODE_PRIVATE)
        setContent {
            MaterialTheme {
                TrucoTagApp(
                    initialUs = prefs.getInt("us", 0),
                    initialThem = prefs.getInt("them", 0),
                    initialTarget = prefs.getInt("target", 12),
                    onSave = { us, them, target ->
                        prefs.edit().putInt("us", us).putInt("them", them)
                            .putInt("target", target).apply()
                    }
                )
            }
        }
    }
}

@Composable
private fun TrucoTagApp(
    initialUs: Int,
    initialThem: Int,
    initialTarget: Int,
    onSave: (Int, Int, Int) -> Unit
) {
    var us by remember { mutableIntStateOf(initialUs) }
    var them by remember { mutableIntStateOf(initialThem) }
    var target by remember { mutableIntStateOf(initialTarget) }
    var showHistory by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<String>() }

    fun persist() = onSave(us, them, target)
    fun addPoint(teamUs: Boolean, amount: Int) {
        if (teamUs) us = (us + amount).coerceIn(0, target)
        else them = (them + amount).coerceIn(0, target)
        persist()
        if (us == target || them == target) {
            val winner = if (us == target) "Nós" else "Eles"
            history.add(0, "$winner venceu • $us × $them • mão de $target")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A4935), FeltDark, Color(0xFF031B15))))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TRUCOTAG", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("MARCADOR DE TRUCO", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                }
                Row {
                    IconButton(onClick = { showHistory = true }) {
                        Icon(Icons.Default.History, "Histórico", tint = Cream)
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, "Configurações", tint = Cream)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MÃO ATÉ", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.size(10.dp))
                Text("$target", color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScorePanel(
                    modifier = Modifier.weight(1f),
                    name = "NÓS",
                    score = us,
                    accent = Gold,
                    onAdd = { addPoint(true, 1) },
                    onRemove = { addPoint(true, -1) }
                )
                ScorePanel(
                    modifier = Modifier.weight(1f),
                    name = "ELES",
                    score = them,
                    accent = Color(0xFFE58B72),
                    onAdd = { addPoint(false, 1) },
                    onRemove = { addPoint(false, -1) }
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("ESCOLHA A MÃO", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(12, 15, 18, 21, 24).forEach { value ->
                    val selected = target == value
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Gold else Color(0x332A6B51))
                            .clickable {
                                target = value
                                us = us.coerceAtMost(target)
                                them = them.coerceAtMost(target)
                                persist()
                            }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            value.toString(),
                            color = if (selected) FeltDark else Cream,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3025)),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.65f))
            ) {
                Column(Modifier.fillMaxWidth().padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CONTROLE DA PARTIDA", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        Tenths(us = true, score = us)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("×", color = Muted, fontSize = 25.sp, fontWeight = FontWeight.Light)
                            Text("PLACAR", color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
                        }
                        Tenths(us = false, score = them)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showReset = true },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF183D30), contentColor = Cream),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(7.dp))
                    Text("NOVA PARTIDA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        if (us > 0 || them > 0) history.add(0, "Partida encerrada • $us × $them • mão de $target")
                        us = 0
                        them = 0
                        persist()
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = FeltDark)
                ) {
                    Text("ZERAR PLACAR", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("BOM JOGO E BOM TRUCO!", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
        }
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            containerColor = FeltDark,
            title = { Text("Histórico de partidas", color = Gold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (history.isEmpty()) Text("As partidas concluídas nesta sessão aparecerão aqui.", color = Cream)
                    else history.take(12).forEach { Text("• $it", color = Cream, modifier = Modifier.padding(vertical = 4.dp)) }
                }
            },
            confirmButton = { TextButton(onClick = { showHistory = false }) { Text("FECHAR", color = Gold) } },
            dismissButton = { TextButton(onClick = { history.clear() }) { Text("LIMPAR", color = Muted) } }
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = FeltDark,
            title = { Text("Configurações", color = Gold, fontWeight = FontWeight.Bold) },
            text = { Text("TrucoTag • Marcador de pontuação\nVersão 1.0.0\n\nA pontuação e a mão escolhida ficam salvas neste aparelho.", color = Cream) },
            confirmButton = { TextButton(onClick = { showSettings = false }) { Text("OK", color = Gold) } }
        )
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            containerColor = FeltDark,
            title = { Text("Nova partida?", color = Gold, fontWeight = FontWeight.Bold) },
            text = { Text("O placar atual será zerado. Deseja continuar?", color = Cream) },
            confirmButton = {
                TextButton(onClick = {
                    if (us > 0 || them > 0) history.add(0, "Partida encerrada • $us × $them • mão de $target")
                    us = 0
                    them = 0
                    persist()
                    showReset = false
                }) { Text("ZERAR", color = Gold, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showReset = false }) { Text("CANCELAR", color = Muted) } }
        )
    }
}

@Composable
private fun ScorePanel(
    modifier: Modifier,
    name: String,
    score: Int,
    accent: Color,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC082D22)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 17.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text(score.toString(), color = Cream, fontSize = 62.sp, lineHeight = 68.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(13.dp))
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = FeltDark)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(23.dp))
                Text("PONTO", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(8.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF512D2A))
            ) {
                Icon(Icons.Default.Remove, "Remover ponto", tint = Cream)
            }
        }
    }
}

@Composable
private fun Tenths(us: Boolean, score: Int) {
    val marks = score.coerceIn(0, 12)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (us) "NÓS" else "ELES", color = if (us) Gold else Color(0xFFE58B72), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(6) { index ->
                Box(
                    Modifier.size(13.dp, 22.dp).clip(RoundedCornerShape(4.dp))
                        .background(if (index < marks) (if (us) Gold else Color(0xFFE58B72)) else Color(0xFF24483B))
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(6) { index ->
                Box(
                    Modifier.padding(top = 3.dp).size(13.dp, 22.dp).clip(RoundedCornerShape(4.dp))
                        .background(if (index + 6 < marks) (if (us) Gold else Color(0xFFE58B72)) else Color(0xFF24483B))
                )
            }
        }
    }
}
