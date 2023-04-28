package ru.kima.moex.views.seclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
    private val listener: SecurityActionListener
) : RecyclerView.Adapter<SecurityHolder>() {

    var securities: List<Security> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
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
            binding.wapriceTextView.text = price
            binding.root.setOnClickListener {
                listener.onSecurityDetail(security)
            }
        }
    }
}