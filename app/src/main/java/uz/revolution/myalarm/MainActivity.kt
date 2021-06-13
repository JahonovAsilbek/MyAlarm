package uz.revolution.myalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import uz.revolution.myalarm.adapters.AlarmAdapter
import uz.revolution.myalarm.add.AddDialog
import uz.revolution.myalarm.broadcast.AlarmBroadcast
import uz.revolution.myalarm.database.AppDatabase
import uz.revolution.myalarm.database.BudilnikDao
import uz.revolution.myalarm.databinding.ActivityMainBinding
import uz.revolution.myalarm.models.Budilnik
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var list: ArrayList<Budilnik>
    private var adapter: AlarmAdapter? = null
    lateinit var getDao: BudilnikDao

    lateinit var alarmIntent: Intent
    lateinit var pendingAlarmIntent: PendingIntent
    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        getDao = AppDatabase.get.getDatabase().getDao()

        loadAlarmManager()
        loadData()
        loadAdapter()
        floatClick()

    }

    private fun switchClick() {
        adapter?.setOnSwitchClick(object : AlarmAdapter.OnSwitchClick {
            override fun onClick(budilnik: Budilnik, position: Int) {

                var delta = budilnik.timePickerMills!! - System.currentTimeMillis()
                if (delta < 0) {
                    delta += 86400000
                }
                val time = delta + SystemClock.elapsedRealtime()

                if (budilnik.isOn == false) {
                    // updated alarm status in database
                    budilnik.id?.let { getDao.updateStatus(true, it) }

                    if (budilnik.isDaily == true) {
                        alarmManager.setInexactRepeating(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            time,
                            AlarmManager.INTERVAL_DAY,
                            pendingAlarmIntent
                        )

                        val position = binding.rv.scrollState
                        loadData()
                        loadAdapter()
                        binding.rv.scrollToPosition(position)

                    } else {
                        alarmManager.set(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            time,
                            pendingAlarmIntent
                        )

                        val position = binding.rv.scrollState
                        loadData()
                        loadAdapter()
                        binding.rv.scrollToPosition(position)

                    }
                } else {
                    // updated alarm status in database
                    budilnik.id?.let { getDao.updateStatus(false, it) }

                    val position = binding.rv.scrollState
                    loadData()
                    loadAdapter()
                    binding.rv.scrollToPosition(position)

                }
            }
        })
    }

    private fun loadAlarmManager() {
        alarmIntent = Intent(this@MainActivity, AlarmBroadcast::class.java)
        pendingAlarmIntent = PendingIntent.getBroadcast(this@MainActivity, 1, alarmIntent, 0)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
    }

    private fun loadAdapter() {
        adapter = AlarmAdapter(list)
        binding.rv.adapter = adapter
        switchClick()
        itemClick()
    }

    private fun itemClick() {
        adapter?.setOnItemClick(object : AlarmAdapter.OnItemClick {
            override fun onClick(budilnik: Budilnik, position: Int) {

                val dialog = AddDialog.newInstance(budilnik)
                dialog.setDialogOkClick(object : AddDialog.DialogOkClick {
                    override fun onClick() {
                        val position = binding.rv.scrollState
                        loadData()
                        loadAdapter()
                        binding.rv.scrollToPosition(position)
                    }
                })
                dialog.show(supportFragmentManager, "editDialog")
            }
        })
    }

    private fun floatClick() {
        binding.floataction.setOnClickListener {
            val beginTransaction = supportFragmentManager.beginTransaction()

            val dialog = AddDialog()

            dialog.setDialogOkClick(object : AddDialog.DialogOkClick {
                override fun onClick() {
                    loadData()
                    loadAdapter()
                }
            })

            dialog.show(beginTransaction, "dialog1")
        }
    }

    private fun loadData() {
        list = ArrayList()
        list = getDao.getAllBudilnik() as ArrayList
    }

    override fun onResume() {
        super.onResume()
        loadData()
        loadAdapter()
    }
}