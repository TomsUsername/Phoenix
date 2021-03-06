/**
 * Pokemon Go Bot  Copyright (C) 2016  PokemonGoBot-authors (see authors.md for more information)
 * This program comes with ABSOLUTELY NO WARRANTY;
 * This is free software, and you are welcome to redistribute it under certain conditions.
 *
 * For more information, refer to the LICENSE file in this repositories root directory
 */

package phoenix.bot.pogo.nRnMK9r.tasks

import POGOProtos.Networking.Responses.ReleasePokemonResponseOuterClass.ReleasePokemonResponse.Result
import phoenix.bot.pogo.api.util.PokemonMetaRegistry
import phoenix.bot.pogo.nRnMK9r.Bot
import phoenix.bot.pogo.nRnMK9r.Context
import phoenix.bot.pogo.nRnMK9r.Settings
import phoenix.bot.pogo.nRnMK9r.Task
import phoenix.bot.pogo.nRnMK9r.util.Log
import phoenix.bot.pogo.nRnMK9r.util.pokemon.getIv
import phoenix.bot.pogo.nRnMK9r.util.pokemon.getIvPercentage
import phoenix.bot.pogo.nRnMK9r.util.pokemon.shouldTransfer
import java.util.concurrent.atomic.AtomicInteger

class ReleasePokemon : Task {
    override fun run(bot: Bot, ctx: Context, settings: Settings) {
        val pokemonMap = ctx.api.inventory.pokemon
        // prevent concurrent modification exception
        val groupedPokemon = pokemonMap.map { it.value }.groupBy { it.pokemonData.pokemonId }
        val sortByIV = settings.sortByIv
        val pokemonCounts = hashMapOf<String, Int>()

        groupedPokemon.forEach {
            val sorted = if (sortByIV) {
                it.value.sortedByDescending { it.pokemonData.getIv() }
            } else {
                it.value.sortedByDescending { it.pokemonData.cp }
            }
            for ((index, pokemon) in sorted.withIndex()) {
                // don't drop favorited, deployed, or nicknamed pokemon
                val isFavourite = pokemon.pokemonData.nickname.isNotBlank() ||
                        pokemon.pokemonData.favorite != 0 ||
                        !pokemon.pokemonData.deployedFortId.isEmpty() ||
                        (ctx.api.playerData.hasBuddyPokemon() && ctx.api.playerData.buddyPokemon.id == pokemon.pokemonData.id)
                if (!isFavourite) {
                    val ivPercentage = pokemon.pokemonData.getIvPercentage()
                    // never transfer highest rated Pokemon (except for obligatory transfer)
                    if (settings.obligatoryTransfer.contains(pokemon.pokemonData.pokemonId) || index >= settings.keepPokemonAmount) {
                        val (shouldRelease, reason) = pokemon.pokemonData.shouldTransfer(settings, pokemonCounts,
                                bot.api.inventory.candies.getOrPut(PokemonMetaRegistry.getMeta(pokemon.pokemonData.pokemonId).family, { AtomicInteger(0) }))

                        if (shouldRelease) {
                            Log.yellow("Were Sending ${pokemon.pokemonData.pokemonId.name} to the professor with " +
                                    "CP ${pokemon.pokemonData.cp} and IV $ivPercentage%; reason: $reason")
                            val result = bot.api.queueRequest(phoenix.bot.pogo.api.request.ReleasePokemon().withPokemonId(pokemon.pokemonData.id)).toBlocking().first().response

                            if (result.result == Result.SUCCESS) {
                                Log.green("Successfully transfered ${pokemon.pokemonData.pokemonId.name} with " +
                                        "CP ${pokemon.pokemonData.cp} and IV $ivPercentage%")
                                if (ctx.pokemonInventoryFullStatus.get()) {
                                    // Just released a pokemon so the inventory is not full anymore
                                    ctx.pokemonInventoryFullStatus.set(false)
                                    if (settings.catchPokemon)
                                        Log.green("Inventory freed, enabling catching of pokemon")
                                }
                                ctx.pokemonStats.second.andIncrement
                                ctx.server.releasePokemon(pokemon.pokemonData.id)
                                ctx.server.sendProfile()
                            } else {
                                Log.red("Failed to transfer ${pokemon.pokemonData.pokemonId.name}: ${result.result}")
                            }

                        }
                    }
                }
            }
        }
    }
}
