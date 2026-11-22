package com.dimm.trucotag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Felt = Color(0xFF064431)
private val FeltDeep = Color(0xFF02291F)
private val FeltLight = Color(0xFF0C5941)
private val Gold = Color(0xFFE9C16C)
private val GoldLight = Color(0xFFFFE5A0)
private val Ivory = Color(0xFFFFF5DE)
private val Muted = Color(0xFFB8CDBF)
private val Coral = Color(0xFFE58B78)
private val Wood = Color(0xFF4A2819)
private val WoodLight = Color(0xFF9B6235)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(4, 48, 35)
        window.navigationBarColor = android.graphics.Color.rgb(2, 30, 23)
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
    var us by remember { mutableIntStateOf(initialUs.coerceIn(0, initialTarget)) }
    var them by remember { mutableIntStateOf(initialThem.coerceIn(0, initialTarget)) }
    var target by remember { mutableIntStateOf(initialTarget) }
    var showHistory by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showReset by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<String>() }
    val winner = when {
        us >= target -> "NÓS VENCEMOS!"
        them >= target -> "ELES VENCERAM!"
        else -> null
    }

    fun persist() = onSave(us, them, target)
    fun changeScore(ours: Boolean, delta: Int) {
        val oldUs = us
        val oldThem = them
        if (ours) us = (us + delta).coerceIn(0, target)
        else them = (them + delta).coerceIn(0, target)
        persist()
        if (winner == null && (us == target || them == target)) {
            val victor = if (us == target) "Nós" else "Eles"
            history.add(0, "$victor venceu • $us × $them • mão de $target")
        }
        if (oldUs == us && oldThem == them) return
    }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF120B08), Wood, Color(0xFF100A07))))) {
        // The table has a dark walnut rail, a fine brass pinstripe and a deep green felt centre.
        Box(
            Modifier.fillMaxSize().padding(horizontal = 7.dp, vertical = 9.dp)
                .shadow(18.dp, RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(30.dp))
                .background(Brush.linearGradient(listOf(Color(0xFFB17A43), Wood, Color(0xFF28140D))))
                .border(2.dp, Gold.copy(alpha = 0.75f), RoundedCornerShape(30.dp))
                .padding(7.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(FeltLight, Felt, FeltDeep)))
        ) {
            FeltTexture(Modifier.fillMaxSize())
            Column(
                Modifier.fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 17.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("TRUCOTAG", color = Gold, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 3.5.sp)
                        Text("MESA DE TRUCO", color = Muted, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Medium)
                    }
                    TableIcon(Icons.Default.History, "Histórico") { showHistory = true }
                    Spacer(Modifier.size(5.dp))
                    TableIcon(Icons.Default.Settings, "Configurações") { showSettings = true }
                }

                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("PARTIDA ATÉ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.4.sp)
                    Spacer(Modifier.size(10.dp))
                    Text("$target", color = GoldLight, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.size(10.dp))
                    Text("PONTOS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }

                Spacer(Modifier.height(17.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TeamScore(
                        modifier = Modifier.weight(1f),
                        team = "NÓS",
                        score = us,
                        target = target,
                        accent = Gold,
                        onPlus = { changeScore(true, 1) },
                        onMinus = { changeScore(true, -1) }
                    )
                    TeamScore(
                        modifier = Modifier.weight(1f),
                        team = "ELES",
                        score = them,
                        target = target,
                        accent = Coral,
                        onPlus = { changeScore(false, 1) },
                        onMinus = { changeScore(false, -1) }
                    )
                }

                if (winner != null) {
                    Spacer(Modifier.height(14.dp))
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF5D411B), Color(0xFF9B7131), Color(0xFF5D411B))))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(winner, color = Ivory, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    }
                }

                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.weight(1f).height(1.dp).background(Gold.copy(alpha = 0.35f)))
                    Text("  ESCOLHA A MÃO  ", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.2.sp)
                    Spacer(Modifier.weight(1f).height(1.dp).background(Gold.copy(alpha = 0.35f)))
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    listOf(12, 15, 18, 21, 24).forEach { value ->
                        val selected = target == value
                        Box(
                            Modifier.weight(1f).height(52.dp)
                                .shadow(if (selected) 7.dp else 0.dp, RoundedCornerShape(13.dp))
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    if (selected) Brush.verticalGradient(listOf(GoldLight, Gold, Color(0xFFC18D3E)))
                                    else Brush.verticalGradient(listOf(Color(0xFF104A38), Color(0xFF073526)))
                                )
                                .border(1.dp, if (selected) GoldLight else Gold.copy(alpha = 0.28f), RoundedCornerShape(13.dp))
                                .clickable {
                                    target = value
                                    us = us.coerceAtMost(value)
                                    them = them.coerceAtMost(value)
                                    persist()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$value", color = if (selected) FeltDeep else Ivory, fontSize = 17.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                ScoreTrack(us, them, target)

                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        label = "NOVA PARTIDA",
                        icon = Icons.Default.Refresh,
                        primary = false
                    ) { showReset = true }
                    ActionButton(
                        modifier = Modifier.weight(1f),
                        label = "ZERAR PLACAR",
                        icon = Icons.Default.Remove,
                        primary = true
                    ) {
                        if (us > 0 || them > 0) history.add(0, "Partida encerrada • $us × $them • mão de $target")
                        us = 0
                        them = 0
                        persist()
                    }
                }
                Spacer(Modifier.height(15.dp))
                Text("♠  BOM JOGO E BOM TRUCO!  ♥", color = Muted, fontSize = 9.sp, letterSpacing = 2.2.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            containerColor = FeltDeep,
            title = { Text("Histórico de partidas", color = Gold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (history.isEmpty()) Text("As partidas concluídas aparecerão aqui.", color = Ivory)
                    else history.take(20).forEach { Text("• $it", color = Ivory, modifier = Modifier.padding(vertical = 4.dp)) }
                }
            },
            confirmButton = { TextButton(onClick = { showHistory = false }) { Text("FECHAR", color = Gold) } },
            dismissButton = { TextButton(onClick = { history.clear() }) { Text("LIMPAR", color = Muted) } }
        )
    }
    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = FeltDeep,
            title = { Text("Configurações", color = Gold, fontWeight = FontWeight.Bold) },
            text = { Text("TrucoTag • Marcador de pontuação\nVersão 1.1.0\n\nPlacar e mão selecionada salvos neste aparelho.", color = Ivory) },
            confirmButton = { TextButton(onClick = { showSettings = false }) { Text("OK", color = Gold) } }
        )
    }
    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            containerColor = FeltDeep,
            title = { Text("Começar nova partida?", color = Gold, fontWeight = FontWeight.Bold) },
            text = { Text("O placar atual será salvo no histórico e zerado.", color = Ivory) },
            confirmButton = {
                TextButton(onClick = {
                    if (us > 0 || them > 0) history.add(0, "Partida encerrada • $us × $them • mão de $target")
                    us = 0
                    them = 0
                    persist()
                    showReset = false
                }) { Text("NOVA PARTIDA", color = Gold, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showReset = false }) { Text("CANCELAR", color = Muted) } }
        )
    }
}

