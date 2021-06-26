package com.raidplan.data

import android.os.Parcel
import android.os.Parcelable
import com.raidplan.data.Dungeon
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Character() : RealmObject(), Parcelable {

    @PrimaryKey
    var id: String? = null
    var name: String? = null
    var guild: String? = null
    var server: String? = null
    var playerClass: String? = null
    var level: Int = 0
    var guildRank: Int = 20
    var itemLevel: Int = 0
    var itemLevelMax: Int = 0
    var covenant: String? = null
    var renown: Int = 0
    var arena2s: Int = 0
    var arena3s: Int = 0
    var activeSpec: String? = null
    var role: String? = null
    var roster: Boolean = false
    var rawUrl: String? = null

    var dungeonList: RealmList<Dungeon> = RealmList()

    open fun getRepTier(tier: Int): String {
        return when (tier) {
            0 -> "Hated"
            1 -> "Hostile"
            2 -> "Unfriendly"
            3 -> "Neutral"
            4 -> "Friendly"
            5 -> "Honored"
            6 -> "Revered"
            7 -> "Exalted"
            else -> ""
        }
    }

    open fun getRepPercent(tier: Int, current: Int): Int {
        return when (tier) {
            0 -> ((current.toDouble() / 36000.0) * 100).toInt()
            1 -> ((current.toDouble() / 3000.0) * 100).toInt()
            2 -> ((current.toDouble() / 3000.0) * 100).toInt()
            3 -> ((current.toDouble() / 3000.0) * 100).toInt()
            4 -> ((current.toDouble() / 6000.0) * 100).toInt()
            5 -> ((current.toDouble() / 12000.0) * 100).toInt()
            6 -> ((current.toDouble() / 21000.0) * 100).toInt()
            7 -> 100
            else -> 0
        }
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        guild = parcel.readString()
        server = parcel.readString()
        playerClass = parcel.readString()
        level = parcel.readInt()
        guildRank = parcel.readInt()
        itemLevel = parcel.readInt()
        itemLevelMax = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(guild)
        parcel.writeString(server)
        parcel.writeString(playerClass)
        parcel.writeInt(level)
        parcel.writeInt(guildRank)
        parcel.writeInt(itemLevel)
        parcel.writeInt(itemLevelMax)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Character> {
        override fun createFromParcel(parcel: Parcel): Character {
            return Character(parcel)
        }

        override fun newArray(size: Int): Array<Character?> {
            return arrayOfNulls(size)
        }
    }
}