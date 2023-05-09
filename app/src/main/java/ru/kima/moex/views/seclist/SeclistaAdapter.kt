package ru.kima.moex.views.seclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import ru.kima.moex.R
import ru.kima.moex.ResourceProvider
import ru.kima.moex.databinding.ListItemSecurityBinding
import ru.kima.moex.model.Security

interface SecurityActionListener {
    fun onSecurityDetail(security: Security)
}

class SecurityHolder(
    val binding: ListItemSecurityBinding
) : RecyclerView.ViewHolder(binding.root) {

}

class SeclistaAdapter(
    private val listener: SeclistViewModel
) : RecyclerView.Adapter<SecurityHolder>() {

    var securities: List<Security> = emptyList()
    private val resourceProvider = ResourceProvider.getInstance()
    @ColorInt
    private var increasing_green = 0
    @ColorInt
    private var decreasing_red = 0
    @ColorInt
    private var neutral = 0

    init {
        updateColors()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecurityHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSecurityBinding.inflate(inflater, parent, false)
        return SecurityHolder(binding)
    }

    override fun getItemCount() = securities.size

    override fun onBindViewHolder(holder: SecurityHolder, position: Int) {
        val security = securities[position]
        holder.itemView.tag = security
        holder.apply {

            binding.secidTextView.text = security.SECID
            binding.secnameTextView.text = security.SECNAME
            val price = if (!security.WAPRICE.isNaN()) "${String.format("%.2f", security.WAPRICE)}₽"
            else "Empty"
            if (security.LASTCHANGE != 0.0) {
                binding.wapriceTextView.text = "(${security.LASTCHANGEPRCNT}%) $price"
                if (security.LASTCHANGE > 0.0) {
                    binding.wapriceTextView.setTextColor(increasing_green)
                } else if (security.LASTCHANGE < 0.0) {
                    binding.wapriceTextView.setTextColor(decreasing_red)
                }
            } else {
                binding.wapriceTextView.text = price
                binding.wapriceTextView.setTextColor(neutral)
            }
            binding.root.setOnClickListener {
                listener.onSecurityDetail(security)
            }
        }
    }

    private fun updateColors() {
        increasing_green = resourceProvider.getColor(R.color.increasing_green)
        decreasing_red = resourceProvider.getColor(R.color.decreasing_red)
        neutral = resourceProvider.getColor(R.color.contrast)
    }
}