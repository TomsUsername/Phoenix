/**
 * Pokemon Go Bot  Copyright (C) 2016  PokemonGoBot-authors (see authors.md for more information)
 * This program comes with ABSOLUTELY NO WARRANTY;
 * This is free software, and you are welcome to redistribute it under certain conditions.
 *
 * For more information, refer to the LICENSE file in this repositories root directory
 */

package phoenix.bot.pogo.nRnMK9r.tasks

import phoenix.bot.pogo.nRnMK9r.Bot
import phoenix.bot.pogo.nRnMK9r.Context
import phoenix.bot.pogo.nRnMK9r.Settings
import phoenix.bot.pogo.nRnMK9r.Task
import phoenix.bot.pogo.nRnMK9r.util.Log
import phoenix.bot.pogo.nRnMK9r.util.directions.getAltitude

class GetMapRandomDirection : Task {
    override fun run(bot: Bot, ctx: Context, settings: Settings) {
        // query a small area to keep alive
        val lat = ctx.lat.get() + randomLatLng()
        val lng = ctx.lng.get() + randomLatLng()

        if (settings.displayKeepalive) Log.normal("Getting map of ($lat, $lng)")
        ctx.api.setLocation(lat, lng, getAltitude(lat, lng, ctx))
    }

    fun randomLatLng(): Double {
        return Math.random() * 0.0001 - 0.00005
    }
}
