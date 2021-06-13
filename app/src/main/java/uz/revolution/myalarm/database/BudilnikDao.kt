package uz.revolution.myalarm.database

import androidx.room.*
import uz.revolution.myalarm.models.Budilnik

@Dao
interface BudilnikDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(budilnik: Budilnik)

    @Query("select * from budilnik order by time")
    fun getAllBudilnik(): List<Budilnik>

    @Update
    fun updateBudilnik(budilnik: Budilnik)

    @Query("update budilnik set isOn=:isOn where id=:id")
    fun updateStatus(isOn: Boolean, id: Int)

    @Delete
    fun deleteBudilnik(budilnik: Budilnik)

}