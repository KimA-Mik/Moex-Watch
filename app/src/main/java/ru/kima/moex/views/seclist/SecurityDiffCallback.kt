package ru.kima.moex.views.seclist

import androidx.recyclerview.widget.DiffUtil
import ru.kima.moex.model.Security

class SecurityDiffCallback(
    private val oldList: List<Security>,
    private val newList: List<Security>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].SECID == newList[newItemPosition].SECID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}