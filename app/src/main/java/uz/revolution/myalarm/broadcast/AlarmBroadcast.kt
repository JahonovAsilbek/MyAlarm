package uz.revolution.myalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import uz.revolution.myalarm.database.AppDatabase
import uz.revolution.myalarm.database.BudilnikDao
import java.text.SimpleDateFormat
import java.util.*


class AlarmBroadcast : BroadcastReceiver() {

    lateinit var getDao: BudilnikDao

    override fun onReceive(context: Context?, intent: Intent?) {

        getDao = AppDatabase.get.getDatabase().getDao()

        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val date = Date(System.currentTimeMillis())
        val formatTime = simpleDateFormat.format(date)


        for (i in 0 until getDao.getAllBudilnik().size) {
            val budilnik = getDao.getAllBudilnik()[i]

            if (budilnik.isOn == true && budilnik.time.equals(formatTime, true)) {

                Toast.makeText(context, "Jiring-jiring", Toast.LENGTH_SHORT).show()

                val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val r = RingtoneManager.getRingtone(context, notification)
                r.play()

                if (budilnik.isDaily == false) {

                    budilnik.id?.let { getDao.updateStatus(false, it) }

                }
            }
        }

    }
}