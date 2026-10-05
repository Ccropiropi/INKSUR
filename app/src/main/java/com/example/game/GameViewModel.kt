package com.example.game

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookmarkRunState
import com.example.data.GameRepository
import com.example.data.PlayerProgressEntity
import com.example.data.RunRecordEntity
import com.example.data.SaveManager
import com.example.model.ActiveSpell
import com.example.model.ArtifactDefinition
import com.example.model.AstralExpansionTrait
import com.example.model.BlankScrollDrop
import com.example.model.BlotterBurstVisual
import com.example.model.BrokenStoneEntity
import com.example.model.CalligraphicElement
import com.example.model.ChainReactionTrait
import com.example.model.CharacterDefinition
import com.example.model.CinnabarSealEntity
import com.example.model.ClassDefinition
import com.example.model.DamageNumber
import com.example.model.DeepWellTrait
import com.example.model.ElementalParticle
import com.example.model.ElementalReactionType
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.EquippedGear
import com.example.model.FlexNibTrait
import com.example.model.FlowingSerpentEntity
import com.example.model.GearType
import com.example.model.InkPuddle
import com.example.model.InkPuddlePool
import com.example.model.InkProjectile
import com.example.model.InkBrushSplashParticle
import com.example.model.InkSplashParticle
import com.example.model.InkwellStructure
import com.example.model.InkwellVortexEntity
import com.example.model.LevelUpChoice
import com.example.model.MagnumOpusVisual
import com.example.model.ObeliskEntity
import com.example.model.Orb
import com.example.model.OrbitalRuneEntity
import com.example.model.PassiveStatType
import com.example.model.PlayerMotionTrailNode
import com.example.model.PressurizedInkTrait
import com.example.model.RapidRotationTrait
import com.example.model.RazorFlowTrait
import com.example.model.RedRuneEntity
import com.example.model.ScreenElementalReactionVisual
import com.example.model.SerratedNibTrait
import com.example.model.SoundManager
import com.example.model.SpellCastType
import com.example.model.SpellDefinition
import com.example.model.SpellRuneDrop
import com.example.model.SpellRuneType
import com.example.model.SpellSynthesisRecipe
import com.example.model.SpellTraitModule
import com.example.model.SpellTraitType
import com.example.model.TelegraphedStrike
import com.example.model.TheEraserBoss
import com.example.model.VolatileCoreTrait
import com.example.model.WashBrushArcVisual
import com.example.model.WideBristleTrait
import com.example.ui.InjectedItemTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

enum class ScreenState {
    MAIN_MENU,
    PLAYING,
    PAUSED,
    LEVEL_UP,
    TRAIT_SELECTION,
    RUNE_SOCKET,
    SPELL_SYNTHESIS,
    OBELISK,
    BROKEN_STONE,
    BLANK_SCROLL,
    INKWELL_CHECKPOINT,
    GAME_OVER,
    SUMI_E_CODEX
}

data class PlayerState(
    var x: Float = 0f,
    var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var lastMoveDirection: Float = 0f,
    var hp: Float = 100f,
    var maxHp: Float = 100f,
    var baseMoveSpeed: Float = 210f,
    var moveSpeed: Float = 210f,
    var armor: Float = 0f,
    var pickupRadius: Float = 110f,
    var attackSpeedMultiplier: Float = 1.0f,
    var damageMultiplier: Float = 1.0f,
    var xpMultiplier: Float = 1.0f,
    var bonusProjectiles: Int = 0,
    var cooldownMultiplier: Float = 1.0f,
    var aoeMultiplier: Float = 1.0f,
    var projectileBounces: Int = 0,
    var bounceDamageDelta: Float = 0f,
    var xp: Int = 0,
    var xpNeeded: Int = 11,
    var level: Int = 1,
    var isInvincible: Boolean = false,
    var invincibleTimer: Float = 0f
)