@Composable
private fun FeltTexture(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        // Subtle woven-felt grain, intentionally low contrast so the score remains legible.
        for (i in 0..34) {
            val y = h * i / 34f
            drawLine(Color.White.copy(alpha = 0.018f), Offset(0f, y), Offset(w, y + 18f), 1f)
        }
        for (i in 0..22) {
            val x = w * i / 22f
            drawLine(Color.Black.copy(alpha = 0.045f), Offset(x, 0f), Offset(x - 25f, h), 1.2f)
        }
        drawCircle(
            Brush.radialGradient(listOf(Color(0xFF2B805C).copy(alpha = 0.14f), Color.Transparent)),
            radius = w * 0.65f,
            center = Offset(w * 0.5f, h * 0.38f)
        )
    }
}

@Composable
private fun TableIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(42.dp).clip(CircleShape).background(Color(0x33201109))
            .border(1.dp, Gold.copy(alpha = 0.45f), CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = label, tint = GoldLight, modifier = Modifier.size(21.dp))
    }
}

@Composable
private fun TeamScore(
    modifier: Modifier,
    team: String,
    score: Int,
    target: Int,
    accent: Color,
    onPlus: () -> Unit,
    onMinus: () -> Unit
) {
    Column(
        modifier.clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(Color(0xCC052F24), Color(0xAA031F18))))
            .border(1.4.dp, accent.copy(alpha = 0.75f), RoundedCornerShape(22.dp))
            .padding(horizontal = 10.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.size(7.dp))
            Text(team, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 2.4.sp)
            Spacer(Modifier.size(7.dp))
            Box(Modifier.size(7.dp).clip(CircleShape).background(accent))
        }
        Spacer(Modifier.height(9.dp))
        Text("$score", color = Ivory, fontSize = 64.sp, lineHeight = 70.sp, fontWeight = FontWeight.Black)
        Text("$score / $target PONTOS", color = Muted, fontSize = 9.sp, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(13.dp))
        Box(
            Modifier.fillMaxWidth().height(47.dp).shadow(5.dp, RoundedCornerShape(13.dp))
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.82f))))
                .clickable(enabled = score < target, onClick = onPlus),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = FeltDeep, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(5.dp))
                Text("PONTO", color = FeltDeep, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }
        Spacer(Modifier.height(9.dp))
        Box(
            Modifier.size(39.dp).clip(CircleShape)
                .background(Color(0xFF4B2825))
                .border(1.dp, Color(0xFFB36D5C).copy(alpha = 0.7f), CircleShape)
                .clickable(enabled = score > 0, onClick = onMinus),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Retirar ponto de $team", tint = Ivory, modifier = Modifier.size(21.dp))
        }
    }
}

