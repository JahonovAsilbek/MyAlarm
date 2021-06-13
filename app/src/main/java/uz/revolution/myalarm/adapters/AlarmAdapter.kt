package uz.revolution.myalarm.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.revolution.myalarm.databinding.ItemAlarmBinding
import uz.revolution.myalarm.models.Budilnik

class AlarmAdapter(var list: ArrayList<Budilnik>) :
    RecyclerView.Adapter<AlarmAdapter.VH>() {

    private var onSwitchClick: OnSwitchClick? = null
    private var onItemClick: OnItemClick? = null


    inner class VH(var binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun onBind(budilnik: Budilnik, position: Int) {
            binding.time.text = budilnik.time
            if (budilnik.isOn == true) {

                binding.time.setTextColor(Color.parseColor("#000000"))
                binding.`when`.setTextColor(Color.parseColor("#2B2B2B"))

                if (budilnik.isDaily == true) {
                    binding.`when`.text = "Everyday"
                } else {
                    binding.`when`.text = "Once"
                }

                binding.switchBtn.isChecked = true

            } else {

                binding.time.setTextColor(Color.parseColor("#888888"))
                binding.`when`.setTextColor(Color.parseColor("#888888"))

                binding.`when`.text = "Off"

                binding.switchBtn.isChecked = false
            }

            binding.switchBtn.setOnCheckedChangeListener { buttonView, isChecked ->
                if (onSwitchClick != null) {

                    binding.switchBtn.isChecked = budilnik.isOn != true

                    onSwitchClick?.onClick(budilnik, position)
                }
            }

            itemView.setOnClickListener {
                if (onItemClick != null) {
                    onItemClick?.onClick(budilnik, position)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    public interface OnSwitchClick {
        fun onClick(budilnik: Budilnik, position: Int)
    }

    fun setOnSwitchClick(onSwitchClick: OnSwitchClick) {
        this.onSwitchClick = onSwitchClick
    }

    interface OnItemClick {
        fun onClick(budilnik: Budilnik, position: Int)
    }

    fun setOnItemClick(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }
}