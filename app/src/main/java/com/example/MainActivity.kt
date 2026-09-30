package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.GameViewModel
import com.example.game.ScreenState
import com.example.model.GearType
import com.example.model.SpellDefinition
import com.example.ui.BattleScreen
import com.example.ui.BlankScrollDialog
import com.example.ui.BrokenStoneDialog
import com.example.ui.GameOverDialog
import com.example.ui.LevelUpDialog
import com.example.ui.MainMenuScreen
import com.example.ui.ObeliskDialog
import com.example.ui.PauseDialog
import com.example.ui.RuneSocketDialog
import com.example.ui.SynthesisDialog
import com.example.ui.TraitSelectionDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                InkSurvivorApp()
            }
        }
    }
}

@Composable
fun InkSurvivorApp(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Back handling for screens
    BackHandler(enabled = uiState.screen != ScreenState.MAIN_MENU) {
        when (uiState.screen) {
            ScreenState.PLAYING -> viewModel.pauseGame()
            ScreenState.PAUSED -> viewModel.resumeGame()
            ScreenState.LEVEL_UP -> { /* Modal requires selecting a choice */ }
            ScreenState.TRAIT_SELECTION -> { /* Modal requires trait choice */ }
            ScreenState.RUNE_SOCKET -> { /* Modal requires socketing the rune */ }
            ScreenState.SPELL_SYNTHESIS -> { /* Modal requires synthesis confirmation */ }
            ScreenState.OBELISK -> { /* Modal requires artifact choice */ }
            ScreenState.BROKEN_STONE -> viewModel.closeBrokenStone()
            ScreenState.BLANK_SCROLL -> { /* Modal requires selecting item */ }
            ScreenState.GAME_OVER -> viewModel.setScreen(ScreenState.MAIN_MENU)
            ScreenState.MAIN_MENU -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.screen) {
            ScreenState.MAIN_MENU -> {
                MainMenuScreen(
                    viewModel = viewModel,
                    selectedClass = uiState.playerClass,
                    onSelectClass = { classDef -> viewModel.selectClass(classDef) },
                    onStartGame = { viewModel.startNewGame() }
                )
            }

            ScreenState.PLAYING,
            ScreenState.PAUSED,
            ScreenState.LEVEL_UP,
            ScreenState.TRAIT_SELECTION,
            ScreenState.RUNE_SOCKET,
            ScreenState.SPELL_SYNTHESIS,
            ScreenState.OBELISK,
            ScreenState.BROKEN_STONE,
            ScreenState.BLANK_SCROLL,
            ScreenState.GAME_OVER -> {
                BattleScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onPauseClick = { viewModel.pauseGame() }
                )

                // Dialog overlays
                when (uiState.screen) {
                    ScreenState.LEVEL_UP -> {
                        LevelUpDialog(
                            choices = uiState.availableChoices,
                            onSelectChoice = { choice ->
                                viewModel.selectLevelUpChoice(choice)
                            }
                        )
                    }

                    ScreenState.TRAIT_SELECTION -> {
                        TraitSelectionDialog(
                            spellName = uiState.pendingTraitSpellName,
                            traitOptions = uiState.pendingTraitOptions,
                            onSelectTrait = { trait ->
                                viewModel.selectSpellTrait(trait)
                            }
                        )
                    }

                    ScreenState.RUNE_SOCKET -> {
                        uiState.pendingSpellRune?.let { rune ->
                            RuneSocketDialog(
                                runeType = rune,
                                spells = uiState.activeSpells,
                                onSocketSpell = { spell ->
                                    viewModel.socketRuneIntoSpell(spell, rune)
                                }
                            )
                        }
                    }

                    ScreenState.SPELL_SYNTHESIS -> {
                        SynthesisDialog(
                            recipe = uiState.synthesisRecipe,
                            activeSlotNumber = uiState.activeSlotNumber,
                            onSelectSynthesis = { recipe ->
                                viewModel.confirmSynthesis(recipe)
                            }
                        )
                    }

                    ScreenState.BLANK_SCROLL -> {
                        BlankScrollDialog(
                            availableSpells = SpellDefinition.baseSpells,
                            availableGear = GearType.entries,
                            onSelectItem = { target ->
                                viewModel.selectBlankScrollItem(target)
                            }
                        )
                    }

                    ScreenState.OBELISK -> {
                        ObeliskDialog(
                            artifacts = uiState.obeliskChoices,
                            onSelectArtifact = { artifact ->
                                viewModel.selectObeliskArtifact(artifact)
                            }
                        )
                    }

                    ScreenState.BROKEN_STONE -> {
                        BrokenStoneDialog(
                            artifacts = uiState.brokenStoneChoices,
                            orbCost = 15,
                            currentOrbs = viewModel.player.xp,
                            heavyWeightCount = uiState.heavyWeightCount,
                            isMastered = uiState.isHeavyWeightMastered,
                            onSelectArtifact = { artifact ->
                                viewModel.purchaseBrokenStoneArtifact(artifact)
                            },
                            onClose = { viewModel.closeBrokenStone() }
                        )
                    }

                    ScreenState.PAUSED -> {
                        PauseDialog(
                            viewModel = viewModel,
                            uiState = uiState,
                            onResume = { viewModel.resumeGame() },
                            onAbandon = { viewModel.setScreen(ScreenState.MAIN_MENU) }
                        )
                    }

                    ScreenState.GAME_OVER -> {
                        GameOverDialog(
                            uiState = uiState,
                            onRestart = { viewModel.startNewGame() },
                            onHome = { viewModel.setScreen(ScreenState.MAIN_MENU) }
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}
