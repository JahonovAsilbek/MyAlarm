package uz.revolution.myalarm.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "budilnik")
class Budilnik : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var time: String? = null
    var isOn: Boolean? = null
    var isDaily: Boolean? = null
    var timePickerMills:Long?=null

    constructor()

    @Ignore
    constructor(
        id: Int?,
        time: String?,
        isOn: Boolean?,
        isDaily: Boolean?,
        timePickerMills: Long?
    ) {
        this.id = id
        this.time = time
        this.isOn = isOn
        this.isDaily = isDaily
        this.timePickerMills = timePickerMills
    }

    @Ignore
    constructor(time: String?, isOn: Boolean?, isDaily: Boolean?, timePickerMills: Long?) {
        this.time = time
        this.isOn = isOn
        this.isDaily = isDaily
        this.timePickerMills = timePickerMills
    }

}