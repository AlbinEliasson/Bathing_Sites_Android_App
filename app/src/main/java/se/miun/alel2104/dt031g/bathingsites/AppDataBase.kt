package se.miun.alel2104.dt031g.bathingsites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSite
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSiteDao

@Database(entities = [BathingSite::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun BathingSiteDao(): BathingSiteDao

    companion object {
        private var INSTANCE: AppDataBase? = null
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
}
