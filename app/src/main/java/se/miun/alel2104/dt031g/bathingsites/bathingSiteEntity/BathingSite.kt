package se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BathingSite (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val name: String?,
    val description: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val grade: Double?,
    val waterTmp: Double?,
    val waterTmpDate: String
)