data class GameUIState(
    val screen: ScreenState = ScreenState.MAIN_MENU,
    val timeSurvivedSeconds: Float = 0f,
    val score: Int = 0,
    val kills: Int = 0,
    val damageDealt: Long = 0,
    val character: CharacterDefinition = CharacterDefinition.TheCalligrapher,
    val playerClass: ClassDefinition = ClassDefinition.Scribe,
    val activeSpells: List<ActiveSpell> = emptyList(),
    val equippedGear: List<EquippedGear> = emptyList(),
    val equippedArtifacts: List<ArtifactDefinition> = emptyList(),
    val availableChoices: List<LevelUpChoice> = emptyList(),
    val pendingSpellRune: SpellRuneType? = null,
    val redRuneActive: Boolean = false,
    val synthesisRecipe: SpellSynthesisRecipe? = null,
    val activeSlotNumber: Int = 1,
    val slot1Unlocked: Boolean = false,
    val slot2Unlocked: Boolean = false,
    val slot3Unlocked: Boolean = false,
    val completedSyntheses: List<String> = emptyList(),
    val obeliskChoices: List<ArtifactDefinition> = emptyList(),
    val brokenStoneChoices: List<ArtifactDefinition> = emptyList(),
    val heavyWeightCount: Int = 0,
    val isHeavyWeightMastered: Boolean = false,
    val corruptedCount: Int = 0,
    val isCorruptedMastered: Boolean = false,
    val geometryCount: Int = 0,
    val isGeometryMastered: Boolean = false,
    val titanSpawned: Boolean = false,
    val midBossSpawned: Boolean = false,
    val inkwellActive: Boolean = false,
    val inkwellShopChoices: List<ArtifactDefinition> = emptyList(),
    val screenShakeTimer: Float = 0f,
    val eraserActive: Boolean = false,
    val eraserSafeRadius: Float = 600f,
    val hasBookmarkRun: Boolean = false,
    val unlockedMapTier: Int = 1,
    val selectedMapTier: Int = 1,
    val pendingTraitSpellName: String = "",
    val pendingTraitOptions: List<SpellTraitType> = emptyList(),
    val guaranteedNextItem: LevelUpChoice? = null,
    val magnumOpusActive: Boolean = false,
    val goldEarnedThisRun: Int = 0,
    val crystalsEarnedThisRun: Int = 0,
    val isVictory: Boolean = false,
    val isMinute61Victory: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = GameRepository(database.runDao(), database.playerProgressDao())
    val soundManager = SoundManager(application)

    val playerProgress: StateFlow<PlayerProgressEntity> = repository.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerProgressEntity())

    val runHistory: StateFlow<List<RunRecordEntity>> = repository.allRuns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val player = PlayerState()

    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    // Simulation Entities
    val enemies = mutableListOf<Enemy>()
    val inkProjectiles = mutableListOf<InkProjectile>()
    val puddlePool = InkPuddlePool(capacity = 200)
    val redRunes = mutableListOf<RedRuneEntity>()
    val obelisks = mutableListOf<ObeliskEntity>()
    val brokenStones = mutableListOf<BrokenStoneEntity>()
    val spellRuneDrops = mutableListOf<SpellRuneDrop>()
    val blankScrollDrops = mutableListOf<BlankScrollDrop>()
    val inkwellVortexes = mutableListOf<InkwellVortexEntity>()
    val magnumOpusVisual = MagnumOpusVisual()
    val washBrushVisuals = mutableListOf<WashBrushArcVisual>()
    val orbs = mutableListOf<Orb>()
    val damageNumbers = mutableListOf<DamageNumber>()
    val orbitalRunes = mutableListOf<OrbitalRuneEntity>()
    val cinnabarSeals = mutableListOf<CinnabarSealEntity>()
    val inkSplashParticles = mutableListOf<InkSplashParticle>()
    val playerMotionTrails = mutableListOf<PlayerMotionTrailNode>()
    val flowingSerpents = mutableListOf<FlowingSerpentEntity>()
    val elementalReactionVisuals = mutableListOf<ScreenElementalReactionVisual>()
    val elementalParticles = mutableListOf<ElementalParticle>()
    var activeElementalBanner by mutableStateOf<ElementalReactionType?>(null)
    var activeElementalBannerTimer by mutableStateOf(0f)
    var lastElementalReactionName by mutableStateOf<String?>(null)
    private var playerTrailSpawnTimer: Float = 0f

    // Two App Modes: Standard Run (Start from 0) vs Dev Testing (Unlocked All)
    var isDevTestingMode by mutableStateOf(false)
        private set

    fun applyDevTestingMode(enabled: Boolean) {
        isDevTestingMode = enabled
        viewModelScope.launch {
            if (enabled) {
                repository.setDevModeUnlockedAll()
                saveManager.writeSaveData(
                    saveManager.loadSaveData().copy(
                        gold = 999999,
                        crystals = 99999,
                        inkStones = 99999,
                        metaAtkLevel = 10,
                        metaSpeedLevel = 10,
                        metaMagnetLevel = 10,
                        unlockedCharacters = "calligrapher,scholar,grandmaster,runesmith",
                        unlockedMapTier = 3
                    )
                )
            } else {
                repository.resetToFreshZero()
                saveManager.writeSaveData(
                    saveManager.loadSaveData().copy(
                        gold = 0,
                        crystals = 0,
                        inkStones = 0,
                        metaAtkLevel = 0,
                        metaSpeedLevel = 0,
                        metaMagnetLevel = 0,
                        unlockedCharacters = "calligrapher",
                        unlockedMapTier = 1,
                        totalRuns = 0,
                        totalKills = 0,
                        maxSurvivalSeconds = 0
                    )
                )
            }
        }
    }

    fun toggleDevTestingMode() {
        if (!com.example.BuildConfig.DEBUG) return
        applyDevTestingMode(!isDevTestingMode)
    }

    // Phase 5 Entities
    val inkwellStructures = mutableListOf<InkwellStructure>()
    val blotterBurstVisuals = mutableListOf<BlotterBurstVisual>()
    val theEraserBoss = TheEraserBoss()

    // High-Contrast Ink-Brush Splash Particles for Elemental Combinations
    val inkBrushSplashParticles = mutableListOf<InkBrushSplashParticle>()

    // Settings: Screen Shake, Haptic Feedback & Impact Frame Hit-Stop
    var isFixedAnalog by mutableStateOf(true)
    var isScreenShakeEnabled by mutableStateOf(true)
    var isHapticFeedbackEnabled by mutableStateOf(true)
    var isImpactFrameEnabled by mutableStateOf(true)
    var impactFrameTimer by mutableStateOf(0f)
    var activeImpactReaction by mutableStateOf<ElementalReactionType?>(null)

    fun updateFixedAnalog(fixed: Boolean) {
        isFixedAnalog = fixed
        saveManager.setFixedAnalog(fixed)
    }

    fun updateScreenShakeEnabled(enabled: Boolean) {
        isScreenShakeEnabled = enabled
        saveManager.setScreenShake(enabled)
    }

    fun updateHapticFeedbackEnabled(enabled: Boolean) {
        isHapticFeedbackEnabled = enabled
        soundManager.isHapticEnabled = enabled
        saveManager.setHapticFeedback(enabled)
    }

    fun updateImpactFrameEnabled(enabled: Boolean) {
        isImpactFrameEnabled = enabled
        saveManager.setImpactFrame(enabled)
    }

    // Active elemental stacks tracking derived from player's active spells
    val playerElementalStacks by derivedStateOf {
        val stacks = mutableMapOf<CalligraphicElement, Int>()
        for (spell in _uiState.value.activeSpells) {
            val el = spell.element
            stacks[el] = (stacks[el] ?: 0) + spell.rank
        }
        stacks
    }

    fun spawnHighContrastInkBrushSplash(
        x: Float,
        y: Float,
        reactionType: ElementalReactionType,
        count: Int = 36
    ) {
        val (starkColor, glowColor) = when (reactionType) {
            ElementalReactionType.FROZEN_INK -> Color(0xFF020B14) to Color(0xFF00E5FF)
            ElementalReactionType.BURNING_CALLIGRAPHY -> Color(0xFF2E0505) to Color(0xFFFF3D00)
            ElementalReactionType.COSMIC_SUPERNOVA -> Color(0xFF100726) to Color(0xFFFFD54F)
            ElementalReactionType.PERMAFROST_BLOSSOM -> Color(0xFF031626) to Color(0xFF80D8FF)
            ElementalReactionType.THERMAL_SHOCK -> Color(0xFF280314) to Color(0xFFFF1744)
        }

        for (i in 0 until count) {
            val angle = kotlin.random.Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
            val speed = 110f + kotlin.random.Random.nextFloat() * 380f
            val maxLife = 0.55f + kotlin.random.Random.nextFloat() * 0.45f
            val radius = 5f + kotlin.random.Random.nextFloat() * 12f

            inkBrushSplashParticles.add(
                InkBrushSplashParticle(
                    id = ++entityIdCounter,
                    x = x + (kotlin.random.Random.nextFloat() * 20f - 10f),
                    y = y + (kotlin.random.Random.nextFloat() * 20f - 10f),
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    radius = radius * 0.4f,
                    maxRadius = radius,
                    life = maxLife,
                    maxLife = maxLife,
                    starkColor = if (i % 3 == 0) glowColor else starkColor,
                    elementalGlowColor = glowColor,
                    strokeWidth = 3.5f + kotlin.random.Random.nextFloat() * 4f,
                    splatterTendrilAngle = angle + (kotlin.random.Random.nextFloat() * 0.5f - 0.25f),
                    splatterTendrilLength = 12f + kotlin.random.Random.nextFloat() * 26f,
                    isFeatheredBleed = i % 2 == 0
                )
            )
        }
        if (inkBrushSplashParticles.size > 180) {
            inkBrushSplashParticles.subList(0, inkBrushSplashParticles.size - 180).clear()
        }
    }

    fun spawnInkFluidSplash(
        x: Float,
        y: Float,
        count: Int = 6,
        isCritOrBleed: Boolean = false,
        baseDmg: Float = 20f
    ) {
        val numDroplets = count.coerceIn(3, 14)
        for (i in 0 until numDroplets) {
            val angle = kotlin.random.Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
            val speed = 70f + kotlin.random.Random.nextFloat() * (120f + baseDmg * 0.8f).coerceAtMost(350f)
            val radius = 4f + kotlin.random.Random.nextFloat() * 6f
            val maxLife = 0.45f + kotlin.random.Random.nextFloat() * 0.35f
            inkSplashParticles.add(
                InkSplashParticle(
                    id = ++entityIdCounter,
                    x = x + (kotlin.random.Random.nextFloat() * 14f - 7f),
                    y = y + (kotlin.random.Random.nextFloat() * 14f - 7f),
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    radius = radius * 0.65f,
                    maxRadius = radius,
                    life = maxLife,
                    maxLife = maxLife,
                    viscosity = 3.2f + kotlin.random.Random.nextFloat() * 1.2f,
                    color = if (isCritOrBleed) Color(0xFFD32F2F) else Color(0xFF0F0F14),
                    secondaryColor = if (isCritOrBleed) Color(0xFF380808) else Color(0xFF000000),
                    isCrit = isCritOrBleed,
                    tendrilAngle = angle + (kotlin.random.Random.nextFloat() * 0.4f - 0.2f),
                    tendrilLength = 4f + kotlin.random.Random.nextFloat() * 10f
                )
            )
        }
        if (inkSplashParticles.size > 140) {
            inkSplashParticles.subList(0, inkSplashParticles.size - 140).clear()
        }
    }

    // Entity counters & Timers
    private var entityIdCounter: Long = 0
    private var enemySpawnTimer: Float = 0f

    // Red Rune & Elite tracking
    private var nextRedRuneTime: Float = 150f
    private var nextElitePackId: Long = 0L
    private val elitePackCounts = mutableMapOf<Long, Int>()

    // Broken Stone spawn timer (Only after minute 25 = 1500s)
    private var nextBrokenStoneTime: Float = 1500f
    private var fieldOrbSpawnTimer: Float = 0f

    // Passive Stat Upgrades
    var passiveBonusHp: Float = 0f
    var passiveBonusDmg: Float = 0f
    var passiveBonusSpeed: Float = 0f
    var passiveBonusArmor: Float = 0f
    var passiveBonusPickup: Float = 0f
    var passiveBonusAttackSpeed: Float = 0f

    // P0-1 Telemetry tracking
    var totalXpCollected: Long = 0L
        private set
    private val loggedTelemetryMinutes = mutableSetOf<Int>()

    // P0-2 Polynomial XP curve calculation: 6 + 5L + 0.35L^2
    fun calculateXpNeeded(level: Int): Int = (6 + 5 * level + 0.35f * level * level).toInt()

    // Pacing Bosses & Events
    private var titanSpawned: Boolean = false
    private var midBossSpawned: Boolean = false
    private var inkwellSpawned: Boolean = false
    private var enemySpawningHalted: Boolean = false
    private var eraserStarted: Boolean = false
    var screenShakeTimer: Float = 0f

    // Phase 4 & 5 Core Managers
    val synthesisManager = SynthesisManager()
    val masteryManager = MasteryManager()
    val saveManager = SaveManager(application)

    val slot1Unlocked: Boolean get() = synthesisManager.slot1Unlocked
    val slot2Unlocked: Boolean get() = synthesisManager.slot2Unlocked
    val slot3Unlocked: Boolean get() = synthesisManager.slot3Unlocked
    val completedSyntheses: MutableSet<String> = mutableSetOf()

    // Trait Awakening sub-window state
    private var pendingTraitSpellId: String? = null
    private var pendingTraitTargetRank: Int = 3

    // RNG Mitigation: The Blank Scroll injection
    private var guaranteedNextItem: LevelUpChoice? = null

    // Level 100 Magnum Opus state
    private var magnumOpusTriggered: Boolean = false
    private var magnumOpusFreezeTimer: Float = 0f

    // Joystick input vector
    var joystickVector: Offset = Offset.Zero

    init {
        val saveData = saveManager.loadSaveData()
        isFixedAnalog = saveData.fixedAnalog
        isScreenShakeEnabled = saveData.screenShakeEnabled
        isHapticFeedbackEnabled = saveData.hapticFeedbackEnabled
        isImpactFrameEnabled = saveData.impactFrameEnabled
        soundManager.isHapticEnabled = saveData.hapticFeedbackEnabled
        _uiState.value = _uiState.value.copy(
            hasBookmarkRun = saveManager.hasBookmark(),
            unlockedMapTier = saveData.unlockedMapTier
        )
    }

    fun setScreen(screen: ScreenState) {
        _uiState.value = _uiState.value.copy(screen = screen)
    }

    fun selectClass(classDef: ClassDefinition) {
        _uiState.value = _uiState.value.copy(playerClass = classDef)
    }

    fun selectCharacter(charDef: CharacterDefinition) {
        _uiState.value = _uiState.value.copy(character = charDef)
    }

    fun selectMapTier(tier: Int) {
        if (tier <= _uiState.value.unlockedMapTier) {
            _uiState.value = _uiState.value.copy(selectedMapTier = tier)
        }
    }

    fun startNewGame() {
        val character = _uiState.value.character
        val selectedClass = _uiState.value.playerClass
        val progress = playerProgress.value

        // Initialize Player with Character modifiers + Meta-progression upgrades
        val metaAtkMult = 1.0f + (progress.metaAtkLevel * 0.05f)
        val metaSpeedMult = 1.0f + (progress.metaSpeedLevel * 0.04f)
        val metaMagnetMult = 1.0f + (progress.metaMagnetLevel * 0.20f)

        player.x = 0f
        player.y = 0f
        player.vx = 0f
        player.vy = 0f
        player.lastMoveDirection = 0f
        player.hp = 100f
        player.maxHp = 100f
        player.baseMoveSpeed = 210f * metaSpeedMult * character.moveSpeedMultiplier
        player.moveSpeed = player.baseMoveSpeed
        player.armor = 0f
        player.pickupRadius = 110f * metaMagnetMult * character.pickupRadiusMultiplier
        player.attackSpeedMultiplier = 1.0f
        player.damageMultiplier = 1.0f * metaAtkMult * character.damageMultiplier
        player.xpMultiplier = character.xpMultiplier
        player.bonusProjectiles = character.bonusProjectiles
        player.cooldownMultiplier = character.cooldownMultiplier
        player.aoeMultiplier = character.aoeMultiplier
        player.projectileBounces = 0
        player.bounceDamageDelta = 0f
        player.xp = 0
        player.xpNeeded = calculateXpNeeded(1)
        player.level = 1
        player.isInvincible = false
        player.invincibleTimer = 0f
        totalXpCollected = 0L
        loggedTelemetryMinutes.clear()

        // Reset simulation lists
        enemies.clear()
        inkProjectiles.clear()
        puddlePool.clear()
        redRunes.clear()
        obelisks.clear()
        brokenStones.clear()
        spellRuneDrops.clear()
        blankScrollDrops.clear()
        inkwellVortexes.clear()
        inkwellStructures.clear()
        blotterBurstVisuals.clear()
        theEraserBoss.reset()

        magnumOpusVisual.active = false
        magnumOpusVisual.timer = 0f
        washBrushVisuals.clear()
        orbitalRunes.clear()
        cinnabarSeals.clear()
        inkSplashParticles.clear()
        orbs.clear()
        damageNumbers.clear()
        elitePackCounts.clear()

        entityIdCounter = 0
        enemySpawnTimer = 0f
        nextRedRuneTime = 150f
        nextBrokenStoneTime = if (isDevTestingMode) 30f else 1500f
        fieldOrbSpawnTimer = 0f
        passiveBonusHp = 0f
        passiveBonusDmg = 0f
        passiveBonusSpeed = 0f
        passiveBonusArmor = 0f
        passiveBonusPickup = 0f
        passiveBonusAttackSpeed = 0f
        titanSpawned = false
        midBossSpawned = false
        inkwellSpawned = false
        enemySpawningHalted = false
        eraserStarted = false
        screenShakeTimer = 0f

        synthesisManager.reset()
        masteryManager.reset()
        completedSyntheses.clear()
        pendingTraitSpellId = null
        pendingTraitTargetRank = 3
        guaranteedNextItem = null
        magnumOpusTriggered = false
        magnumOpusFreezeTimer = 0f
        nextElitePackId = 0L
        joystickVector = Offset.Zero

        val starterSpell = ActiveSpell(definition = selectedClass.starterSpell, rank = 1)
        val initialSpells = mutableListOf(starterSpell)
        if (selectedClass.id == "scribe") {
            initialSpells.add(ActiveSpell(definition = SpellDefinition.WashBrush, rank = 1))
            initialSpells.add(ActiveSpell(definition = SpellDefinition.SteelFountain, rank = 1))
        }

        val saveData = saveManager.loadSaveData()

        _uiState.value = GameUIState(
            screen = ScreenState.PLAYING,
            timeSurvivedSeconds = 0f,
            score = 0,
            kills = 0,
            damageDealt = 0L,
            character = character,
            playerClass = selectedClass,
            activeSpells = initialSpells,
            equippedGear = emptyList(),
            equippedArtifacts = emptyList(),
            availableChoices = emptyList(),
            pendingSpellRune = null,
            redRuneActive = false,
            synthesisRecipe = null,
            slot1Unlocked = false,
            slot2Unlocked = false,
            slot3Unlocked = false,
            completedSyntheses = emptyList(),
            heavyWeightCount = 0,
            isHeavyWeightMastered = false,
            corruptedCount = 0,
            isCorruptedMastered = false,
            geometryCount = 0,
            isGeometryMastered = false,
            titanSpawned = false,
            midBossSpawned = false,
            inkwellActive = false,
            eraserActive = false,
            hasBookmarkRun = saveManager.hasBookmark(),
            unlockedMapTier = saveData.unlockedMapTier,
            selectedMapTier = _uiState.value.selectedMapTier,
            isVictory = false,
            isMinute61Victory = false
        )
    }

    fun pauseGame() {
        if (_uiState.value.screen == ScreenState.PLAYING) {
            _uiState.value = _uiState.value.copy(screen = ScreenState.PAUSED)
        }
    }

    fun resumeGame() {
        if (_uiState.value.screen == ScreenState.PAUSED) {
            _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
        }
    }

    // 60fps game simulation update
    fun updateGame(dt: Float) {
        if (_uiState.value.screen != ScreenState.PLAYING) return

        val clampedDt = dt.coerceIn(0.001f, 0.05f)
        val currentState = _uiState.value
        val newTime = currentState.timeSurvivedSeconds + clampedDt

        // Camera Screen Shake Decay
        if (screenShakeTimer > 0f) {
            screenShakeTimer -= clampedDt
            if (screenShakeTimer < 0f) screenShakeTimer = 0f
        }

        // ==========================================
        // PHASE 5: THE MINUTE 60 CLIMAX (THE ERASURE)
        // ==========================================
        // At 59:50 (3590s): Screen shakes, standard enemies dissolve into white ash, The Eraser appears!
        if (newTime >= 3590f && !eraserStarted) {
            eraserStarted = true
            screenShakeTimer = 2.5f
            soundManager.playReactionBoom()
            enemies.removeAll { !it.isBoss }
            theEraserBoss.reset()
            theEraserBoss.active = true
            theEraserBoss.timer = 0f
            enemySpawningHalted = true
        }

        // The Eraser active 60s battle
        if (theEraserBoss.active) {
            theEraserBoss.timer += clampedDt
            val progress = (theEraserBoss.timer / theEraserBoss.maxDuration).coerceIn(0f, 1f)
            theEraserBoss.currentSafeRadius = theEraserBoss.initialSafeRadius - progress * (theEraserBoss.initialSafeRadius - theEraserBoss.minSafeRadius)

            // Outside Safe Circle: Rapid Erasure Void damage
            val distFromCenter = hypot(player.x, player.y)
            if (distFromCenter > theEraserBoss.currentSafeRadius) {
                damagePlayer(28f * clampedDt)
            }

            // Telegraphed Geometric Strikes
            theEraserBoss.strikeTimer += clampedDt
            if (theEraserBoss.strikeTimer >= 2.0f) {
                theEraserBoss.strikeTimer = 0f
                val angle = Random.nextFloat() * PI.toFloat()
                val r = theEraserBoss.currentSafeRadius * 0.95f
                theEraserBoss.telegraphedStrikes.add(
                    TelegraphedStrike(
                        id = ++entityIdCounter,
                        startX = cos(angle) * r,
                        startY = sin(angle) * r,
                        endX = -cos(angle) * r,
                        endY = -sin(angle) * r,
                        lineWidth = 28f
                    )
                )
            }

            val strikeIter = theEraserBoss.telegraphedStrikes.iterator()
            while (strikeIter.hasNext()) {
                val strike = strikeIter.next()
                strike.timer += clampedDt
                if (strike.isStriking && !strike.hasDealtDamage) {
                    strike.hasDealtDamage = true
                    val dist = distanceToSegment(player.x, player.y, strike.startX, strike.startY, strike.endX, strike.endY)
                    if (dist < strike.lineWidth) {
                        damagePlayer(36f)
                        soundManager.playHit()
                    }
                }
                if (strike.timer >= strike.telegraphDuration + strike.strikeDuration) {
                    strikeIter.remove()
                }
            }

            // Absolute Win Condition: Survive to Minute 61:00 (3660s)
            if (newTime >= 3660f && !currentState.isVictory) {
                theEraserBoss.active = false
                soundManager.playReactionBoom()
                finishRun(isVictory = true, isMinute61Victory = true)
                return
            }
        }

        // Magnum Opus freeze & visual timer
        if (magnumOpusFreezeTimer > 0f) {
            magnumOpusFreezeTimer -= clampedDt
        }
        if (magnumOpusVisual.active) {
            magnumOpusVisual.timer += clampedDt
            magnumOpusVisual.flashAlpha = (1f - (magnumOpusVisual.timer / magnumOpusVisual.maxDuration)).coerceIn(0f, 1f)
            if (magnumOpusVisual.timer >= magnumOpusVisual.maxDuration) {
                magnumOpusVisual.active = false
            }
        }

        // 1. Movement logic
        val stickMagnitude = joystickVector.getDistance()
        if (stickMagnitude > 0.08f) {
            val normMag = (stickMagnitude.coerceAtMost(1f))
            val angle = atan2(joystickVector.y, joystickVector.x)
            player.lastMoveDirection = angle
            player.vx = cos(angle) * (player.moveSpeed * normMag)
            player.vy = sin(angle) * (player.moveSpeed * normMag)
        } else {
            player.vx = 0f
            player.vy = 0f
        }

        player.x += player.vx * clampedDt
        player.y += player.vy * clampedDt

        // Dynamic player ink motion trail renderer
        if (player.vx != 0f || player.vy != 0f) {
            playerTrailSpawnTimer += clampedDt
            if (playerTrailSpawnTimer >= 0.042f) {
                playerTrailSpawnTimer = 0f
                playerMotionTrails.add(
                    PlayerMotionTrailNode(
                        id = ++entityIdCounter,
                        x = player.x,
                        y = player.y,
                        angle = player.lastMoveDirection,
                        life = 0.55f,
                        maxLife = 0.55f,
                        width = 22f + kotlin.random.Random.nextFloat() * 6f
                    )
                )
                if (playerMotionTrails.size > 40) {
                    playerMotionTrails.removeAt(0)
                }
            }
        }

        // Invincibility cooldown
        if (player.isInvincible) {
            player.invincibleTimer -= clampedDt
            if (player.invincibleTimer <= 0f) {
                player.isInvincible = false
            }
        }

        // 2. Red Rune Spawner & Collision (Every 2:30)
        if (newTime >= nextRedRuneTime && redRunes.isEmpty() && !enemySpawningHalted) {
            spawnRedRuneNearPlayer()
            nextRedRuneTime += 150f
        }

        val redRuneIterator = redRunes.iterator()
        while (redRuneIterator.hasNext()) {
            val rune = redRuneIterator.next()
            rune.pulseTimer += clampedDt
            if (hypot(player.x - rune.x, player.y - rune.y) < rune.radius + 18f) {
                redRuneIterator.remove()
                triggerRedRuneElitePack()
                soundManager.playReactionBoom()
                break
            }
        }

        // 3. Obelisk Collision (Defeating Elite pack)
        val obeliskIterator = obelisks.iterator()
        while (obeliskIterator.hasNext()) {
            val obelisk = obeliskIterator.next()
            obelisk.pulseTimer += clampedDt
            if (hypot(player.x - obelisk.x, player.y - obelisk.y) < obelisk.radius + 20f) {
                obeliskIterator.remove()
                triggerObeliskUI()
                break
            }
        }

        // 3.5 Broken Stone Field Collision (Only accessible after minute 25)
        val stoneIterator = brokenStones.iterator()
        while (stoneIterator.hasNext()) {
            val stone = stoneIterator.next()
            stone.pulseTimer += clampedDt
            if (hypot(player.x - stone.x, player.y - stone.y) < stone.radius + 24f) {
                triggerBrokenStoneUI(stone)
                break
            }
        }

        // 4. Broken Stone Spawn: ONLY after minute 25 (1500s)
        val isMin25OrDev = newTime >= 1500f || (isDevTestingMode && newTime >= 30f)
        if (isMin25OrDev && newTime >= nextBrokenStoneTime && !enemySpawningHalted && brokenStones.isEmpty()) {
            nextBrokenStoneTime += 240f
            spawnBrokenStoneNearPlayer()
            soundManager.playReactionBoom()
        }

        // 5. Boss Spawns:
        // The Titan Boss at 10:00 (600s)
        if (newTime >= 600f && !titanSpawned) {
            titanSpawned = true
            spawnTheTitan()
            soundManager.playReactionBoom()
            _uiState.value = _uiState.value.copy(titanSpawned = true)
        }

        // Mid-Boss Scrollkeeper Colossus at 25:00 (1500s)
        if (newTime >= 1500f && !midBossSpawned) {
            midBossSpawned = true
            spawnMidBoss()
            soundManager.playReactionBoom()
            _uiState.value = _uiState.value.copy(midBossSpawned = true)
        }

        // 6. Blank Scroll Collision (25:00 Mid-Boss Spoils)
        val scrollIterator = blankScrollDrops.iterator()
        while (scrollIterator.hasNext()) {
            val scroll = scrollIterator.next()
            scroll.pulseTimer += clampedDt
            if (hypot(player.x - scroll.x, player.y - scroll.y) < scroll.radius + player.pickupRadius * 0.5f) {
                scrollIterator.remove()
                soundManager.playLevelUp()
                triggerBlankScrollUI()
                break
            }
        }

        // ==========================================
        // PHASE 5: THE MID-RUN CHECKPOINT (THE INKWELL) AT 30:00
        // ==========================================
        if (newTime >= 1800f && !inkwellSpawned) {
            inkwellSpawned = true
            enemySpawningHalted = true
            inkwellStructures.add(
                InkwellStructure(
                    id = ++entityIdCounter,
                    x = 0f,
                    y = 0f
                )
            )
            soundManager.playReactionBoom()
        }

        val inkwellIter = inkwellStructures.iterator()
        while (inkwellIter.hasNext()) {
            val inkwell = inkwellIter.next()
            inkwell.pulseTimer += clampedDt
            if (hypot(player.x - inkwell.x, player.y - inkwell.y) < inkwell.radius + 24f) {
                player.hp = player.maxHp
                soundManager.playLevelUp()
                triggerInkwellUI()
                break
            }
        }

        // 7. Spell Auto-Casting on Cooldown
        for (spell in currentState.activeSpells) {
            spell.cooldownTimer -= clampedDt
            if (spell.cooldownTimer <= 0f) {
                spell.cooldownTimer = spell.getEffectiveCooldown(player.attackSpeedMultiplier, player.cooldownMultiplier)
                castSpell(spell, currentState.character)
            }
        }

        // 8. Update Ink Projectiles (with Sacred Geometry Bounces)
        val projIterator = inkProjectiles.iterator()
        while (projIterator.hasNext()) {
            val proj = projIterator.next()
            proj.life -= clampedDt

            if (proj.isFlexNib) {
                proj.strokeWidth *= 1.02f
                proj.strokeLength *= 1.02f
            }

            proj.x += proj.vx * clampedDt
            proj.y += proj.vy * clampedDt

            for (enemy in enemies) {
                if (proj.hitEnemyIds.contains(enemy.id)) continue
                val dist = hypot(enemy.x - proj.x, enemy.y - proj.y)
                if (dist < enemy.type.radius + proj.strokeWidth) {
                    proj.hitEnemyIds.add(enemy.id)
                    damageEnemy(enemy, proj.damage, element = proj.element)

                    // Harpoon heavy pushback
                    if (proj.isHarpoon) {
                        val pushMagnitude = if (enemy.isBoss) 12f else 54f
                        enemy.x += cos(proj.angleRad) * pushMagnitude
                        enemy.y += sin(proj.angleRad) * pushMagnitude
                    }

                    // Ultimate Master's Decree instant obliterating push
                    if (proj.isUltimate) {
                        enemy.x += cos(proj.angleRad) * 60f
                        enemy.y += sin(proj.angleRad) * 60f
                    }

                    if (proj.isSerratedNib) {
                        enemy.bleedTimer = 3.0f
                        enemy.bleedDamagePerTick = (proj.damage * 0.28f).coerceAtLeast(4f)
                    }

                    if (proj.hasViscousRune) {
                        enemy.slowTimer = 2.5f
                        enemy.slowRatio = 0.40f
                    }

                    // Sacred Geometry Bounce Logic
                    if (proj.bounceRemaining > 0) {
                        proj.bounceRemaining--
                        proj.damage *= proj.bounceDamageMultiplier
                        val nextTarget = enemies.firstOrNull { it.id != enemy.id && !proj.hitEnemyIds.contains(it.id) && hypot(it.x - proj.x, it.y - proj.y) < 360f }
                        if (nextTarget != null) {
                            val angle = atan2(nextTarget.y - proj.y, nextTarget.x - proj.x)
                            val speed = hypot(proj.vx, proj.vy)
                            proj.vx = cos(angle) * speed
                            proj.vy = sin(angle) * speed
                            proj.angleRad = angle
                        } else {
                            proj.vx = -proj.vx
                            proj.vy = -proj.vy
                            proj.angleRad = atan2(proj.vy, proj.vx)
                        }
                    } else {
                        proj.pierceCount--
                        if (proj.pierceCount <= 0) break
                    }
                }
            }

            if (proj.life <= 0f || (proj.pierceCount <= 0 && proj.bounceRemaining <= 0)) {
                projIterator.remove()
            }
        }

        // 9. Update Pooled Ink Puddles
        for (i in 0 until puddlePool.pool.size) {
            val puddle = puddlePool.pool[i]
            if (!puddle.active) continue

            puddle.life += clampedDt
            puddle.tickTimer += clampedDt

            if (puddle.tickTimer >= puddle.tickInterval) {
                puddle.tickTimer = 0f
                for (enemy in enemies) {
                    val dist = hypot(enemy.x - puddle.x, enemy.y - puddle.y)
                    if (dist < puddle.radius + enemy.type.radius) {
                        val puddleEl = if (puddle.isBurningCalligraphy) CalligraphicElement.CINNABAR_FLAME else CalligraphicElement.CORROSIVE_ACID
                        damageEnemy(enemy, puddle.damage, element = puddleEl)
                        if (puddle.isFrozenInk && !enemy.isBoss) {
                            enemy.frozenSolidTimer = 1.5f
                        }
                        if (puddle.hasViscousRune) {
                            enemy.slowTimer = 2.5f
                            enemy.slowRatio = 0.40f
                        }
                    }
                }
            }

            if (puddle.life >= puddle.maxLife) {
                puddle.active = false
            }
        }

        // ==========================================
        // PHASE 5: THE BLOTTER PERFORMANCE SPONGE
        // ==========================================
        // If active puddles >= 35 and no Blotter is alive, spawn The Blotter
        val activePuddleCount = puddlePool.activeCount
        if (activePuddleCount >= 35 && enemies.none { it.isBlotter } && !theEraserBoss.active) {
            spawnTheBlotter()
        }

        // 10. Update Inkwell Vortexes (Phase 4 Gravitational Suction)
        val vortexIterator = inkwellVortexes.iterator()
        while (vortexIterator.hasNext()) {
            val vortex = vortexIterator.next()
            vortex.life -= clampedDt
            vortex.angle += 3.5f * clampedDt
            vortex.tickTimer += clampedDt

            for (enemy in enemies) {
                val dx = vortex.x - enemy.x
                val dy = vortex.y - enemy.y
                val dist = hypot(dx, dy)
                if (dist < vortex.radius + enemy.type.radius && dist > 1f) {
                    val pullSpeed = if (enemy.isBoss) 60f else 180f
                    enemy.x += (dx / dist) * pullSpeed * clampedDt
                    enemy.y += (dy / dist) * pullSpeed * clampedDt

                    if (vortex.tickTimer >= vortex.tickInterval) {
                        damageEnemy(enemy, vortex.damage)
                        if (vortex.hasViscousRune) {
                            enemy.slowTimer = 2.5f
                            enemy.slowRatio = 0.40f
                        }
                    }
                }
            }

            if (vortex.tickTimer >= vortex.tickInterval) {
                vortex.tickTimer = 0f
            }

            if (vortex.life <= 0f) {
                vortexIterator.remove()
            }
        }

        // 11. Update Wash Brush Arc Visuals
        val arcIterator = washBrushVisuals.iterator()
        while (arcIterator.hasNext()) {
            val visual = arcIterator.next()
            visual.life -= clampedDt
            if (visual.life <= 0f) arcIterator.remove()
        }

        // 11.1 Update Blotter Burst Visuals
        val burstIterator = blotterBurstVisuals.iterator()
        while (burstIterator.hasNext()) {
            val burst = burstIterator.next()
            burst.timer += clampedDt
            if (burst.timer >= burst.maxDuration) burstIterator.remove()
        }

        // 11.2 Update Orbital Runes (Celestial Orbit)
        val orbitalSpell = _uiState.value.activeSpells.find { it.definition.id == "orbital_runes" }
        if (orbitalSpell != null) {
            val isRapid = orbitalSpell.hasTrait(SpellTraitType.RAPID_ROTATION.id)
            val isExpansion = orbitalSpell.hasTrait(SpellTraitType.ASTRAL_EXPANSION.id)
            val targetCount = if (isExpansion) 4 else 3
            val baseRad = if (isExpansion) 130f else 95f
            val rotSpeed = if (isRapid) 4.5f else 2.8f
            val dmg = orbitalSpell.definition.baseDamage * player.damageMultiplier * (1f + (orbitalSpell.rank - 1) * 0.25f)

            while (orbitalRunes.size < targetCount) {
                orbitalRunes.add(
                    OrbitalRuneEntity(
                        id = ++entityIdCounter,
                        orbitAngle = (orbitalRunes.size * (2 * PI / targetCount)).toFloat(),
                        orbitRadius = baseRad,
                        damage = dmg,
                        hasViscous = isRapid
                    )
                )
            }

            for (rune in orbitalRunes) {
                rune.orbitAngle += rotSpeed * clampedDt
                rune.orbitRadius = baseRad
                rune.damage = dmg
                rune.hasViscous = isRapid
                rune.hitCooldownTimer -= clampedDt

                val rx = player.x + kotlin.math.cos(rune.orbitAngle) * rune.orbitRadius
                val ry = player.y + kotlin.math.sin(rune.orbitAngle) * rune.orbitRadius

                if (rune.hitCooldownTimer <= 0f) {
                    for (enemy in enemies) {
                        val d = kotlin.math.hypot(enemy.x - rx, enemy.y - ry)
                        if (d < enemy.type.radius + 18f) {
                            damageEnemy(enemy, rune.damage, element = CalligraphicElement.CELESTIAL_ASTRAL)
                            if (rune.hasViscous) {
                                enemy.slowTimer = 2.0f
                                enemy.slowRatio = 0.40f
                            }
                            rune.hitCooldownTimer = 0.25f
                            soundManager.playSplatter()
                            break
                        }
                    }
                }
            }
        } else {
            orbitalRunes.clear()
        }

        // 11.3 Update Cinnabar Seals (Detonation Glyphs)
        val sealIter = cinnabarSeals.iterator()
        while (sealIter.hasNext()) {
            val seal = sealIter.next()
            seal.timer += clampedDt
            if (!seal.detonated) {
                if (seal.timer >= seal.fuseTime) {
                    seal.detonated = true
                    soundManager.playReactionBoom()
                    screenShakeTimer = 0.15f
                    for (enemy in enemies) {
                        val d = kotlin.math.hypot(enemy.x - seal.x, enemy.y - seal.y)
                        if (d <= seal.radius + enemy.type.radius) {
                            damageEnemy(enemy, seal.damage, element = CalligraphicElement.CINNABAR_FLAME)
                            if (seal.hasViscous) {
                                enemy.slowTimer = 2.5f
                                enemy.slowRatio = 0.40f
                            }
                        }
                    }
                }
            } else {
                seal.blastTimer += clampedDt
                if (seal.blastTimer >= seal.blastDuration) {
                    sealIter.remove()
                }
            }
        }

        // 11.4 Telemetry Milestones (P0-1: Log level, kills, XP/min, enemies alive at min 2, 5, 10, 20, 30)
        val telemetryMilestones = listOf(2, 5, 10, 20, 30)
        for (m in telemetryMilestones) {
            if (newTime >= m * 60f && !loggedTelemetryMinutes.contains(m)) {
                loggedTelemetryMinutes.add(m)
                val xpPerMin = if (newTime > 0f) (totalXpCollected / (newTime / 60f)).toInt() else 0
                android.util.Log.i(
                    "InkSurvivorTelemetry",
                    "[TELEMETRY] Minute: $m | Level: ${player.level} | Kills: ${_uiState.value.kills} | TotalXP: $totalXpCollected | XP/min: $xpPerMin | EnemiesAlive: ${enemies.size}"
                )
            }
        }

        // 12. Demanding Spawn Rate Scaling & Capping (Smooth acceleration demanding active combat)
        val hpMultiplier = if (newTime <= 1800f) {
            1.0f + (newTime / 1800f) * 3.2f
        } else {
            4.2f
        }

        val speedMultiplier = if (newTime <= 1800f) {
            1.0f + (newTime / 1800f) * 0.45f
        } else {
            1.45f + ((newTime - 1800f) / 1800f) * 1.0f
        }

        // Spawn interval gets progressively demanding (more enemies over time!)
        val spawnInterval = when {
            newTime < 180f -> 0.65f - (newTime / 180f) * 0.22f          // 0.65s down to 0.43s
            newTime < 600f -> 0.43f - ((newTime - 180f) / 420f) * 0.17f // 0.43s down to 0.26s
            newTime < 1200f -> 0.26f - ((newTime - 600f) / 600f) * 0.10f// 0.26s down to 0.16s
            else -> (0.16f - ((newTime - 1200f) / 1800f) * 0.07f).coerceAtLeast(0.09f) // 0.16s down to 0.09s
        }

        // Target: 30+ enemies alive at minute 5, 80+ at minute 10
        val maxEnemies = when {
            newTime < 180f -> 45 + ((newTime / 180f) * 25f).toInt() // 45 -> 70
            newTime < 300f -> 70 + (((newTime - 180f) / 120f) * 20f).toInt() // 70 -> 90
            newTime < 600f -> 90 + (((newTime - 300f) / 300f) * 25f).toInt() // 90 -> 115
            newTime < 1200f -> 115 + (((newTime - 600f) / 600f) * 25f).toInt() // 115 -> 140
            else -> 140
        }

        enemySpawnTimer += clampedDt
        if (enemySpawnTimer >= spawnInterval && enemies.size < maxEnemies && !enemySpawningHalted && !theEraserBoss.active) {
            enemySpawnTimer = 0f
            val swarmChance = if (newTime >= 1500f) 0.55f else if (newTime >= 300f) 0.35f else if (newTime >= 180f) 0.20f else 0f
            if (Random.nextFloat() < swarmChance) {
                spawnHordeSwarm(hpMultiplier, speedMultiplier)
            } else {
                val spawnCount = if (newTime >= 300f && enemies.size + 2 <= maxEnemies && Random.nextFloat() < 0.4f) 2 else 1
                for (i in 0 until spawnCount) {
                    spawnEnemyOutsideViewport(hpMultiplier, speedMultiplier)
                }
            }
        }

        // In-Field Ambient Spawned Orbs (rewards exploration & movement!)
        fieldOrbSpawnTimer += clampedDt
        if (fieldOrbSpawnTimer >= 14f && orbs.size < 35 && !enemySpawningHalted) {
            fieldOrbSpawnTimer = 0f
            val orbAngle = Random.nextFloat() * 2 * PI.toFloat()
            val orbDist = 280f + Random.nextFloat() * 200f
            val isAboveMin25 = newTime >= 1500f || isDevTestingMode
            val fieldTier = if (isAboveMin25) {
                if (Random.nextFloat() < 0.35f) 3 else 2
            } else {
                1
            }
            val fieldVal = when (fieldTier) {
                3 -> 25
                2 -> 8
                else -> 2
            }
            orbs.add(
                Orb(
                    id = ++entityIdCounter,
                    x = player.x + cos(orbAngle) * orbDist,
                    y = player.y + sin(orbAngle) * orbDist,
                    value = fieldVal,
                    tier = fieldTier
                )
            )
        }

        // 13. Update Enemies & AI with Collision & Separation
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()

            if (enemy.flashTimer > 0f) enemy.flashTimer -= clampedDt
            if (enemy.attackCooldown > 0f) enemy.attackCooldown -= clampedDt
            if (enemy.elementalReactionCooldown > 0f) enemy.elementalReactionCooldown -= clampedDt
            if (enemy.frozenSolidTimer > 0f) enemy.frozenSolidTimer -= clampedDt
            if (enemy.frostTimer > 0f) enemy.frostTimer -= clampedDt
            if (enemy.acidTimer > 0f) enemy.acidTimer -= clampedDt
            if (enemy.astralTimer > 0f) enemy.astralTimer -= clampedDt

            // Burning flame ticking damage
            if (enemy.flameTimer > 0f) {
                enemy.flameTimer -= clampedDt
                enemy.burningTickTimer += clampedDt
                if (enemy.burningTickTimer >= 0.40f) {
                    enemy.burningTickTimer = 0f
                    damageEnemy(enemy, enemy.burningDamagePerTick, isBleed = false, isReaction = true)
                    spawnInkFluidSplash(enemy.x, enemy.y, count = 2, isCritOrBleed = true, baseDmg = enemy.burningDamagePerTick)
                }
            }

            if (enemy.bleedTimer > 0f) {
                enemy.bleedTimer -= clampedDt
                enemy.bleedTickTimer += clampedDt
                if (enemy.bleedTickTimer >= 0.45f) {
                    enemy.bleedTickTimer = 0f
                    damageEnemy(enemy, enemy.bleedDamagePerTick, isBleed = true)
                }
            }

            if (enemy.slowTimer > 0f) {
                enemy.slowTimer -= clampedDt
                if (enemy.slowTimer <= 0f) enemy.slowRatio = 0f
            }

            if (enemy.snareTimer > 0f) {
                enemy.snareTimer -= clampedDt
                if (enemy.snareTimer <= 0f) enemy.isSnared = false
            }

            // Pathfinding: The Blotter pathfinds to densest puddle cluster; other enemies chase player
            if (enemy.isBlotter) {
                val targetPuddle = puddlePool.pool.filter { it.active }
                    .minByOrNull { hypot(it.x - enemy.x, it.y - enemy.y) }

                if (targetPuddle != null) {
                    val dx = targetPuddle.x - enemy.x
                    val dy = targetPuddle.y - enemy.y
                    val dist = hypot(dx, dy)
                    if (dist > 1f) {
                        enemy.vx = (dx / dist) * enemy.type.speed
                        enemy.vy = (dy / dist) * enemy.type.speed
                        enemy.x += enemy.vx * clampedDt
                        enemy.y += enemy.vy * clampedDt
                    }
                    if (dist < enemy.type.radius + targetPuddle.radius * 0.7f) {
                        targetPuddle.active = false
                        enemy.absorbedPuddles++
                        soundManager.playSplatter()
                    }
                }
            } else if (magnumOpusFreezeTimer <= 0f) {
                val dx = player.x - enemy.x
                val dy = player.y - enemy.y
                val dist = hypot(dx, dy)
                val moveSpeed = enemy.effectiveSpeed * speedMultiplier

                if (dist > 1f) {
                    val nx = dx / dist
                    val ny = dy / dist
                    enemy.vx = nx * moveSpeed
                    enemy.vy = ny * moveSpeed
                    enemy.x += enemy.vx * clampedDt
                    enemy.y += enemy.vy * clampedDt
                }

                // Enemy-to-enemy soft-body separation (optimized with squared distance)
                for (other in enemies) {
                    if (other.id == enemy.id) continue
                    val sepDx = enemy.x - other.x
                    val sepDy = enemy.y - other.y
                    val distSq = sepDx * sepDx + sepDy * sepDy
                    val minDist = enemy.type.radius + other.type.radius
                    val minDistSq = minDist * minDist
                    if (distSq < minDistSq && distSq > 0.0025f) {
                        val sepDist = kotlin.math.sqrt(distSq)
                        val overlap = (minDist - sepDist) * 0.5f
                        val invDist = 1.0f / sepDist
                        val sepNx = sepDx * invDist
                        val sepNy = sepDy * invDist
                        enemy.x += sepNx * overlap * 0.12f
                        enemy.y += sepNy * overlap * 0.12f
                    }
                }

                // Physical collision with Player & Contact Damage
                val distToPlayer = hypot(player.x - enemy.x, player.y - enemy.y)
                val touchDist = enemy.type.radius + 18f
                if (distToPlayer < touchDist && distToPlayer > 0.1f) {
                    val pushX = (player.x - enemy.x) / distToPlayer
                    val pushY = (player.y - enemy.y) / distToPlayer
                    player.x += pushX * 2.8f
                    player.y += pushY * 2.8f

                    if (!player.isInvincible && enemy.attackCooldown <= 0f && enemy.type.damage > 0f) {
                        enemy.attackCooldown = 0.75f
                        val dmg = enemy.type.damage * (if (enemy.isElite) 1.5f else if (enemy.isBoss) 2.2f else 1.0f)
                        damagePlayer(dmg)
                    }
                }
            }

            // Death handling
            if (enemy.isDead) {
                if (enemy.isBlotter) {
                    // Blotter burst AOE explosion
                    val aoeRadius = 220f
                    val aoeDamage = 350f + enemy.absorbedPuddles * 35f
                    blotterBurstVisuals.add(
                        BlotterBurstVisual(
                            id = ++entityIdCounter,
                            x = enemy.x,
                            y = enemy.y,
                            radius = aoeRadius
                        )
                    )
                    soundManager.playReactionBoom()
                    screenShakeTimer = 0.5f

                    for (other in enemies) {
                        if (other.id != enemy.id) {
                            if (hypot(other.x - enemy.x, other.y - enemy.y) <= aoeRadius) {
                                damageEnemy(other, aoeDamage)
                            }
                        }
                    }
                } else if (enemy.type == EnemyType.THE_TITAN) {
                    orbs.add(
                        Orb(
                            id = ++entityIdCounter,
                            x = enemy.x,
                            y = enemy.y,
                            value = 50,
                            isCondensed = true
                        )
                    )
                    soundManager.playLevelUp()
                } else if (enemy.type == EnemyType.MID_BOSS_COLOSSUS) {
                    blankScrollDrops.add(
                        BlankScrollDrop(
                            id = ++entityIdCounter,
                            x = enemy.x,
                            y = enemy.y
                        )
                    )
                    soundManager.playReactionBoom()
                } else if (enemy.isElite && enemy.elitePackId != null) {
                    val packId = enemy.elitePackId!!
                    val remaining = (elitePackCounts[packId] ?: 1) - 1
                    elitePackCounts[packId] = remaining

                    if (remaining <= 0) {
                        elitePackCounts.remove(packId)
                        val droppedRune = SpellRuneType.values().random()
                        spawnSpellRuneDrop(enemy.x, enemy.y, droppedRune)
                        spawnObelisk(enemy.x + 20f, enemy.y + 20f)
                        soundManager.playReactionBoom()
                    }
                }

                // Detonating Mark on death explosion
                if (enemy.isMarkedForDetonation) {
                    val markRadius = 140f
                    val markDmg = 85f
                    soundManager.playReactionBoom()
                    spawnInkFluidSplash(enemy.x, enemy.y, count = 8, isCritOrBleed = true, baseDmg = markDmg)
                    for (other in enemies) {
                        if (other.id != enemy.id && hypot(other.x - enemy.x, other.y - enemy.y) <= markRadius) {
                            damageEnemy(other, markDmg)
                        }
                    }
                }

                if (!enemy.isBlotter && enemy.type != EnemyType.THE_TITAN && enemy.type != EnemyType.MID_BOSS_COLOSSUS) {
                    val isAboveMin25 = newTime >= 1500f || isDevTestingMode
                    val orbTier = when {
                        !isAboveMin25 -> 1 // Only lowest tier for lowest tier enemy before minute 25
                        enemy.isBoss -> 3
                        enemy.isElite -> if (Random.nextFloat() < 0.60f) 3 else 2
                        enemy.type == EnemyType.PAPER_BRUTE -> if (Random.nextFloat() < 0.45f) 3 else 2
                        enemy.type == EnemyType.FOLDED_STALKER -> 2
                        else -> if (Random.nextFloat() < 0.30f) 2 else 1
                    }
                    val orbValue = when (orbTier) {
                        3 -> (enemy.type.xpValue * 4).coerceAtLeast(20)
                        2 -> (enemy.type.xpValue * 2).coerceAtLeast(6)
                        else -> enemy.type.xpValue
                    }
                    orbs.add(
                        Orb(
                            id = ++entityIdCounter,
                            x = enemy.x,
                            y = enemy.y,
                            value = orbValue,
                            tier = orbTier
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    kills = _uiState.value.kills + 1,
                    score = _uiState.value.score + (if (enemy.isBoss) 600 else if (enemy.isElite) 60 else 10)
                )

                enemyIterator.remove()
            }
        }

        // 14. Update Spell Rune Drops
        val spellRuneIterator = spellRuneDrops.iterator()
        while (spellRuneIterator.hasNext()) {
            val drop = spellRuneIterator.next()
            drop.pulseTimer += clampedDt
            if (hypot(player.x - drop.x, player.y - drop.y) < drop.radius + player.pickupRadius * 0.4f) {
                spellRuneIterator.remove()
                soundManager.playLevelUp()
                triggerRuneSocketUI(drop.runeType)
                break
            }
        }

        // 15. Update Orbs (Attraction & pickup)
        val orbIterator = orbs.iterator()
        while (orbIterator.hasNext()) {
            val orb = orbIterator.next()
            orb.pulseTimer += clampedDt
            val dx = player.x - orb.x
            val dy = player.y - orb.y
            val dist = hypot(dx, dy)

            if (dist < player.pickupRadius) {
                val pullSpeed = if (orb.isCondensed) 340f else 430f
                orb.x += (dx / dist) * pullSpeed * clampedDt
                orb.y += (dy / dist) * pullSpeed * clampedDt
            }

            if (dist < 26f) {
                collectOrb(orb)
                orbIterator.remove()
            }
        }

        // 16. Update Damage Numbers
        val dmgIterator = damageNumbers.iterator()
        while (dmgIterator.hasNext()) {
            val dn = dmgIterator.next()
            dn.life -= clampedDt
            dn.y += dn.vy * clampedDt
            if (dn.life <= 0f) dmgIterator.remove()
        }

        // 16.1 Fluid Simulation Particle System (Viscous Ink Splash Physics)
        val splashIterator = inkSplashParticles.iterator()
        while (splashIterator.hasNext()) {
            val p = splashIterator.next()
            p.life -= clampedDt
            if (p.life <= 0f) {
                splashIterator.remove()
                continue
            }
            p.x += p.vx * clampedDt
            p.y += p.vy * clampedDt
            val drag = (1f - p.viscosity * clampedDt).coerceIn(0f, 1f)
            p.vx *= drag
            p.vy *= drag
            val lifeRatio = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
            p.radius = p.maxRadius * (0.60f + 0.40f * lifeRatio)
        }

        // 16.2 Player Motion Trail Updates
        val trailIterator = playerMotionTrails.iterator()
        while (trailIterator.hasNext()) {
            val node = trailIterator.next()
            node.life -= clampedDt
            if (node.life <= 0f) {
                trailIterator.remove()
            }
        }

        // 16.3 Flowing Serpent Spell Simulation
        val serpentIterator = flowingSerpents.iterator()
        while (serpentIterator.hasNext()) {
            val serpent = serpentIterator.next()
            serpent.life -= clampedDt
            serpent.waveTimer += clampedDt
            serpent.tickTimer += clampedDt

            if (enemies.isNotEmpty()) {
                val targetE = enemies.minByOrNull { hypot(it.x - serpent.x, it.y - serpent.y) }
                if (targetE != null) {
                    val desiredAngle = atan2(targetE.y - serpent.y, targetE.x - serpent.x)
                    var angleDiff = desiredAngle - serpent.currentAngle
                    while (angleDiff > PI) angleDiff -= (2 * PI).toFloat()
                    while (angleDiff < -PI) angleDiff += (2 * PI).toFloat()
                    serpent.currentAngle += angleDiff.coerceIn(-4.2f * clampedDt, 4.2f * clampedDt)
                }
            }

            val undulatingAngle = serpent.currentAngle + sin(serpent.waveTimer * 10f) * 0.35f
            serpent.x += cos(undulatingAngle) * serpent.speed * clampedDt
            serpent.y += sin(undulatingAngle) * serpent.speed * clampedDt

            serpent.segments.add(0, Offset(serpent.x, serpent.y))
            if (serpent.segments.size > 16) {
                serpent.segments.removeAt(serpent.segments.size - 1)
            }

            if (serpent.tickTimer >= 0.22f) {
                serpent.tickTimer = 0f
                for (enemy in enemies) {
                    val headDist = hypot(enemy.x - serpent.x, enemy.y - serpent.y)
                    val hitsBody = headDist < serpent.radius + enemy.type.radius ||
                        serpent.segments.any { hypot(enemy.x - it.x, enemy.y - it.y) < serpent.radius * 0.75f + enemy.type.radius }
                    if (hitsBody) {
                        damageEnemy(enemy, serpent.damage * 0.38f, element = CalligraphicElement.CELESTIAL_ASTRAL)
                        spawnInkFluidSplash(enemy.x, enemy.y, count = 2, baseDmg = serpent.damage)
                    }
                }
            }

            if (serpent.life <= 0f) {
                spawnInkFluidSplash(serpent.x, serpent.y, count = 8, baseDmg = serpent.damage)
                serpentIterator.remove()
            }
        }

        // 16.4 Update Screen Elemental Reactions
        val reactionIterator = elementalReactionVisuals.iterator()
        while (reactionIterator.hasNext()) {
            val visual = reactionIterator.next()
            visual.timer += clampedDt
            if (visual.isFinished) {
                reactionIterator.remove()
            }
        }

        // 16.5 Update Elemental Particles (Ice Shards & Fire Embers)
        val partIterator = elementalParticles.iterator()
        while (partIterator.hasNext()) {
            val p = partIterator.next()
            p.life -= clampedDt
            if (p.life <= 0f) {
                partIterator.remove()
                continue
            }
            p.x += p.vx * clampedDt
            p.y += p.vy * clampedDt
            p.vx *= (1f - 1.8f * clampedDt).coerceIn(0f, 1f)
            p.vy *= (1f - 1.8f * clampedDt).coerceIn(0f, 1f)
            p.angle += p.rotSpeed * clampedDt
        }

        // 16.6 Update High-Contrast Ink-Brush Splash Animation Particles
        val splashIter = inkBrushSplashParticles.iterator()
        while (splashIter.hasNext()) {
            val p = splashIter.next()
            p.life -= clampedDt
            if (p.life <= 0f) {
                splashIter.remove()
                continue
            }
            p.x += p.vx * clampedDt
            p.y += p.vy * clampedDt
            p.vx *= (1f - 2.8f * clampedDt).coerceIn(0f, 1f)
            p.vy *= (1f - 2.8f * clampedDt).coerceIn(0f, 1f)
            val lifeRatio = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
            p.radius = p.maxRadius * (0.4f + 0.6f * lifeRatio)
        }

        // Update impact frame timer
        if (impactFrameTimer > 0f) {
            impactFrameTimer -= clampedDt
            if (impactFrameTimer <= 0f) {
                impactFrameTimer = 0f
                activeImpactReaction = null
            }
        }

        // Update active banner timer
        if (activeElementalBannerTimer > 0f) {
            activeElementalBannerTimer -= clampedDt
            if (activeElementalBannerTimer <= 0f) {
                activeElementalBanner = null
            }
        }

        _uiState.value = _uiState.value.copy(
            timeSurvivedSeconds = newTime,
            redRuneActive = redRunes.isNotEmpty(),
            screenShakeTimer = screenShakeTimer,
            eraserActive = theEraserBoss.active,
            eraserSafeRadius = theEraserBoss.currentSafeRadius
        )
    }

    private fun castSpell(activeSpell: ActiveSpell, character: CharacterDefinition) {
        val facingDir = player.lastMoveDirection
        val damage = activeSpell.getEffectiveDamage(player.damageMultiplier)
        val hasViscous = activeSpell.hasViscousRune()
        val spellElement = activeSpell.element

        val hasRuler = _uiState.value.equippedArtifacts.any { it.id == ArtifactDefinition.TheFracturedRuler.id }
        val isGeometryMastered = masteryManager.isSacredGeometryMastered
        val totalArtifactBounces = _uiState.value.equippedArtifacts.sumOf { it.projectileBounces }
        val bounceCount = (if (hasRuler) 5 else 0) + totalArtifactBounces + player.projectileBounces
        val bounceDelta = if (hasRuler) {
            if (isGeometryMastered) 1.30f else 0.70f
        } else 1.0f

        when (activeSpell.definition.castType) {
            SpellCastType.DIRECTIONAL_PROJECTILE -> {
                soundManager.playSwoosh()
                val speed = activeSpell.definition.baseSpeed * character.projectileSpeedMultiplier
                val pierce = activeSpell.definition.basePierce + character.bonusPierce

                val isFlexNib = activeSpell.hasTrait(SpellTraitType.FLEX_NIB.id)
                val isSerratedNib = activeSpell.hasTrait(SpellTraitType.SERRATED_NIB.id)
                val isHarpoon = activeSpell.definition.isHarpoon
                val isUltimate = activeSpell.definition.isUltimate

                if (isUltimate) {
                    for (i in 0 until 8) {
                        val angle = (i * PI / 4).toFloat()
                        inkProjectiles.add(
                            InkProjectile(
                                id = ++entityIdCounter,
                                x = player.x,
                                y = player.y,
                                vx = cos(angle) * speed,
                                vy = sin(angle) * speed,
                                angleRad = angle,
                                damage = damage,
                                pierceCount = pierce,
                                strokeLength = activeSpell.definition.projectileLength,
                                strokeWidth = activeSpell.definition.projectileWidth,
                                isUltimate = true,
                                sourceSpellId = activeSpell.definition.id,
                                hasViscousRune = hasViscous,
                                element = spellElement,
                                bounceRemaining = bounceCount,
                                bounceDamageMultiplier = bounceDelta,
                                isSacredGeometry = hasRuler,
                                isGeometryCured = isGeometryMastered
                            )
                        )
                    }
                } else {
                    val shootAngle = if (enemies.isNotEmpty()) {
                        var nearestDist = Float.MAX_VALUE
                        var nearestEnemy: Enemy? = null
                        for (enemy in enemies) {
                            val d = hypot(enemy.x - player.x, enemy.y - player.y)
                            if (d < nearestDist) {
                                nearestDist = d
                                nearestEnemy = enemy
                            }
                        }
                        if (nearestEnemy != null && nearestDist <= 750f) {
                            atan2(nearestEnemy.y - player.y, nearestEnemy.x - player.x)
                        } else {
                            facingDir
                        }
                    } else {
                        facingDir
                    }

                    val extraProj = character.bonusProjectiles + player.bonusProjectiles
                    val totalProjCount = 1 + extraProj
                    val spreadAngle = 0.12f // gentle calligraphic fan
                    val startAngle = shootAngle - ((totalProjCount - 1) * spreadAngle) / 2f

                    for (pIdx in 0 until totalProjCount) {
                        val currentAngle = startAngle + pIdx * spreadAngle
                        inkProjectiles.add(
                            InkProjectile(
                                id = ++entityIdCounter,
                                x = player.x,
                                y = player.y,
                                vx = cos(currentAngle) * speed,
                                vy = sin(currentAngle) * speed,
                                angleRad = currentAngle,
                                damage = damage,
                                pierceCount = pierce,
                                strokeLength = activeSpell.definition.projectileLength,
                                strokeWidth = activeSpell.definition.projectileWidth,
                                isFlexNib = isFlexNib,
                                isSerratedNib = isSerratedNib,
                                isHarpoon = isHarpoon,
                                sourceSpellId = activeSpell.definition.id,
                                hasViscousRune = hasViscous,
                                bounceRemaining = bounceCount,
                                bounceDamageMultiplier = bounceDelta,
                                isSacredGeometry = hasRuler,
                                isGeometryCured = isGeometryMastered
                            )
                        )
                    }
                }
            }

            SpellCastType.OMNIDIRECTIONAL_BARRAGE -> {
                soundManager.playSwoosh()
                val speed = activeSpell.definition.baseSpeed * character.projectileSpeedMultiplier
                val count = 12
                for (i in 0 until count) {
                    val angle = (i * 2 * PI / count).toFloat()
                    inkProjectiles.add(
                        InkProjectile(
                            id = ++entityIdCounter,
                            x = player.x,
                            y = player.y,
                            vx = cos(angle) * speed,
                            vy = sin(angle) * speed,
                            angleRad = angle,
                            damage = damage,
                            pierceCount = activeSpell.definition.basePierce,
                            strokeLength = activeSpell.definition.projectileLength,
                            strokeWidth = activeSpell.definition.projectileWidth,
                            isBarrage = true,
                            sourceSpellId = activeSpell.definition.id,
                            hasViscousRune = hasViscous,
                            bounceRemaining = bounceCount,
                            bounceDamageMultiplier = bounceDelta,
                            isSacredGeometry = hasRuler,
                            isGeometryCured = isGeometryMastered
                        )
                    )
                }
            }

            SpellCastType.PHYSICS_OVERLAP_ARC -> {
                soundManager.playSplatter()

                if (activeSpell.definition.isVortex) {
                    inkwellVortexes.add(
                        InkwellVortexEntity(
                            id = ++entityIdCounter,
                            x = player.x,
                            y = player.y,
                            radius = activeSpell.definition.arcRadius,
                            damage = damage,
                            life = 5.0f,
                            hasViscousRune = hasViscous
                        )
                    )
                } else {
                    val isWideBristle = activeSpell.hasTrait(SpellTraitType.WIDE_BRISTLE.id)
                    val arcRadius = (if (isWideBristle) activeSpell.definition.arcRadius * 1.25f else activeSpell.definition.arcRadius) * player.aoeMultiplier
                    val arcSpan = if (isWideBristle) activeSpell.definition.arcAngleSpanRad * 1.4f else activeSpell.definition.arcAngleSpanRad

                    washBrushVisuals.add(
                        WashBrushArcVisual(
                            id = ++entityIdCounter,
                            x = player.x,
                            y = player.y,
                            angleRad = facingDir,
                            arcSpanRad = arcSpan,
                            radius = arcRadius
                        )
                    )

                    for (enemy in enemies) {
                        val dx = enemy.x - player.x
                        val dy = enemy.y - player.y
                        val dist = hypot(dx, dy)
                        if (dist <= arcRadius + enemy.type.radius) {
                            val enemyAngle = atan2(dy, dx)
                            var angleDiff = abs(enemyAngle - facingDir)
                            if (angleDiff > PI) angleDiff = (2 * PI - angleDiff).toFloat()
                            if (angleDiff <= arcSpan / 2f) {
                                damageEnemy(enemy, damage)
                                if (hasViscous) {
                                    enemy.slowTimer = 2.5f
                                    enemy.slowRatio = 0.40f
                                }
                            }
                        }
                    }

                    // Leave 2D pooled ink puddle decal
                    val puddleRadius = (if (activeSpell.hasTrait(SpellTraitType.DEEP_WELL.id)) 48f else 36f) * player.aoeMultiplier
                    val puddleDmg = damage * 0.40f
                    val puddleMaxLife = if (activeSpell.hasTrait(SpellTraitType.DEEP_WELL.id)) 6.0f else 4.0f
                    puddlePool.obtain(
                        x = player.x + cos(facingDir) * (arcRadius * 0.5f),
                        y = player.y + sin(facingDir) * (arcRadius * 0.5f),
                        radius = puddleRadius,
                        damage = puddleDmg,
                        maxLife = puddleMaxLife,
                        sourceSpellId = activeSpell.definition.id,
                        hasViscousRune = hasViscous
                    )
                }
            }

            SpellCastType.WAVE_TRAIL -> {
                soundManager.playSwoosh()
                val speed = activeSpell.definition.baseSpeed * character.projectileSpeedMultiplier
                val pierce = activeSpell.definition.basePierce + character.bonusPierce
                inkProjectiles.add(
                    InkProjectile(
                        id = ++entityIdCounter,
                        x = player.x,
                        y = player.y,
                        vx = cos(facingDir) * speed,
                        vy = sin(facingDir) * speed,
                        angleRad = facingDir,
                        damage = damage,
                        pierceCount = pierce,
                        strokeLength = activeSpell.definition.projectileLength,
                        strokeWidth = activeSpell.definition.projectileWidth,
                        sourceSpellId = activeSpell.definition.id,
                        hasViscousRune = hasViscous,
                        bounceRemaining = bounceCount,
                        bounceDamageMultiplier = bounceDelta,
                        isSacredGeometry = hasRuler,
                        isGeometryCured = isGeometryMastered
                    )
                )
                // In wake of wave trail, spawn an ink surge puddle
                puddlePool.obtain(
                    x = player.x,
                    y = player.y,
                    radius = 42f,
                    damage = damage * 0.5f,
                    maxLife = 2.5f,
                    sourceSpellId = activeSpell.definition.id,
                    hasViscousRune = hasViscous
                )
            }

            SpellCastType.ORBITAL_RUNES -> {
                soundManager.playSwoosh()
                for (rune in orbitalRunes) {
                    rune.hitCooldownTimer = 0f
                }
            }

            SpellCastType.DETONATION_SEAL -> {
                soundManager.playSwoosh()
                var targetX = player.x + kotlin.math.cos(facingDir) * 120f
                var targetY = player.y + kotlin.math.sin(facingDir) * 120f
                if (enemies.isNotEmpty()) {
                    var nearestD = Float.MAX_VALUE
                    var nearestE: Enemy? = null
                    for (e in enemies) {
                        val d = kotlin.math.hypot(e.x - player.x, e.y - player.y)
                        if (d < nearestD) {
                            nearestD = d
                            nearestE = e
                        }
                    }
                    if (nearestE != null && nearestD <= 600f) {
                        targetX = nearestE.x
                        targetY = nearestE.y
                    }
                }

                val hasVolatile = activeSpell.hasTrait(SpellTraitType.VOLATILE_CORE.id)
                val sealRadius = (if (hasVolatile) activeSpell.definition.arcRadius * 1.45f else activeSpell.definition.arcRadius) * player.aoeMultiplier
                val sealDmg = if (hasVolatile) damage * 1.55f else damage

                cinnabarSeals.add(
                    CinnabarSealEntity(
                        id = ++entityIdCounter,
                        x = targetX,
                        y = targetY,
                        radius = sealRadius,
                        damage = sealDmg,
                        hasViscous = hasViscous
                    )
                )
            }

            SpellCastType.FOOTPRINT_TRAIL -> {
                // Searing sumi-e footprints / wake trail behind the player
                // Properties: size (areaRadius), duration (durationSeconds), damage
                val trailRadius = activeSpell.getEffectiveAreaRadius(player.aoeMultiplier)
                val trailDuration = activeSpell.getEffectiveDuration()
                val trailDmg = damage * 0.45f

                // Drop slightly behind current movement direction
                val footX = player.x - kotlin.math.cos(facingDir) * 18f
                val footY = player.y - kotlin.math.sin(facingDir) * 18f

                puddlePool.obtain(
                    x = footX,
                    y = footY,
                    radius = trailRadius,
                    damage = trailDmg,
                    maxLife = trailDuration,
                    sourceSpellId = activeSpell.definition.id,
                    hasViscousRune = hasViscous
                )
                spawnInkFluidSplash(footX, footY, count = 3, baseDmg = trailDmg)
            }

            SpellCastType.FLOWING_SERPENT -> {
                soundManager.playSwoosh()
                val speed = if (activeSpell.definition.baseSpeed > 0f) activeSpell.definition.baseSpeed * character.projectileSpeedMultiplier else 400f
                val life = activeSpell.getEffectiveDuration()
                val serpentRadius = activeSpell.getEffectiveAreaRadius()

                var targetAngle = facingDir
                if (enemies.isNotEmpty()) {
                    val targetE = enemies.minByOrNull { kotlin.math.hypot(it.x - player.x, it.y - player.y) }
                    if (targetE != null) {
                        targetAngle = kotlin.math.atan2(targetE.y - player.y, targetE.x - player.x)
                    }
                }

                flowingSerpents.add(
                    FlowingSerpentEntity(
                        id = ++entityIdCounter,
                        x = player.x,
                        y = player.y,
                        targetAngle = targetAngle,
                        currentAngle = facingDir,
                        speed = speed,
                        damage = damage,
                        radius = serpentRadius,
                        life = life,
                        maxLife = life
                    )
                )
            }
        }
    }

    private fun spawnEnemyOutsideViewport(hpMult: Float = 1.0f, speedMult: Float = 1.0f) {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        // Outside screen frame (prevents spawning directly in frame)
        val spawnDistance = 550f + Random.nextFloat() * 70f
        val ex = player.x + cos(angle) * spawnDistance
        val ey = player.y + sin(angle) * spawnDistance

        val roll = Random.nextFloat()
        val type = when {
            roll < 0.65f -> EnemyType.BASIC_CONSTRUCT
            roll < 0.88f -> EnemyType.FOLDED_STALKER
            else -> EnemyType.PAPER_BRUTE
        }

        val hp = type.baseHp * hpMult
        enemies.add(
            Enemy(
                id = ++entityIdCounter,
                type = type,
                x = ex,
                y = ey,
                hp = hp,
                maxHp = hp
            )
        )
    }

    private fun spawnHordeSwarm(hpMult: Float, speedMult: Float) {
        val baseAngle = Random.nextFloat() * 2 * PI.toFloat()
        val clusterCount = Random.nextInt(4, 7)
        for (i in 0 until clusterCount) {
            val angle = baseAngle + (i - clusterCount / 2f) * 0.18f
            val dist = 560f + Random.nextFloat() * 60f
            val ex = player.x + cos(angle) * dist
            val ey = player.y + sin(angle) * dist
            val type = if (Random.nextFloat() < 0.7f) EnemyType.BASIC_CONSTRUCT else EnemyType.FOLDED_STALKER
            val hp = type.baseHp * hpMult
            enemies.add(
                Enemy(
                    id = ++entityIdCounter,
                    type = type,
                    x = ex,
                    y = ey,
                    hp = hp,
                    maxHp = hp
                )
            )
        }
    }

    private fun spawnTheTitan() {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val ex = player.x + cos(angle) * 500f
        val ey = player.y + sin(angle) * 500f

        enemies.add(
            Enemy(
                id = ++entityIdCounter,
                type = EnemyType.THE_TITAN,
                x = ex,
                y = ey,
                hp = EnemyType.THE_TITAN.baseHp,
                maxHp = EnemyType.THE_TITAN.baseHp
            )
        )
    }

    private fun spawnMidBoss() {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val ex = player.x + cos(angle) * 520f
        val ey = player.y + sin(angle) * 520f

        enemies.add(
            Enemy(
                id = ++entityIdCounter,
                type = EnemyType.MID_BOSS_COLOSSUS,
                x = ex,
                y = ey,
                hp = EnemyType.MID_BOSS_COLOSSUS.baseHp,
                maxHp = EnemyType.MID_BOSS_COLOSSUS.baseHp,
                isMidBoss = true
            )
        )
    }

    private fun spawnTheBlotter() {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val ex = player.x + cos(angle) * 480f
        val ey = player.y + sin(angle) * 480f

        enemies.add(
            Enemy(
                id = ++entityIdCounter,
                type = EnemyType.THE_BLOTTER,
                x = ex,
                y = ey,
                hp = EnemyType.THE_BLOTTER.baseHp,
                maxHp = EnemyType.THE_BLOTTER.baseHp,
                isBlotter = true
            )
        )
        soundManager.playReactionBoom()
    }

    private fun spawnRedRuneNearPlayer() {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val dist = 220f + Random.nextFloat() * 60f
        redRunes.add(
            RedRuneEntity(
                id = ++entityIdCounter,
                x = player.x + cos(angle) * dist,
                y = player.y + sin(angle) * dist
            )
        )
    }

    private fun triggerRedRuneElitePack() {
        val packId = ++nextElitePackId
        elitePackCounts[packId] = 6
        val ringRadius = 260f

        for (i in 0 until 6) {
            val angle = (i * (2 * PI / 6)).toFloat()
            val ex = player.x + cos(angle) * ringRadius
            val ey = player.y + sin(angle) * ringRadius

            val eliteHp = EnemyType.BASIC_CONSTRUCT.baseHp * 5f
            enemies.add(
                Enemy(
                    id = ++entityIdCounter,
                    type = EnemyType.BASIC_CONSTRUCT,
                    x = ex,
                    y = ey,
                    hp = eliteHp,
                    maxHp = eliteHp,
                    isElite = true,
                    elitePackId = packId
                )
            )
        }
    }

    private fun spawnObelisk(x: Float, y: Float) {
        obelisks.add(
            ObeliskEntity(
                id = ++entityIdCounter,
                x = x,
                y = y
            )
        )
    }

    private fun triggerObeliskUI() {
        val choices = ArtifactDefinition.ObeliskList.shuffled().take(3)
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.OBELISK,
            obeliskChoices = choices
        )
    }

    private fun spawnBrokenStoneNearPlayer() {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val dist = 240f + Random.nextFloat() * 50f
        brokenStones.add(
            BrokenStoneEntity(
                id = ++entityIdCounter,
                x = player.x + cos(angle) * dist,
                y = player.y + sin(angle) * dist,
                orbCost = 15
            )
        )
    }

    fun triggerInRunShop() {
        val choices = ArtifactDefinition.StoneList.shuffled().take(3)
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.BROKEN_STONE,
            brokenStoneChoices = choices,
            heavyWeightCount = masteryManager.heavyWeightCount,
            isHeavyWeightMastered = masteryManager.isHeavyWeightMastered
        )
        soundManager.playReactionBoom()
    }

    private fun triggerBrokenStoneUI(stone: BrokenStoneEntity? = null) {
        triggerInRunShop()
    }

    fun purchaseBrokenStoneArtifact(artifact: ArtifactDefinition) {
        if (player.xp < 15) return
        player.xp -= 15
        equipArtifact(artifact)
        val remaining = _uiState.value.brokenStoneChoices.filter { it.id != artifact.id }
        _uiState.value = _uiState.value.copy(
            brokenStoneChoices = remaining
        )
    }

    fun closeBrokenStone() {
        brokenStones.clear()
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.PLAYING,
            brokenStoneChoices = emptyList()
        )
    }

    fun selectObeliskArtifact(artifact: ArtifactDefinition) {
        equipArtifact(artifact)
        _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
    }

    // ==========================================
    // PHASE 5: THE INKWELL CHECKPOINT HANDLERS
    // ==========================================
    private fun triggerInkwellUI() {
        val choices = (ArtifactDefinition.ObeliskList + ArtifactDefinition.StoneList).shuffled().take(3)
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.INKWELL_CHECKPOINT,
            inkwellActive = true,
            inkwellShopChoices = choices
        )
    }

    fun purchaseInkwellArtifact(artifact: ArtifactDefinition) {
        if (player.xp < 20) return
        player.xp -= 20
        equipArtifact(artifact)
        val remaining = _uiState.value.inkwellShopChoices.filter { it.id != artifact.id }
        _uiState.value = _uiState.value.copy(inkwellShopChoices = remaining)
    }

    fun restAndResumeFromInkwell() {
        inkwellStructures.clear()
        enemySpawningHalted = false
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.PLAYING,
            inkwellActive = false
        )
        // Spawn Minute 31 horde swarms immediately!
        val hpMult = 4.5f
        val speedMult = 1.05f
        for (i in 0 until 4) {
            spawnHordeSwarm(hpMult, speedMult)
        }
        soundManager.playReactionBoom()
    }

    fun bookmarkRunAndExit() {
        val st = _uiState.value
        val spellsData = st.activeSpells.joinToString("|") {
            "${it.definition.id}:${it.rank}:${it.traitModules.joinToString(",") { m -> m.id }}:${it.socketedRunes.joinToString(",") { r -> r.name }}"
        }
        val gearData = st.equippedGear.joinToString("|") { "${it.type.name}:${it.stacks}" }
        val artifactsData = st.equippedArtifacts.joinToString("|") { it.id }
        val synthesesData = completedSyntheses.joinToString("|")

        val state = BookmarkRunState(
            characterId = st.character.id,
            classId = st.playerClass.id,
            timeSurvivedSeconds = st.timeSurvivedSeconds,
            score = st.score,
            kills = st.kills,
            damageDealt = st.damageDealt,
            playerHp = player.hp,
            playerMaxHp = player.maxHp,
            playerLevel = player.level,
            playerXp = player.xp,
            playerXpNeeded = player.xpNeeded,
            activeSpellsData = spellsData,
            equippedGearData = gearData,
            equippedArtifactsData = artifactsData,
            completedSynthesesData = synthesesData,
            slot1Unlocked = synthesisManager.slot1Unlocked,
            slot2Unlocked = synthesisManager.slot2Unlocked,
            slot3Unlocked = synthesisManager.slot3Unlocked,
            heavyWeightCount = masteryManager.heavyWeightCount,
            isHeavyWeightMastered = masteryManager.isHeavyWeightMastered,
            corruptedCount = masteryManager.corruptedMediumCount,
            isCorruptedMastered = masteryManager.isCorruptedMediumMastered,
            geometryCount = masteryManager.sacredGeometryCount,
            isGeometryMastered = masteryManager.isSacredGeometryMastered
        )

        saveManager.saveBookmark(state)
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.MAIN_MENU,
            hasBookmarkRun = true
        )
    }

    fun resumeBookmarkedRun() {
        val state = saveManager.loadAndConsumeBookmark() ?: return
        val charDef = CharacterDefinition.allCharacters.find { it.id == state.characterId } ?: CharacterDefinition.TheCalligrapher
        val classDef = ClassDefinition.allClasses.find { it.id == state.classId } ?: ClassDefinition.Scribe

        // Reconstitute active spells
        val restoredSpells = mutableListOf<ActiveSpell>()
        if (state.activeSpellsData.isNotBlank()) {
            val spellParts = state.activeSpellsData.split("|")
            for (p in spellParts) {
                val tokens = p.split(":")
                if (tokens.isNotEmpty()) {
                    val sId = tokens[0]
                    val sRank = tokens.getOrNull(1)?.toIntOrNull() ?: 1
                    val sDef = SpellDefinition.allSpells.find { it.id == sId } ?: SpellDefinition.QuillDart
                    val sTraits = mutableListOf<SpellTraitModule>()
                    if (tokens.size > 2 && tokens[2].isNotBlank()) {
                        val traitIds = tokens[2].split(",")
                        for (tid in traitIds) {
                            when (tid) {
                                "serrated_nib" -> sTraits.add(SerratedNibTrait)
                                "flex_nib" -> sTraits.add(FlexNibTrait)
                                "wide_bristle" -> sTraits.add(WideBristleTrait)
                                "deep_well" -> sTraits.add(DeepWellTrait)
                                "razor_flow" -> sTraits.add(RazorFlowTrait)
                                "pressurized_ink" -> sTraits.add(PressurizedInkTrait)
                            }
                        }
                    }
                    val sRunes = mutableListOf<SpellRuneType>()
                    if (tokens.size > 3 && tokens[3].isNotBlank()) {
                        val runeNames = tokens[3].split(",")
                        for (rName in runeNames) {
                            try {
                                sRunes.add(SpellRuneType.valueOf(rName))
                            } catch (_: Exception) {}
                        }
                    }
                    restoredSpells.add(ActiveSpell(definition = sDef, rank = sRank, traitModules = sTraits, socketedRunes = sRunes))
                }
            }
        }
        if (restoredSpells.isEmpty()) {
            restoredSpells.add(ActiveSpell(classDef.starterSpell, 1))
        }

        // Reconstitute gear
        val restoredGear = mutableListOf<EquippedGear>()
        if (state.equippedGearData.isNotBlank()) {
            val gearParts = state.equippedGearData.split("|")
            for (gp in gearParts) {
                val tokens = gp.split(":")
                if (tokens.size == 2) {
                    try {
                        val gType = GearType.valueOf(tokens[0])
                        val gStacks = tokens[1].toIntOrNull() ?: 1
                        restoredGear.add(EquippedGear(gType, gStacks))
                    } catch (_: Exception) {}
                }
            }
        }

        // Reconstitute artifacts
        val restoredArtifacts = mutableListOf<ArtifactDefinition>()
        if (state.equippedArtifactsData.isNotBlank()) {
            val artIds = state.equippedArtifactsData.split("|")
            val allArtifacts = ArtifactDefinition.ObeliskList + ArtifactDefinition.StoneList
            for (aid in artIds) {
                val found = allArtifacts.find { it.id == aid }
                if (found != null) restoredArtifacts.add(found)
            }
        }

        completedSyntheses.clear()
        if (state.completedSynthesesData.isNotBlank()) {
            completedSyntheses.addAll(state.completedSynthesesData.split("|"))
        }

        synthesisManager.reset()
        if (state.slot1Unlocked) synthesisManager.onLevelReached(35)
        if (state.slot2Unlocked) synthesisManager.onLevelReached(60)
        if (state.slot3Unlocked) synthesisManager.onLevelReached(95)

        masteryManager.restore(
            heavyCount = state.heavyWeightCount,
            heavyMastered = state.isHeavyWeightMastered,
            corruptedCount = state.corruptedCount,
            corruptedMastered = state.isCorruptedMastered,
            geometryCount = state.geometryCount,
            geometryMastered = state.isGeometryMastered
        )
        masteryManager.evaluateArtifacts(restoredArtifacts)

        // Clear simulation state
        enemies.clear()
        inkProjectiles.clear()
        puddlePool.clear()
        redRunes.clear()
        obelisks.clear()
        brokenStones.clear()
        spellRuneDrops.clear()
        blankScrollDrops.clear()
        inkwellVortexes.clear()
        inkwellStructures.clear()
        theEraserBoss.reset()
        enemySpawningHalted = false
        inkwellSpawned = true
        titanSpawned = true
        midBossSpawned = true

        player.x = 0f
        player.y = 0f
        player.vx = 0f
        player.vy = 0f
        player.level = state.playerLevel
        player.xp = state.playerXp
        player.xpNeeded = state.playerXpNeeded
        player.hp = state.playerHp
        player.maxHp = state.playerMaxHp

        _uiState.value = GameUIState(
            screen = ScreenState.PLAYING,
            timeSurvivedSeconds = state.timeSurvivedSeconds,
            score = state.score,
            kills = state.kills,
            damageDealt = state.damageDealt,
            character = charDef,
            playerClass = classDef,
            activeSpells = restoredSpells,
            equippedGear = restoredGear,
            equippedArtifacts = restoredArtifacts,
            completedSyntheses = completedSyntheses.toList(),
            slot1Unlocked = synthesisManager.slot1Unlocked,
            slot2Unlocked = synthesisManager.slot2Unlocked,
            slot3Unlocked = synthesisManager.slot3Unlocked,
            heavyWeightCount = masteryManager.heavyWeightCount,
            isHeavyWeightMastered = masteryManager.isHeavyWeightMastered,
            corruptedCount = masteryManager.corruptedMediumCount,
            isCorruptedMastered = masteryManager.isCorruptedMediumMastered,
            geometryCount = masteryManager.sacredGeometryCount,
            isGeometryMastered = masteryManager.isSacredGeometryMastered,
            hasBookmarkRun = false,
            titanSpawned = true,
            midBossSpawned = true
        )

        recalculatePlayerStats()
        soundManager.playLevelUp()
    }

    private fun equipArtifact(artifact: ArtifactDefinition) {
        val currentArtifacts = _uiState.value.equippedArtifacts.toMutableList()
        currentArtifacts.add(artifact)

        val result = masteryManager.evaluateArtifacts(currentArtifacts)
        if (result.newMasteriesUnlocked.isNotEmpty()) {
            soundManager.playReactionBoom()
        }

        _uiState.value = _uiState.value.copy(
            equippedArtifacts = currentArtifacts,
            heavyWeightCount = result.heavyWeightCount,
            isHeavyWeightMastered = result.isHeavyWeightMastered,
            corruptedCount = result.corruptedMediumCount,
            isCorruptedMastered = result.isCorruptedMediumMastered,
            geometryCount = result.sacredGeometryCount,
            isGeometryMastered = result.isSacredGeometryMastered
        )

        recalculatePlayerStats()
    }

    private fun recalculatePlayerStats() {
        val character = _uiState.value.character
        val artifacts = _uiState.value.equippedArtifacts

        var totalDmgMult = (1.0f * character.damageMultiplier) + passiveBonusDmg
        var totalSpeedAdd = 0f
        var totalSpeedMult = 1.0f + passiveBonusSpeed
        var totalArmor = passiveBonusArmor
        var totalAttackSpeed = 1.0f + passiveBonusAttackSpeed
        var maxHpMult = 1.0f

        var currentPickupRadius = 110f + passiveBonusPickup
        for (g in _uiState.value.equippedGear) {
            when (g.type) {
                GearType.HEAVY_VELLUM -> totalArmor += 3f * g.stacks
                GearType.ERGONOMIC_GRIP -> totalAttackSpeed += 0.15f * g.stacks
                GearType.DENSE_SOOT -> totalDmgMult += 0.20f * g.stacks
                GearType.SCRIBES_SANDAL -> totalSpeedAdd += 30f * g.stacks
                GearType.LODESTONE_INKWELL -> currentPickupRadius += (45f * g.stacks)
                GearType.SPRING_WATER -> {
                    // P1: SPRING_WATER gear provides vitality (+15% Max HP per stack)
                    maxHpMult += 0.15f * g.stacks
                }
            }
        }
        player.pickupRadius = currentPickupRadius

        for (art in artifacts) {
            totalDmgMult += art.damageModifier
            totalSpeedMult += art.moveSpeedModifier
            totalArmor += art.armorModifier
            totalAttackSpeed += art.attackSpeedModifier
            maxHpMult += art.maxHpMultiplier
        }

        player.damageMultiplier = totalDmgMult.coerceAtLeast(0.1f)
        player.moveSpeed = ((player.baseMoveSpeed + totalSpeedAdd) * totalSpeedMult).coerceAtLeast(40f)
        player.armor = totalArmor
        player.attackSpeedMultiplier = totalAttackSpeed.coerceAtLeast(0.2f)
        player.maxHp = ((100f + passiveBonusHp) * maxHpMult).coerceAtLeast(20f)
        player.hp = player.hp.coerceAtMost(player.maxHp)
        player.projectileBounces = artifacts.sumOf { it.projectileBounces }
    }

    private fun spawnSpellRuneDrop(x: Float, y: Float, runeType: SpellRuneType) {
        spellRuneDrops.add(
            SpellRuneDrop(
                id = ++entityIdCounter,
                x = x,
                y = y,
                runeType = runeType
            )
        )
    }

    private fun triggerRuneSocketUI(runeType: SpellRuneType) {
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.RUNE_SOCKET,
            pendingSpellRune = runeType
        )
    }

    fun socketRuneIntoSpell(targetSpell: ActiveSpell, rune: SpellRuneType) {
        if (!targetSpell.socketedRunes.contains(rune)) {
            targetSpell.socketedRunes.add(rune)
        }
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.PLAYING,
            pendingSpellRune = null
        )
    }

    // Phase 4 RNG Mitigation: The Blank Scroll UI & Injection
    private fun triggerBlankScrollUI() {
        _uiState.value = _uiState.value.copy(screen = ScreenState.BLANK_SCROLL)
    }

    fun selectBlankScrollItem(target: InjectedItemTarget) {
        val choice = when (target) {
            is InjectedItemTarget.SpellTarget -> LevelUpChoice.NewSpellChoice(target.spell)
            is InjectedItemTarget.GearTarget -> {
                val currentStacks = _uiState.value.equippedGear.find { it.type == target.gear }?.stacks ?: 0
                LevelUpChoice.GearChoice(target.gear, currentStacks)
            }
        }
        guaranteedNextItem = choice
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.PLAYING,
            guaranteedNextItem = choice
        )
    }

    private fun damageEnemy(
        enemy: Enemy,
        damage: Float,
        isBleed: Boolean = false,
        element: CalligraphicElement? = null,
        isReaction: Boolean = false
    ) {
        if (enemy.isDead) return
        var finalDmg = damage
        var isCritHit = false

        if (isBleed) {
            val poisonBonus = _uiState.value.equippedArtifacts.sumOf { it.poisonDamageMultiplier.toDouble() }.toFloat()
            finalDmg *= (1.0f + poisonBonus)
        }

        // Frozen Solid vulnerability bonus: +60% shatter damage
        if (enemy.frozenSolidTimer > 0f) {
            finalDmg *= 1.60f
            isCritHit = true
            spawnElementalIceShards(enemy.x, enemy.y, count = 4)
        }

        // Elemental Priming & Reaction Triggering
        if (element != null && !isReaction && enemy.elementalReactionCooldown <= 0f) {
            when (element) {
                CalligraphicElement.FROST -> {
                    enemy.frostTimer = 4.0f
                    enemy.slowTimer = 3.0f
                    enemy.slowRatio = 0.45f
                    if (enemy.acidTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.FROZEN_INK, finalDmg)
                    } else if (enemy.flameTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.THERMAL_SHOCK, finalDmg)
                    } else if (enemy.astralTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.PERMAFROST_BLOSSOM, finalDmg)
                    }
                }
                CalligraphicElement.CINNABAR_FLAME -> {
                    enemy.flameTimer = 4.0f
                    enemy.burningDamagePerTick = (finalDmg * 0.35f).coerceAtLeast(6f)
                    if (enemy.frozenSolidTimer > 0f || enemy.frostTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.THERMAL_SHOCK, finalDmg)
                    } else if (enemy.acidTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.BURNING_CALLIGRAPHY, finalDmg)
                    } else if (enemy.astralTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.COSMIC_SUPERNOVA, finalDmg)
                    }
                }
                CalligraphicElement.CORROSIVE_ACID -> {
                    enemy.acidTimer = 4.5f
                    if (enemy.frostTimer > 0f || enemy.frozenSolidTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.FROZEN_INK, finalDmg)
                    } else if (enemy.flameTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.BURNING_CALLIGRAPHY, finalDmg)
                    }
                }
                CalligraphicElement.CELESTIAL_ASTRAL -> {
                    enemy.astralTimer = 4.0f
                    if (enemy.flameTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.COSMIC_SUPERNOVA, finalDmg)
                    } else if (enemy.frostTimer > 0f || enemy.frozenSolidTimer > 0f) {
                        triggerElementalReaction(enemy, ElementalReactionType.PERMAFROST_BLOSSOM, finalDmg)
                    }
                }
            }
        }

        enemy.hp -= finalDmg
        enemy.flashTimer = 0.12f
        _uiState.value = _uiState.value.copy(damageDealt = _uiState.value.damageDealt + finalDmg.toLong())

        // Phase 5 Corrupted Medium Mastery: Vampirism heals player on bleed/poison DoT ticks
        if (isBleed && (masteryManager.isCorruptedMediumMastered || _uiState.value.equippedArtifacts.any { it.hasVampirism })) {
            val healAmount = (finalDmg * 0.25f).coerceAtLeast(1f)
            player.hp = (player.hp + healAmount).coerceAtMost(player.maxHp)
        }

        if (!isBleed && !isReaction) {
            soundManager.playHit()
        }

        // Spawn fluid simulation ink splash particles
        spawnInkFluidSplash(
            x = enemy.x,
            y = enemy.y,
            count = if (finalDmg > 60f) 8 else 5,
            isCritOrBleed = isBleed || isCritHit,
            baseDmg = finalDmg
        )

        val dmgColor = when {
            isReaction -> Color(0xFFFF9100)
            isCritHit -> Color(0xFF00E5FF)
            isBleed -> Color(0xFFD32F2F)
            element == CalligraphicElement.FROST -> Color(0xFF00E5FF)
            element == CalligraphicElement.CINNABAR_FLAME -> Color(0xFFFF3D00)
            element == CalligraphicElement.CORROSIVE_ACID -> Color(0xFF00E676)
            element == CalligraphicElement.CELESTIAL_ASTRAL -> Color(0xFFFFD54F)
            else -> Color.Black
        }

        damageNumbers.add(
            DamageNumber(
                id = ++entityIdCounter,
                x = enemy.x + Random.nextFloat() * 16f - 8f,
                y = enemy.y - 18f,
                text = "${finalDmg.toInt()}",
                color = dmgColor,
                isCritical = isCritHit || isReaction
            )
        )
    }

    private fun triggerElementalReaction(
        primaryEnemy: Enemy,
        reactionType: ElementalReactionType,
        baseDamage: Float
    ) {
        primaryEnemy.elementalReactionCooldown = 0.75f
        activeElementalBanner = reactionType
        activeElementalBannerTimer = 2.0f
        lastElementalReactionName = reactionType.title
        soundManager.playReactionBoom()

        if (isScreenShakeEnabled) {
            screenShakeTimer = 0.45f
        }
        if (isHapticFeedbackEnabled) {
            soundManager.triggerHaptic(SoundManager.VibrationType.HEAVY)
        }
        if (isImpactFrameEnabled) {
            impactFrameTimer = 0.08f
            activeImpactReaction = reactionType
        }

        spawnHighContrastInkBrushSplash(primaryEnemy.x, primaryEnemy.y, reactionType, count = 38)

        val reactionDamage = (baseDamage * 2.4f).coerceAtLeast(70f)
        val reactionRadius = when (reactionType) {
            ElementalReactionType.COSMIC_SUPERNOVA -> 600f
            ElementalReactionType.BURNING_CALLIGRAPHY -> 520f
            ElementalReactionType.FROZEN_INK -> 440f
            ElementalReactionType.PERMAFROST_BLOSSOM -> 550f
            ElementalReactionType.THERMAL_SHOCK -> 380f
        }

        elementalReactionVisuals.add(
            ScreenElementalReactionVisual(
                id = ++entityIdCounter,
                reactionType = reactionType,
                x = primaryEnemy.x,
                y = primaryEnemy.y,
                damage = reactionDamage,
                radius = reactionRadius
            )
        )

        when (reactionType) {
            ElementalReactionType.FROZEN_INK -> {
                primaryEnemy.acidTimer = 0f
                primaryEnemy.frostTimer = 0f
                primaryEnemy.frozenSolidTimer = 3.0f

                for (other in enemies) {
                    val d = kotlin.math.hypot(other.x - primaryEnemy.x, other.y - primaryEnemy.y)
                    if (d <= reactionRadius) {
                        other.frozenSolidTimer = 2.5f
                        damageEnemy(other, reactionDamage, isReaction = true)
                    }
                }
                for (i in 0 until puddlePool.pool.size) {
                    val p = puddlePool.pool[i]
                    if (p.active && kotlin.math.hypot(p.x - primaryEnemy.x, p.y - primaryEnemy.y) <= reactionRadius) {
                        p.isFrozenInk = true
                        p.isBurningCalligraphy = false
                        p.maxLife += 2.5f
                    }
                }
                spawnElementalIceShards(primaryEnemy.x, primaryEnemy.y, count = 24)
            }
            ElementalReactionType.BURNING_CALLIGRAPHY -> {
                primaryEnemy.acidTimer = 0f
                primaryEnemy.flameTimer = 4.5f
                primaryEnemy.burningDamagePerTick = (reactionDamage * 0.40f).coerceAtLeast(14f)

                for (other in enemies) {
                    val d = kotlin.math.hypot(other.x - primaryEnemy.x, other.y - primaryEnemy.y)
                    if (d <= reactionRadius) {
                        other.flameTimer = 4.5f
                        other.burningDamagePerTick = (reactionDamage * 0.40f).coerceAtLeast(14f)
                        damageEnemy(other, reactionDamage, isReaction = true)
                    }
                }
                for (i in 0 until puddlePool.pool.size) {
                    val p = puddlePool.pool[i]
                    if (p.active && kotlin.math.hypot(p.x - primaryEnemy.x, p.y - primaryEnemy.y) <= reactionRadius) {
                        p.isBurningCalligraphy = true
                        p.isFrozenInk = false
                        p.damage *= 1.75f
                        p.maxLife += 2.0f
                    }
                }
                spawnElementalFireEmbers(primaryEnemy.x, primaryEnemy.y, count = 28)
            }
            ElementalReactionType.COSMIC_SUPERNOVA -> {
                primaryEnemy.astralTimer = 0f
                primaryEnemy.flameTimer = 0f

                for (other in enemies) {
                    val d = kotlin.math.hypot(other.x - primaryEnemy.x, other.y - primaryEnemy.y)
                    if (d <= reactionRadius && d > 10f) {
                        val angle = kotlin.math.atan2(primaryEnemy.y - other.y, primaryEnemy.x - other.x)
                        val pullForce = 85f
                        other.x += kotlin.math.cos(angle) * pullForce
                        other.y += kotlin.math.sin(angle) * pullForce
                        damageEnemy(other, reactionDamage * 1.35f, isReaction = true)
                    }
                }
                spawnElementalCosmicMotes(primaryEnemy.x, primaryEnemy.y, count = 30)
            }
            ElementalReactionType.PERMAFROST_BLOSSOM -> {
                primaryEnemy.astralTimer = 0f
                primaryEnemy.frostTimer = 0f
                primaryEnemy.frozenSolidTimer = 3.5f

                for (other in enemies) {
                    val d = kotlin.math.hypot(other.x - primaryEnemy.x, other.y - primaryEnemy.y)
                    if (d <= reactionRadius) {
                        other.slowTimer = 4.0f
                        other.slowRatio = 0.75f
                        if (d <= reactionRadius * 0.6f) {
                            other.frozenSolidTimer = 2.5f
                        }
                        damageEnemy(other, reactionDamage, isReaction = true)
                    }
                }
                spawnElementalIceShards(primaryEnemy.x, primaryEnemy.y, count = 24)
            }
            ElementalReactionType.THERMAL_SHOCK -> {
                primaryEnemy.frostTimer = 0f
                primaryEnemy.flameTimer = 0f
                primaryEnemy.frozenSolidTimer = 0f

                for (other in enemies) {
                    val d = kotlin.math.hypot(other.x - primaryEnemy.x, other.y - primaryEnemy.y)
                    if (d <= reactionRadius) {
                        val angle = kotlin.math.atan2(other.y - primaryEnemy.y, other.x - primaryEnemy.x)
                        val push = if (other.isBoss) 25f else 95f
                        other.x += kotlin.math.cos(angle) * push
                        other.y += kotlin.math.sin(angle) * push
                        damageEnemy(other, reactionDamage * 1.5f, isReaction = true)
                    }
                }
                spawnElementalFireEmbers(primaryEnemy.x, primaryEnemy.y, count = 14)
                spawnElementalIceShards(primaryEnemy.x, primaryEnemy.y, count = 14)
            }
        }
    }

    private fun spawnElementalIceShards(x: Float, y: Float, count: Int) {
        val colors = listOf(Color(0xFF00E5FF), Color(0xFFE0F7FA), Color(0xFF18FFFF), Color(0xFF001F3F))
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
            val speed = Random.nextFloat() * 260f + 60f
            elementalParticles.add(
                ElementalParticle(
                    id = ++entityIdCounter,
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    color = colors[Random.nextInt(colors.size)],
                    size = Random.nextFloat() * 12f + 6f,
                    life = Random.nextFloat() * 0.6f + 0.4f,
                    maxLife = 1.0f,
                    isShard = true,
                    angle = Random.nextFloat() * 360f,
                    rotSpeed = Random.nextFloat() * 10f - 5f
                )
            )
        }
    }

    private fun spawnElementalFireEmbers(x: Float, y: Float, count: Int) {
        val colors = listOf(Color(0xFFFF3D00), Color(0xFFFF9100), Color(0xFFFFD600), Color(0xFFD50000))
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
            val speed = Random.nextFloat() * 280f + 70f
            elementalParticles.add(
                ElementalParticle(
                    id = ++entityIdCounter,
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    color = colors[Random.nextInt(colors.size)],
                    size = Random.nextFloat() * 10f + 5f,
                    life = Random.nextFloat() * 0.7f + 0.35f,
                    maxLife = 1.05f,
                    isShard = false,
                    angle = Random.nextFloat() * 360f,
                    rotSpeed = Random.nextFloat() * 8f - 4f
                )
            )
        }
    }

    private fun spawnElementalCosmicMotes(x: Float, y: Float, count: Int) {
        val colors = listOf(Color(0xFFFFD54F), Color(0xFF7C4DFF), Color(0xFFB388FF), Color(0xFFFFEA00))
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
            val speed = Random.nextFloat() * 250f + 50f
            elementalParticles.add(
                ElementalParticle(
                    id = ++entityIdCounter,
                    x = x,
                    y = y,
                    vx = kotlin.math.cos(angle) * speed,
                    vy = kotlin.math.sin(angle) * speed,
                    color = colors[Random.nextInt(colors.size)],
                    size = Random.nextFloat() * 11f + 5f,
                    life = Random.nextFloat() * 0.8f + 0.4f,
                    maxLife = 1.2f,
                    isShard = true,
                    angle = Random.nextFloat() * 360f,
                    rotSpeed = Random.nextFloat() * 12f - 6f
                )
            )
        }
    }

    private fun damagePlayer(rawDamage: Float) {
        val actualDamage = (rawDamage - player.armor).coerceAtLeast(1f)
        player.hp -= actualDamage
        player.isInvincible = true
        player.invincibleTimer = 0.55f
        soundManager.triggerHaptic(SoundManager.VibrationType.HEAVY)

        if (player.hp <= 0f) {
            player.hp = 0f
            finishRun(isVictory = false)
        }
    }

    private fun distanceToSegment(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        val lenSq = dx * dx + dy * dy
        if (lenSq < 0.001f) return hypot(px - x1, py - y1)
        val t = (((px - x1) * dx + (py - y1) * dy) / lenSq).coerceIn(0f, 1f)
        val projX = x1 + t * dx
        val projY = y1 + t * dy
        return hypot(px - projX, py - projY)
    }

    private fun collectOrb(orb: Orb) {
        val earnedXp = (orb.value * player.xpMultiplier).toInt().coerceAtLeast(1)
        player.xp += earnedXp
        totalXpCollected += earnedXp
        soundManager.triggerHaptic(SoundManager.VibrationType.LIGHT)

        if (player.xp >= player.xpNeeded) {
            player.xp -= player.xpNeeded
            player.level++
            player.xpNeeded = calculateXpNeeded(player.level)

            // Phase 4 Milestone Tracker: Levels 25, 40, 55 (re-gated in SynthesisManager)
            val newlyUnlocked = synthesisManager.onLevelReached(player.level)
            if (newlyUnlocked) {
                soundManager.playLevelUp()
            }

            _uiState.value = _uiState.value.copy(
                slot1Unlocked = synthesisManager.slot1Unlocked,
                slot2Unlocked = synthesisManager.slot2Unlocked,
                slot3Unlocked = synthesisManager.slot3Unlocked
            )

            // Phase 4 Magnum Opus check (gated on 3 evolutions completed or level 55+)
            if ((player.level >= 55 || completedSyntheses.size >= 3) && !magnumOpusTriggered) {
                checkMagnumOpusUltimate()
            }

            // Continuous Asynchronous Synthesis check (No fallback penalty!)
            if (checkAsynchronousSynthesis()) {
                return
            }

            soundManager.playLevelUp()
            triggerLevelUp()
        }
    }

    // Phase 4 Asynchronous Synthesis Check: Evaluates open slots & valid combos via SynthesisManager
    fun checkAsynchronousSynthesis(): Boolean {
        val recipe = synthesisManager.findAvailableSynthesis(
            spells = _uiState.value.activeSpells,
            gear = _uiState.value.equippedGear,
            completedSyntheses = completedSyntheses
        )
        if (recipe != null) {
            inkProjectiles.clear()
            _uiState.value = _uiState.value.copy(
                screen = ScreenState.SPELL_SYNTHESIS,
                synthesisRecipe = recipe,
                activeSlotNumber = completedSyntheses.size + 1
            )
            return true
        }
        return false
    }

    fun confirmSynthesis(recipe: SpellSynthesisRecipe) {
        completedSyntheses.add(recipe.id)
        val currentSpells = _uiState.value.activeSpells.toMutableList()
        val spellIdx = currentSpells.indexOfFirst { it.definition.id == recipe.requiredSpellId }
        if (spellIdx >= 0) {
            val oldSpell = currentSpells[spellIdx]
            currentSpells[spellIdx] = ActiveSpell(
                definition = recipe.evolvedSpell,
                rank = 1,
                traitModules = oldSpell.traitModules,
                socketedRunes = oldSpell.socketedRunes
            )
        } else {
            currentSpells.add(ActiveSpell(definition = recipe.evolvedSpell, rank = 1))
        }

        soundManager.playReactionBoom()
        _uiState.value = _uiState.value.copy(
            activeSpells = currentSpells,
            completedSyntheses = completedSyntheses.toList(),
            screen = ScreenState.PLAYING,
            synthesisRecipe = null
        )

        if ((player.level >= 55 || completedSyntheses.size >= 3) && !magnumOpusTriggered) {
            checkMagnumOpusUltimate()
        }
    }

    fun confirmHarpoonSynthesis() {
        confirmSynthesis(SpellSynthesisRecipe.HarpoonSynthesis)
    }

    // Phase 4 Level 100 "Magnum Opus" Ultimate Validation & Sequence
    private fun checkMagnumOpusUltimate() {
        val character = _uiState.value.character
        val playerClass = _uiState.value.playerClass
        val isEligible = synthesisManager.validateMagnumOpusUltimate(
            level = player.level,
            characterId = character.id,
            classId = playerClass.id,
            completedSyntheses = completedSyntheses
        )

        if (isEligible) {
            magnumOpusTriggered = true
            soundManager.playReactionBoom()
            magnumOpusVisual.active = true
            magnumOpusVisual.timer = 0f
            magnumOpusFreezeTimer = 2.4f

            // Screen-wide obliteration of non-boss enemies
            val wipedCount = enemies.count { !it.isBoss }
            enemies.removeAll { !it.isBoss }
            _uiState.value = _uiState.value.copy(
                kills = _uiState.value.kills + wipedCount,
                score = _uiState.value.score + wipedCount * 25,
                magnumOpusActive = true
            )

            // Permanently equip The Master's Decree
            val currentSpells = _uiState.value.activeSpells.toMutableList()
            if (currentSpells.isNotEmpty()) {
                currentSpells[0] = ActiveSpell(
                    definition = SpellDefinition.TheMastersDecree,
                    rank = 1,
                    traitModules = currentSpells[0].traitModules,
                    socketedRunes = currentSpells[0].socketedRunes
                )
            } else {
                currentSpells.add(ActiveSpell(SpellDefinition.TheMastersDecree, rank = 1))
            }
            _uiState.value = _uiState.value.copy(activeSpells = currentSpells)
        }
    }

    // Regular Level Up Choices: User requested trait selection in separate new window
    private fun triggerLevelUp() {
        val choices = mutableListOf<LevelUpChoice>()
        val spells = _uiState.value.activeSpells

        for (spell in spells) {
            if (!spell.isMaxLevel) {
                val nextRank = spell.rank + 1
                val desc = if (nextRank == 3) {
                    "Awaken Trait Branch (Select in dedicated window)"
                } else {
                    "+25% Spell Damage and -5% Cooldown."
                }
                choices.add(
                    LevelUpChoice.SpellLevelChoice(
                        spellId = spell.definition.id,
                        spellName = spell.definition.name,
                        targetRank = nextRank,
                        statBonusDesc = desc
                    )
                )
            }
        }

        for (def in SpellDefinition.baseSpells) {
            if (spells.none { it.definition.id == def.id }) {
                choices.add(LevelUpChoice.NewSpellChoice(def))
            }
        }

        // Dedicated Passive Stat Upgrades
        val passiveStatPool = listOf(
            LevelUpChoice.PassiveStatChoice(
                statType = PassiveStatType.DRAGON_BLOOD_HP,
                title = "Dragon Blood Vellum",
                badge = "+HP",
                subtitle = "Hero Vitality",
                description = "+25 Maximum Health and immediately restores 25 HP.",
                bonusValue = 25f
            ),
            LevelUpChoice.PassiveStatChoice(
                statType = PassiveStatType.SWIFT_BRUSH_SPEED,
                title = "Swift Brushwork",
                badge = "+Speed",
                subtitle = "Mobility Transcendence",
                description = "+12% Movement Speed across the battlefield.",
                bonusValue = 0.12f
            ),
            LevelUpChoice.PassiveStatChoice(
                statType = PassiveStatType.CARBON_DENSITY_DMG,
                title = "Carbon Soot Density",
                badge = "+Damage",
                subtitle = "Ink Concentration",
                description = "+15% All Calligraphic Damage.",
                bonusValue = 0.15f
            ),
            LevelUpChoice.PassiveStatChoice(
                statType = PassiveStatType.EXPANDED_WELL_MAGNET,
                title = "Expanded Well",
                badge = "+Magnet",
                subtitle = "Essence Attraction",
                description = "+35 Attraction Pickup Radius for ink orbs.",
                bonusValue = 35f
            ),
            LevelUpChoice.PassiveStatChoice(
                statType = PassiveStatType.TEMPERED_NIB_ARMOR,
                title = "Tempered Steel Nib",
                badge = "+Armor",
                subtitle = "Paper Horror Ward",
                description = "+2 Flat Armor, reducing damage taken from enemy contacts.",
                bonusValue = 2f
            ),
            LevelUpChoice.PassiveStatChoice(
                statType = PassiveStatType.KEEN_BRISTLE_CRIT,
                title = "Keen Bristle Flake",
                badge = "+Atk Spd",
                subtitle = "Lethal Inscription",
                description = "+12% Spell Casting & Attack Speed.",
                bonusValue = 0.12f
            )
        )
        choices.addAll(passiveStatPool.shuffled().take(2))

        val currentGear = _uiState.value.equippedGear.associate { it.type to it.stacks }
        val randomGears = GearType.entries.shuffled().map { type ->
            val stacks = currentGear[type] ?: 0
            LevelUpChoice.GearChoice(gearType = type, currentStacks = stacks)
        }

        val finalChoices = mutableListOf<LevelUpChoice>()

        // Phase 4 Blank Scroll 100% Guaranteed Injection
        if (guaranteedNextItem != null) {
            finalChoices.add(guaranteedNextItem!!)
            guaranteedNextItem = null
            _uiState.value = _uiState.value.copy(guaranteedNextItem = null)
        }

        finalChoices.addAll(choices.shuffled().take(3 - finalChoices.size))
        if (finalChoices.size < 3) {
            for (g in randomGears) {
                if (finalChoices.size >= 3) break
                if (finalChoices.none { it is LevelUpChoice.GearChoice && it.gearType == g.gearType }) {
                    finalChoices.add(g)
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            screen = ScreenState.LEVEL_UP,
            availableChoices = finalChoices
        )
    }

    fun selectLevelUpChoice(choice: LevelUpChoice) {
        when (choice) {
            is LevelUpChoice.SpellLevelChoice -> {
                val spell = _uiState.value.activeSpells.find { it.definition.id == choice.spellId }
                if (spell != null) {
                    if (choice.targetRank in listOf(3, 5, 7)) {
                        // Trait Awakening at Ranks 3, 5, 7: Open dedicated selection window
                        pendingTraitSpellId = spell.definition.id
                        pendingTraitTargetRank = choice.targetRank
                        val traitOptions = SpellTraitType.getTraitsForSpell(spell.definition.id, choice.targetRank)
                        if (traitOptions.isNotEmpty()) {
                            _uiState.value = _uiState.value.copy(
                                screen = ScreenState.TRAIT_SELECTION,
                                pendingTraitSpellName = "${spell.definition.name} (Rank ${choice.targetRank})",
                                pendingTraitOptions = traitOptions
                            )
                            return
                        }
                    }
                    spell.rank = choice.targetRank
                }
            }

            is LevelUpChoice.TraitChoice -> {
                // Backward-compatible fallback
            }

            is LevelUpChoice.NewSpellChoice -> {
                val updatedSpells = _uiState.value.activeSpells.toMutableList()
                updatedSpells.add(ActiveSpell(definition = choice.definition, rank = 1))
                _uiState.value = _uiState.value.copy(activeSpells = updatedSpells)
            }

            is LevelUpChoice.GearChoice -> {
                applyGearStatStick(choice.gearType)
            }

            is LevelUpChoice.PassiveStatChoice -> {
                when (choice.statType) {
                    PassiveStatType.DRAGON_BLOOD_HP -> {
                        passiveBonusHp += choice.bonusValue
                        player.hp = (player.hp + choice.bonusValue).coerceAtMost(player.maxHp + choice.bonusValue)
                    }
                    PassiveStatType.SWIFT_BRUSH_SPEED -> {
                        passiveBonusSpeed += choice.bonusValue
                    }
                    PassiveStatType.CARBON_DENSITY_DMG -> {
                        passiveBonusDmg += choice.bonusValue
                    }
                    PassiveStatType.EXPANDED_WELL_MAGNET -> {
                        passiveBonusPickup += choice.bonusValue
                    }
                    PassiveStatType.TEMPERED_NIB_ARMOR -> {
                        passiveBonusArmor += choice.bonusValue
                    }
                    PassiveStatType.KEEN_BRISTLE_CRIT -> {
                        passiveBonusAttackSpeed += choice.bonusValue
                    }
                }
                recalculatePlayerStats()
            }
        }

        _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
        checkAsynchronousSynthesis()
    }

    // Trait selected in dedicated new window
    fun selectSpellTrait(traitType: SpellTraitType) {
        val targetId = pendingTraitSpellId ?: return
        val spell = _uiState.value.activeSpells.find { it.definition.id == targetId }
        if (spell != null) {
            spell.rank = maxOf(spell.rank, pendingTraitTargetRank)
            val module: SpellTraitModule = SpellTraitType.getModule(traitType)
            if (!spell.traitModules.any { it.id == module.id }) {
                spell.traitModules.add(module)
            }
        }

        pendingTraitSpellId = null
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.PLAYING,
            pendingTraitSpellName = "",
            pendingTraitOptions = emptyList()
        )

        checkAsynchronousSynthesis()
    }

    private fun applyGearStatStick(type: GearType) {
        val gearList = _uiState.value.equippedGear.toMutableList()
        val existing = gearList.find { it.type == type }
        if (existing != null) {
            existing.stacks++
        } else {
            gearList.add(EquippedGear(type, 1))
        }

        if (type == GearType.SPRING_WATER) {
            player.hp = (player.hp + 25f).coerceAtMost(player.maxHp)
        }

        _uiState.value = _uiState.value.copy(equippedGear = gearList)
        recalculatePlayerStats()
    }

    private fun finishRun(isVictory: Boolean, isMinute61Victory: Boolean = false) {
        val st = _uiState.value
        val payout = saveManager.recordRunPayout(
            kills = st.kills,
            survivalSeconds = st.timeSurvivedSeconds.toInt(),
            isMinute61Victory = isMinute61Victory
        )
        val goldEarned = payout.first
        val crystalsEarned = payout.second
        val primarySpell = st.activeSpells.firstOrNull()?.definition?.name ?: "Quill Dart"

        _uiState.value = st.copy(
            screen = ScreenState.GAME_OVER,
            goldEarnedThisRun = goldEarned,
            crystalsEarnedThisRun = crystalsEarned,
            isVictory = isVictory,
            isMinute61Victory = isMinute61Victory,
            unlockedMapTier = saveManager.loadSaveData().unlockedMapTier
        )

        val run = RunRecordEntity(
            score = st.score,
            survivalTimeSeconds = st.timeSurvivedSeconds.toInt(),
            kills = st.kills,
            damageDealt = st.damageDealt,
            primaryColor = if (isMinute61Victory) "Divine Gold" else "Stark Black",
            toolName = "${st.playerClass.name} ($primarySpell)",
            levelReached = player.level,
            isVictory = isVictory
        )

        viewModelScope.launch {
            repository.saveRun(
                run = run,
                goldEarned = goldEarned,
                crystalsEarned = crystalsEarned,
                inkStonesGained = (st.kills / 4).coerceAtLeast(1)
            )
        }
    }

    // Meta-progression Shop methods
    fun upgradeMetaStat(stat: String, cost: Int) {
        saveManager.spendGoldOnMetaStat(stat, cost)
        viewModelScope.launch {
            repository.upgradeMetaStat(stat, cost)
        }
    }

    fun unlockCharacter(characterId: String, cost: Int) {
        saveManager.spendCrystalsOnCharacter(characterId, cost)
        viewModelScope.launch {
            repository.unlockCharacterWithCrystals(characterId, cost)
        }
    }

    fun buyClassFragment(classId: String, costGold: Int = 100, costCrystals: Int = 0) {
        viewModelScope.launch {
            val success = repository.buyClassFragment(classId, costGold, costCrystals)
            if (success) {
                soundManager.playLevelUp()
                val progress = repository.getProgressOnce()
                if (_uiState.value.playerClass.id == classId) {
                    _uiState.value = _uiState.value.copy(
                        playerClass = _uiState.value.playerClass.copy(
                            masteryLevel = progress.getClassLevel(classId)
                        )
                    )
                }
            }
        }
    }
}
