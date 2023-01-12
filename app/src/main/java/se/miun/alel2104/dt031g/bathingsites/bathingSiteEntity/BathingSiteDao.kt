package se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BathingSiteDao {
    @Query("SELECT * FROM BathingSite")
    fun getAllBathingSites(): List<BathingSite>

    @Query("SELECT COUNT(*) FROM BathingSite")
    fun getNumberOfBathingSites(): Int

    @Query("SELECT EXISTS (SELECT 1 FROM BathingSite WHERE latitude = :latitude AND longitude = :longitude)")
    fun exists(latitude: Double, longitude: Double): Boolean

    @Insert
    fun insertAll(vararg BathingSite: BathingSite)

    @Query("DELETE FROM BathingSite")
    fun removeAllTables()
}