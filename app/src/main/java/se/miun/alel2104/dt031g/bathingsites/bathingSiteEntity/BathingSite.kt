package se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The data class containing the bathing-site database structure.
 * @author Albin Eliasson
 */
@Entity(tableName = "bathingsite")
data class BathingSite (
    var name: String?,
    var description: String?,
    var address: String?,
    var latitude: Double?,
    var longitude: Double?,
    var grade: Float?,
    var waterTmp: Double?,
    var waterTmpDate: String?,
    @PrimaryKey(autoGenerate = true) var uid: Long = 0
)