@Composable
private fun ScoreTrack(us: Int, them: Int, target: Int) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp))
            .background(Color(0x99032119))
            .border(1.dp, Gold.copy(alpha = 0.65f), RoundedCornerShape(19.dp))
            .padding(horizontal = 13.dp, vertical = 15.dp)
    ) {
        Text("CONTAGEM DA PARTIDA", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.3.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TrackTeam("NÓS", us, target, Gold, Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text("VS", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Box(Modifier.size(5.dp).clip(CircleShape).background(Gold))
            }
            TrackTeam("ELES", them, target, Coral, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TrackTeam(label: String, score: Int, target: Int, color: Color, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(8.dp))
        val blocks = (target + 2) / 3
        val rows = (blocks + 5) / 6
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(minOf(6, blocks - row * 6)) { col ->
                    val pointIndex = row * 6 + col
                    val active = pointIndex * 3 < score
                    Box(
                        Modifier.weight(1f).height(16.dp).clip(RoundedCornerShape(4.dp))
                            .background(if (active) color else Color(0xFF1B493B))
                    )
                }
            }
            if (row < rows - 1) Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(5.dp))
        Text("$score de $target", color = Muted, fontSize = 9.sp)
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primary: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(15.dp)
    Row(
        modifier.height(52.dp).clip(shape)
            .background(
                if (primary) Brush.verticalGradient(listOf(GoldLight, Gold, Color(0xFFC18B39)))
                else Brush.verticalGradient(listOf(Color(0xFF174D3A), Color(0xFF0B382B)))
            )
            .border(1.dp, if (primary) GoldLight else Gold.copy(alpha = 0.7f), shape)
            .clickable(onClick = onClick).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = if (primary) FeltDeep else Ivory, modifier = Modifier.size(17.dp))
        Spacer(Modifier.size(6.dp))
        Text(label, color = if (primary) FeltDeep else Ivory, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
    }
}

