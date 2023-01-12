package se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * The Dao interface containing the database queries.
 * @author Albin Eliasson
 */
@Dao
interface BathingSiteDao {
    /**
     * Function to access all bathing-sites from the database.
     */
    @Query("SELECT * FROM BathingSite")
    fun getAllBathingSites(): List<BathingSite>

    /**
     * Function to access the number of saved bathing-sites.
     */
    @Query("SELECT COUNT(*) FROM BathingSite")
    fun getNumberOfBathingSites(): Int

    /**
     * Function to check if a certain bathing-site already exists in the database with the same
     * longitude and latitude.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM BathingSite WHERE latitude = :latitude AND longitude = :longitude)")
    fun exists(latitude: Double, longitude: Double): Boolean

    /**
     * Function to insert bathing-sites to the database.
     */
    @Insert
    fun insertAll(vararg BathingSite: BathingSite)

    /**
     * Function to delete all data from the database.
     */
    @Query("DELETE FROM BathingSite")
    fun removeAllTables()
}