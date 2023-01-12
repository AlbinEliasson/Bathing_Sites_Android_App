package se.miun.alel2104.dt031g.bathingsites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSite
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSiteDao

/**
 * Abstract singleton class which builds the database utilizing room.
 * @author Albin Eliasson
 */
@Database(entities = [BathingSite::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    /**
     * Abstract class to access the Dao BathingSiteDao interface.
     */
    abstract fun BathingSiteDao(): BathingSiteDao

    companion object {
        private var INSTANCE: AppDataBase? = null

        /**
         * Access/builds the database if not already built.
         */
        fun getDatabase(context: Context): AppDataBase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, AppDataBase::class.java, context.getString(
                            R.string.name_of_the_database_file))
                            .build()
                }
            }
            return INSTANCE!!
        }
    }

    /**
     * Function to release the database.
     */
    fun destroy() {
        INSTANCE = null
    }
}
