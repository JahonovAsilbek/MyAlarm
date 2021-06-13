package uz.revolution.myalarm.add

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.revolution.myalarm.R
import uz.revolution.myalarm.broadcast.AlarmBroadcast
import uz.revolution.myalarm.database.AppDatabase
import uz.revolution.myalarm.database.BudilnikDao
import uz.revolution.myalarm.databinding.FragmentAddDialogBinding
import uz.revolution.myalarm.models.Budilnik
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"

class AddDialog : DialogFragment() {

    private var editBudilnik: Budilnik? = null
    private var dialogOkClick: DialogOkClick? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editBudilnik = it.getSerializable(ARG_PARAM1) as Budilnik?
        }
    }

    lateinit var binding: FragmentAddDialogBinding
    lateinit var alarmIntent: Intent
    lateinit var pendingAlarmIntent: PendingIntent
    lateinit var alarmManager: AlarmManager
    lateinit var getDao: BudilnikDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDialogBinding.inflate(layoutInflater)
        getDao = AppDatabase.get.getDatabase().getDao()


        cancelClick()
        okClick()
        if (editBudilnik != null) {
            if (editBudilnik?.isDaily == true) {
                binding.repeatSwitch.isChecked = true
            }
            val date = Date()
            date.time = editBudilnik?.timePickerMills!!
            binding.timePicker.setDefaultDate(date)
        }

        return binding.root
    }

    private fun okClick() {
        binding.ok.setOnClickListener {
            val checked = binding.repeatSwitch.isChecked
            val timeMillis = binding.timePicker.date.time
            val currentTime = System.currentTimeMillis()
            var delta = timeMillis - currentTime
            if (delta < 0) {
                delta += 86400000
            }

            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val date = Date(timeMillis)
            val formatTime = simpleDateFormat.format(date)

            setAlarm(formatTime, delta, checked, timeMillis)

        }
    }


    private fun setAlarm(formatTime: String, delta: Long, repeat: Boolean, timeMillis: Long) {
        val time = SystemClock.elapsedRealtime() + delta
        alarmIntent = Intent(binding.root.context, AlarmBroadcast::class.java)
        pendingAlarmIntent = PendingIntent.getBroadcast(binding.root.context, 1, alarmIntent, 0)
        alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
        if (repeat) {
            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY,
                pendingAlarmIntent
            )

            if (editBudilnik != null) {
                getDao.updateBudilnik(
                    Budilnik(
                        editBudilnik!!.id,
                        formatTime,
                        isOn = true,
                        isDaily = true,
                        timeMillis
                    )
                )
            } else {
                getDao.insert(Budilnik(formatTime, isOn = true, isDaily = true, timeMillis))
            }

        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingAlarmIntent)

            if (editBudilnik != null) {
                getDao.updateBudilnik(
                    Budilnik(
                        editBudilnik!!.id,
                        formatTime,
                        isOn = true,
                        isDaily = false,
                        timeMillis
                    )
                )
            } else {
                getDao.insert(Budilnik(formatTime, isOn = true, isDaily = false, timeMillis))
            }

        }

        if (dialogOkClick != null) {
            dialogOkClick?.onClick()
        }

        Log.d("AAAA", "setAlarm: $time")

        dismiss()
    }

    private fun cancelClick() {
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme() = R.style.RoundedCornersDialog

    companion object {

        fun newInstance(budilnik: Budilnik) = AddDialog().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_PARAM1, budilnik)
            }
        }
    }

    interface DialogOkClick {
        fun onClick()
    }

    fun setDialogOkClick(dialogOkClick: DialogOkClick) {
        this.dialogOkClick = dialogOkClick
    }

}