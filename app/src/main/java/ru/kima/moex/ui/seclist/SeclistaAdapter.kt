package ru.kima.moex.ui.seclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kima.moex.data.Security
import ru.kima.moex.databinding.ListItemSecurityBinding

class SecurityHolder(
    val binding: ListItemSecurityBinding
) : RecyclerView.ViewHolder(binding.root) {

}

class SeclistaAdapter(
    private val securities: List<Security>
) : RecyclerView.Adapter<SecurityHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecurityHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSecurityBinding.inflate(inflater, parent, false)
        return SecurityHolder(binding)
    }

    override fun getItemCount() = securities.size

    override fun onBindViewHolder(holder: SecurityHolder, position: Int) {
        val security = securities[position]
        holder.apply {
            binding.secidTextView.text = security.SECID
            binding.secnameTextView.text = security.SECNAME
            binding.wapriceTextView.text = security.WAPRICE.toString()
        }
    }
}