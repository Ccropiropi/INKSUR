package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GearType
import com.example.model.LevelUpChoice
import com.example.model.SpellTraitType

@Composable
fun LevelUpDialog(
    choices: List<LevelUpChoice>,
    onSelectChoice: (LevelUpChoice) -> Unit
) {
    Dialog(
        onDismissRequest = { /* Modal choice required */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(16.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "LEVEL UP",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color.Black,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Inscribe a mechanical trait or gear modification",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 3 Choices
                choices.forEachIndexed { index, choice ->
                    ChoiceCardItem(
                        choice = choice,
                        index = index,
                        onClick = { onSelectChoice(choice) }
                    )
                    if (index < choices.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChoiceCardItem(
    choice: LevelUpChoice,
    index: Int,
    onClick: () -> Unit
) {
    val isTrait = choice is LevelUpChoice.TraitChoice
    val icon: ImageVector = when (choice) {
        is LevelUpChoice.TraitChoice -> Icons.Default.AutoAwesome
        is LevelUpChoice.SpellLevelChoice -> Icons.Default.Upgrade
        is LevelUpChoice.NewSpellChoice -> Icons.Default.Brush
        is LevelUpChoice.GearChoice -> when (choice.gearType) {
            GearType.HEAVY_VELLUM -> Icons.Default.AddModerator
            GearType.ERGONOMIC_GRIP -> Icons.Default.ElectricBolt
            GearType.DENSE_SOOT -> Icons.Default.FormatPaint
            GearType.SCRIBES_SANDAL -> Icons.Default.Navigation
            GearType.LODESTONE_INKWELL -> Icons.Default.Radar
            GearType.SPRING_WATER -> Icons.Default.Favorite
        }
    }

    val badgeColor = if (isTrait) Color(0xFFD32F2F) else Color.Black
    val containerBg = if (isTrait) Color(0xFFFFF9F9) else Color(0xFFFAFAFA)
    val borderColor = if (isTrait) Color(0xFFD32F2F) else Color(0xFF222222)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("choice_option_$index")
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = choice.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .background(badgeColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = choice.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = choice.subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isTrait) Color(0xFFD32F2F) else Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = choice.description,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}
