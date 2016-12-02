/**
 * Pokemon Go Bot  Copyright (C) 2016  PokemonGoBot-authors (see authors.md for more information)
 * This program comes with ABSOLUTELY NO WARRANTY;
 * This is free software, and you are welcome to redistribute it under certain conditions.
 *
 * For more information, refer to the LICENSE file in this repositories root directory
 */

package hydra.bot.pogo.qk9pBEF

import com.google.common.util.concurrent.AtomicDouble
import com.google.maps.GeoApiContext
import com.pokegoapi.api.PokemonGo
import com.pokegoapi.api.player.PlayerProfile
import hydra.bot.pogo.qk9pBEF.gui.SocketServer
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

data class Context(
        val api: PokemonGo,
        val profile: PlayerProfile,
        val lat: AtomicDouble,
        val lng: AtomicDouble,

        val startXp: AtomicLong,
        val startTime: LocalDateTime,
        val pokemonStats: Pair<AtomicInteger, AtomicInteger>,
        val luredPokemonStats: AtomicInteger,
        val pokestops: AtomicInteger,
        val itemStats: Pair<AtomicInteger, AtomicInteger>,
        var walkingSpeed: AtomicDouble,

        val blacklistedEncounters: MutableSet<Long>,
        val server: SocketServer,

        val pokemonInventoryFullStatus: AtomicBoolean = AtomicBoolean(false),

        var restApiPassword: String,
        var s2Cache: MutableMap<String, Double>,
        var restApiToken: String = "",

        val walking: AtomicBoolean = AtomicBoolean(false),

        val pauseWalking: AtomicBoolean = AtomicBoolean(false),

        val geoApiContext: GeoApiContext?
)