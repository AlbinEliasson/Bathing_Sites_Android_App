package se.miun.alel2104.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BathingSiteDao {
    @Query("SELECT * FROM BathingSite")
    fun getAllBathingSites(): List<BathingSite>

    @Query("SELECT EXISTS (SELECT 1 FROM BathingSite WHERE latitude = :latitude AND longitude = :longitude)")
    fun exists(latitude: Double, longitude: Double): Boolean

    @Insert
    fun insertAll(vararg BathingSite: BathingSite)
}