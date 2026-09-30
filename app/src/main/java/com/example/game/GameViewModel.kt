package com.example.game

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.PlayerProgressEntity
import com.example.data.RunRecordEntity
import com.example.data.SaveManager
import com.example.model.ActiveSpell
import com.example.model.ArtifactDefinition
import com.example.model.BlankScrollDrop
import com.example.model.BrokenStoneEntity
import com.example.model.CharacterDefinition
import com.example.model.ClassDefinition
import com.example.model.DamageNumber
import com.example.model.DeepWellTrait
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.EquippedGear
import com.example.model.FlexNibTrait
import com.example.model.GearType
import com.example.model.InkPuddle
import com.example.model.InkPuddlePool
import com.example.model.InkProjectile
import com.example.model.InkwellVortexEntity
import com.example.model.LevelUpChoice
import com.example.model.MagnumOpusVisual
import com.example.model.ObeliskEntity
import com.example.model.Orb
import com.example.model.PressurizedInkTrait
import com.example.model.RazorFlowTrait
import com.example.model.RedRuneEntity
import com.example.model.SerratedNibTrait
import com.example.model.SoundManager
import com.example.model.SpellCastType
import com.example.model.SpellDefinition
import com.example.model.SpellRuneDrop
import com.example.model.SpellRuneType
import com.example.model.SpellSynthesisRecipe
import com.example.model.SpellTraitModule
import com.example.model.SpellTraitType
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
    GAME_OVER
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
    var xp: Int = 0,
    var xpNeeded: Int = 8,
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
    val titanSpawned: Boolean = false,
    val midBossSpawned: Boolean = false,
    val pendingTraitSpellName: String = "",
    val pendingTraitOptions: List<SpellTraitType> = emptyList(),
    val guaranteedNextItem: LevelUpChoice? = null,
    val magnumOpusActive: Boolean = false,
    val goldEarnedThisRun: Int = 0,
    val crystalsEarnedThisRun: Int = 0,
    val isVictory: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = GameRepository(database.runDao(), database.playerProgressDao())
    val soundManager = SoundManager(application)

    val playerProgress: StateFlow<PlayerProgressEntity> = repository.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerProgressEntity())

    val runHistory: StateFlow<List<RunRecordEntity>> = repository.allRuns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    // Simulation entities
    val player = PlayerState()
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

    // Entity counters & Timers
    private var entityIdCounter: Long = 0
    private var enemySpawnTimer: Float = 0f

    // Red Rune & Elite tracking
    private var nextRedRuneTime: Float = 150f
    private var nextElitePackId: Long = 0L
    private val elitePackCounts = mutableMapOf<Long, Int>()

    // Broken Stone spawn timer
    private var nextBrokenStoneTime: Float = 90f

    // Pacing Bosses
    private var titanSpawned: Boolean = false
    private var midBossSpawned: Boolean = false

    // Phase 4 Systems: SynthesisManager & SaveManager
    val synthesisManager = SynthesisManager()
    val saveManager = SaveManager(application)

    val slot1Unlocked: Boolean get() = synthesisManager.slot1Unlocked
    val slot2Unlocked: Boolean get() = synthesisManager.slot2Unlocked
    val slot3Unlocked: Boolean get() = synthesisManager.slot3Unlocked
    val completedSyntheses: MutableSet<String> = mutableSetOf()

    // Trait Awakening sub-window state
    private var pendingTraitSpellId: String? = null

    // RNG Mitigation: The Blank Scroll injection
    private var guaranteedNextItem: LevelUpChoice? = null

    // Level 100 Magnum Opus state
    private var magnumOpusTriggered: Boolean = false
    private var magnumOpusFreezeTimer: Float = 0f

    // Archetype Mastery System
    private var heavyWeightMasteryAchieved: Boolean = false

    // Joystick input vector
    var joystickVector: Offset = Offset.Zero

    fun setScreen(screen: ScreenState) {
        _uiState.value = _uiState.value.copy(screen = screen)
    }

    fun selectClass(classDef: ClassDefinition) {
        _uiState.value = _uiState.value.copy(playerClass = classDef)
    }

    fun selectCharacter(charDef: CharacterDefinition) {
        _uiState.value = _uiState.value.copy(character = charDef)
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
        player.xp = 0
        player.xpNeeded = 8
        player.level = 1
        player.isInvincible = false
        player.invincibleTimer = 0f

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
        magnumOpusVisual.active = false
        magnumOpusVisual.timer = 0f
        washBrushVisuals.clear()
        orbs.clear()
        damageNumbers.clear()
        elitePackCounts.clear()

        entityIdCounter = 0
        enemySpawnTimer = 0f
        nextRedRuneTime = 150f
        nextBrokenStoneTime = 90f
        titanSpawned = false
        midBossSpawned = false

        synthesisManager.reset()
        completedSyntheses.clear()
        pendingTraitSpellId = null
        guaranteedNextItem = null
        magnumOpusTriggered = false
        magnumOpusFreezeTimer = 0f
        heavyWeightMasteryAchieved = false
        nextElitePackId = 0L
        joystickVector = Offset.Zero

        val starterSpell = ActiveSpell(definition = selectedClass.starterSpell, rank = 1)
        val initialSpells = mutableListOf(starterSpell)
        if (selectedClass.id == "scribe") {
            initialSpells.add(ActiveSpell(definition = SpellDefinition.WashBrush, rank = 1))
            initialSpells.add(ActiveSpell(definition = SpellDefinition.SteelFountain, rank = 1))
        }

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
            titanSpawned = false,
            midBossSpawned = false,
            isVictory = false
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

        // Check 60-Minute Victory
        if (newTime >= 3600f && !currentState.isVictory) {
            finishRun(isVictory = true)
            return
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

        // Invincibility cooldown
        if (player.isInvincible) {
            player.invincibleTimer -= clampedDt
            if (player.invincibleTimer <= 0f) {
                player.isInvincible = false
            }
        }

        // 2. Red Rune Spawner & Collision (Every 2:30)
        if (newTime >= nextRedRuneTime && redRunes.isEmpty()) {
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

        // 4. Broken Stone Spawner & Collision
        if (newTime >= nextBrokenStoneTime && brokenStones.isEmpty()) {
            spawnBrokenStoneNearPlayer()
            nextBrokenStoneTime += 150f
        }

        val brokenStoneIterator = brokenStones.iterator()
        while (brokenStoneIterator.hasNext()) {
            val stone = brokenStoneIterator.next()
            stone.pulseTimer += clampedDt
            if (hypot(player.x - stone.x, player.y - stone.y) < stone.radius + 20f) {
                triggerBrokenStoneUI(stone)
                break
            }
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

        // 7. Spell Auto-Casting on Cooldown
        for (spell in currentState.activeSpells) {
            spell.cooldownTimer -= clampedDt
            if (spell.cooldownTimer <= 0f) {
                spell.cooldownTimer = spell.getEffectiveCooldown(player.attackSpeedMultiplier)
                castSpell(spell, currentState.character)
            }
        }

        // 8. Update Ink Projectiles
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
                    damageEnemy(enemy, proj.damage)

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

                    proj.pierceCount--
                    if (proj.pierceCount <= 0) break
                }
            }

            if (proj.life <= 0f || proj.pierceCount <= 0) {
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
                        damageEnemy(enemy, puddle.damage)
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

        // 10. Update Inkwell Vortexes (Phase 4 Gravitational Suction)
        val vortexIterator = inkwellVortexes.iterator()
        while (vortexIterator.hasNext()) {
            val vortex = vortexIterator.next()
            vortex.life -= clampedDt
            vortex.angle += 3.5f * clampedDt
            vortex.tickTimer += clampedDt

            // Suction pull & tick damage
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

        // 12. 60-Minute Map Scaling (Density over HP)
        // Minute 0 to 30: Linear HP scaling. Minute 30+: Hard cap HP!
        val hpMultiplier = if (newTime <= 1800f) {
            1.0f + (newTime / 1800f) * 3.5f
        } else {
            4.5f
        }

        // Minute 31 to 60: Exponential speed and spawn density
        val speedMultiplier = if (newTime <= 1800f) {
            1.0f
        } else {
            1.0f + ((newTime - 1800f) / 1800f) * 1.5f
        }

        val spawnInterval = if (newTime <= 1800f) {
            (1.1f - (newTime / 1800f) * 0.75f).coerceAtLeast(0.35f)
        } else {
            (0.35f - ((newTime - 1800f) / 1800f) * 0.27f).coerceAtLeast(0.08f)
        }

        val maxEnemies = if (newTime > 1800f) 150 else 85
        enemySpawnTimer += clampedDt
        if (enemySpawnTimer >= spawnInterval && enemies.size < maxEnemies) {
            enemySpawnTimer = 0f
            if (newTime > 1800f && Random.nextFloat() < 0.65f) {
                spawnHordeSwarm(hpMultiplier, speedMultiplier)
            } else {
                spawnEnemyOutsideViewport(hpMultiplier, speedMultiplier)
            }
        }

        // 13. Update Enemies & AI with Collision & Separation
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()

            if (enemy.flashTimer > 0f) enemy.flashTimer -= clampedDt
            if (enemy.attackCooldown > 0f) enemy.attackCooldown -= clampedDt

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

            // Move toward player (freeze during Magnum Opus)
            if (magnumOpusFreezeTimer <= 0f) {
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

                // Enemy-to-enemy soft-body separation
                for (other in enemies) {
                    if (other.id == enemy.id) continue
                    val sepDx = enemy.x - other.x
                    val sepDy = enemy.y - other.y
                    val sepDist = hypot(sepDx, sepDy)
                    val minDist = enemy.type.radius + other.type.radius
                    if (sepDist < minDist && sepDist > 0.05f) {
                        val overlap = (minDist - sepDist) * 0.5f
                        val sepNx = sepDx / sepDist
                        val sepNy = sepDy / sepDist
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

                    if (!player.isInvincible && enemy.attackCooldown <= 0f) {
                        enemy.attackCooldown = 0.75f
                        val dmg = enemy.type.damage * (if (enemy.isElite) 1.5f else if (enemy.isBoss) 2.2f else 1.0f)
                        damagePlayer(dmg)
                    }
                }
            }

            // Death handling
            if (enemy.isDead) {
                if (enemy.type == EnemyType.THE_TITAN) {
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
                        spawnSpellRuneDrop(enemy.x, enemy.y, SpellRuneType.VISCOUS_RUNE)
                        spawnObelisk(enemy.x + 20f, enemy.y + 20f)
                        soundManager.playReactionBoom()
                    }
                } else {
                    orbs.add(
                        Orb(
                            id = ++entityIdCounter,
                            x = enemy.x,
                            y = enemy.y,
                            value = enemy.type.xpValue
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

        _uiState.value = _uiState.value.copy(
            timeSurvivedSeconds = newTime,
            redRuneActive = redRunes.isNotEmpty()
        )
    }

    private fun castSpell(activeSpell: ActiveSpell, character: CharacterDefinition) {
        val facingDir = player.lastMoveDirection
        val damage = activeSpell.getEffectiveDamage(player.damageMultiplier)
        val hasViscous = activeSpell.hasViscousRune()

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
                    // Magnum Opus: 8-directional cosmic calligraphy strokes!
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
                                hasViscousRune = hasViscous
                            )
                        )
                    }
                } else {
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
                            isFlexNib = isFlexNib,
                            isSerratedNib = isSerratedNib,
                            isHarpoon = isHarpoon,
                            sourceSpellId = activeSpell.definition.id,
                            hasViscousRune = hasViscous
                        )
                    )
                }
            }

            SpellCastType.OMNIDIRECTIONAL_BARRAGE -> {
                // Fountain Barrage: 12-needle radial spray
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
                            hasViscousRune = hasViscous
                        )
                    )
                }
            }

            SpellCastType.PHYSICS_OVERLAP_ARC -> {
                soundManager.playSplatter()

                if (activeSpell.definition.isVortex) {
                    // Inkwell Vortex spawn
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
                    val arcRadius = if (isWideBristle) activeSpell.definition.arcRadius * 1.25f else activeSpell.definition.arcRadius
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
                            val angleToEnemy = atan2(dy, dx)
                            var angleDiff = abs(angleToEnemy - facingDir)
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

                    val puddleX = player.x + cos(facingDir) * (arcRadius * 0.55f)
                    val puddleY = player.y + sin(facingDir) * (arcRadius * 0.55f)
                    val isDeepWell = activeSpell.hasTrait(SpellTraitType.DEEP_WELL.id)

                    val puddle = puddlePool.obtain(
                        x = puddleX,
                        y = puddleY,
                        radius = if (isWideBristle) 80f else 60f,
                        damage = damage * 0.35f,
                        maxLife = if (isDeepWell) 6.0f else 4.0f,
                        tickInterval = if (isDeepWell) 0.35f else 0.5f,
                        sourceSpellId = activeSpell.definition.id,
                        hasViscousRune = hasViscous
                    )

                    for (trait in activeSpell.traitModules) {
                        trait.onPuddleSpawn(puddle)
                    }
                }
            }
        }
    }

    private fun spawnEnemyOutsideViewport(hpMult: Float = 1.0f, speedMult: Float = 1.0f) {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val spawnDistance = 460f
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
            val dist = 480f + Random.nextFloat() * 40f
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

    private fun triggerBrokenStoneUI(stone: BrokenStoneEntity) {
        val choices = ArtifactDefinition.StoneList.shuffled().take(3)
        val heavyWeightCount = _uiState.value.equippedArtifacts.count { it.archetypeTag == ArtifactDefinition.ARCHETYPE_HEAVY_WEIGHT }
        _uiState.value = _uiState.value.copy(
            screen = ScreenState.BROKEN_STONE,
            brokenStoneChoices = choices,
            heavyWeightCount = heavyWeightCount,
            isHeavyWeightMastered = heavyWeightMasteryAchieved
        )
    }

    fun purchaseBrokenStoneArtifact(artifact: ArtifactDefinition) {
        if (player.xp < 15) return
        player.xp -= 15
        equipArtifact(artifact)
        _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
    }

    fun closeBrokenStone() {
        _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
    }

    fun selectObeliskArtifact(artifact: ArtifactDefinition) {
        equipArtifact(artifact)
        _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
    }

    private fun equipArtifact(artifact: ArtifactDefinition) {
        val currentArtifacts = _uiState.value.equippedArtifacts.toMutableList()
        currentArtifacts.add(artifact)

        val heavyWeightCount = currentArtifacts.count { it.archetypeTag == ArtifactDefinition.ARCHETYPE_HEAVY_WEIGHT }
        if (heavyWeightCount >= 4 && !heavyWeightMasteryAchieved) {
            heavyWeightMasteryAchieved = true
            soundManager.playReactionBoom()

            for (i in 0 until currentArtifacts.size) {
                val art = currentArtifacts[i]
                if (art.id == ArtifactDefinition.TheIronVats.id) {
                    currentArtifacts[i] = art.copy(
                        moveSpeedModifier = 0.60f,
                        isMasteryCured = true
                    )
                }
            }
        }

        _uiState.value = _uiState.value.copy(
            equippedArtifacts = currentArtifacts,
            heavyWeightCount = heavyWeightCount,
            isHeavyWeightMastered = heavyWeightMasteryAchieved
        )

        recalculatePlayerStats()
    }

    private fun recalculatePlayerStats() {
        val character = _uiState.value.character
        val artifacts = _uiState.value.equippedArtifacts

        var totalDmgMult = 1.0f * character.damageMultiplier
        var totalSpeedAdd = 0f
        var totalSpeedMult = 1.0f
        var totalArmor = 0f
        var totalAttackSpeed = 1.0f

        for (g in _uiState.value.equippedGear) {
            when (g.type) {
                GearType.HEAVY_VELLUM -> totalArmor += 3f * g.stacks
                GearType.ERGONOMIC_GRIP -> totalAttackSpeed += 0.15f * g.stacks
                GearType.DENSE_SOOT -> totalDmgMult += 0.20f * g.stacks
                GearType.SCRIBES_SANDAL -> totalSpeedAdd += 30f * g.stacks
                GearType.LODESTONE_INKWELL -> player.pickupRadius = 110f + (45f * g.stacks)
                GearType.SPRING_WATER -> {}
            }
        }

        for (art in artifacts) {
            totalDmgMult += art.damageModifier
            totalSpeedMult += art.moveSpeedModifier
            totalArmor += art.armorModifier
            totalAttackSpeed += art.attackSpeedModifier
        }

        player.damageMultiplier = totalDmgMult.coerceAtLeast(0.1f)
        player.moveSpeed = ((player.baseMoveSpeed + totalSpeedAdd) * totalSpeedMult).coerceAtLeast(40f)
        player.armor = totalArmor
        player.attackSpeedMultiplier = totalAttackSpeed.coerceAtLeast(0.2f)
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

    private fun damageEnemy(enemy: Enemy, damage: Float, isBleed: Boolean = false) {
        if (enemy.isDead) return
        enemy.hp -= damage
        enemy.flashTimer = 0.12f
        _uiState.value = _uiState.value.copy(damageDealt = _uiState.value.damageDealt + damage.toLong())

        if (!isBleed) {
            soundManager.playHit()
        }

        damageNumbers.add(
            DamageNumber(
                id = ++entityIdCounter,
                x = enemy.x + Random.nextFloat() * 16f - 8f,
                y = enemy.y - 18f,
                text = "${damage.toInt()}",
                color = if (isBleed) Color(0xFFD32F2F) else Color.Black
            )
        )
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

    private fun collectOrb(orb: Orb) {
        player.xp += orb.value
        soundManager.triggerHaptic(SoundManager.VibrationType.LIGHT)

        if (player.xp >= player.xpNeeded) {
            player.xp -= player.xpNeeded
            player.level++
            player.xpNeeded = (player.xpNeeded * 1.35f).toInt() + 2

            // Phase 4 Milestone Tracker: Levels 35, 60, 95
            val newlyUnlocked = synthesisManager.onLevelReached(player.level)
            if (newlyUnlocked) {
                soundManager.playLevelUp()
            }

            _uiState.value = _uiState.value.copy(
                slot1Unlocked = synthesisManager.slot1Unlocked,
                slot2Unlocked = synthesisManager.slot2Unlocked,
                slot3Unlocked = synthesisManager.slot3Unlocked
            )

            // Phase 4 Level 100 Magnum Opus check
            if (player.level >= 100 && !magnumOpusTriggered) {
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

        if (player.level >= 100 && !magnumOpusTriggered) {
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
                    if (choice.targetRank == 3) {
                        // Level 3 Trait Awakening: Open dedicated new window
                        pendingTraitSpellId = spell.definition.id
                        val traitOptions = when (spell.definition.id) {
                            "quill_dart" -> listOf(SpellTraitType.SERRATED_NIB, SpellTraitType.FLEX_NIB)
                            "wash_brush" -> listOf(SpellTraitType.WIDE_BRISTLE, SpellTraitType.DEEP_WELL)
                            "steel_fountain" -> listOf(SpellTraitType.RAZOR_FLOW, SpellTraitType.PRESSURIZED_INK)
                            else -> listOf(SpellTraitType.SERRATED_NIB, SpellTraitType.FLEX_NIB)
                        }

                        _uiState.value = _uiState.value.copy(
                            screen = ScreenState.TRAIT_SELECTION,
                            pendingTraitSpellName = spell.definition.name,
                            pendingTraitOptions = traitOptions
                        )
                        return
                    } else {
                        spell.rank = choice.targetRank
                    }
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
        }

        _uiState.value = _uiState.value.copy(screen = ScreenState.PLAYING)
        checkAsynchronousSynthesis()
    }

    // Trait selected in dedicated new window
    fun selectSpellTrait(traitType: SpellTraitType) {
        val targetId = pendingTraitSpellId ?: return
        val spell = _uiState.value.activeSpells.find { it.definition.id == targetId }
        if (spell != null) {
            spell.rank = 3
            val module: SpellTraitModule = when (traitType) {
                SpellTraitType.SERRATED_NIB -> SerratedNibTrait
                SpellTraitType.FLEX_NIB -> FlexNibTrait
                SpellTraitType.WIDE_BRISTLE -> WideBristleTrait
                SpellTraitType.DEEP_WELL -> DeepWellTrait
                SpellTraitType.RAZOR_FLOW -> RazorFlowTrait
                SpellTraitType.PRESSURIZED_INK -> PressurizedInkTrait
            }
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

        _uiState.value = _uiState.value.copy(equippedGear = gearList)
        recalculatePlayerStats()
    }

    private fun finishRun(isVictory: Boolean) {
        val st = _uiState.value
        val payout = saveManager.recordRunPayout(
            kills = st.kills,
            survivalSeconds = st.timeSurvivedSeconds.toInt()
        )
        val goldEarned = payout.first
        val crystalsEarned = payout.second
        val primarySpell = st.activeSpells.firstOrNull()?.definition?.name ?: "Quill Dart"

        _uiState.value = st.copy(
            screen = ScreenState.GAME_OVER,
            goldEarnedThisRun = goldEarned,
            crystalsEarnedThisRun = crystalsEarned,
            isVictory = isVictory
        )

        val run = RunRecordEntity(
            score = st.score,
            survivalTimeSeconds = st.timeSurvivedSeconds.toInt(),
            kills = st.kills,
            damageDealt = st.damageDealt,
            primaryColor = "Stark Black",
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
}
