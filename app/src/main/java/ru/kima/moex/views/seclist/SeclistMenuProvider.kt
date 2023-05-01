package ru.kima.moex.views.seclist

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import ru.kima.moex.R

interface SeclistMenuListener {
    fun onQueryString(query: String?)
    fun onFavoriteChange()
}

class SeclistMenuProvider(
    private val listener: SeclistMenuListener
) : MenuProvider {

    private lateinit var favoriteCheckbox: MenuItem
    private var isFavoriteCheckboxChecked = false
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_securities_list_menu, menu)

        favoriteCheckbox = menu.findItem(R.id.menu_show_favorite)
        favoriteCheckbox.isChecked = isFavoriteCheckboxChecked

        val search = menu.findItem(R.id.menu_item_search)
        val searchView = search?.actionView as SearchView
        searchView.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    listener.onQueryString(newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    listener.onQueryString(query)
                    return true
                }
            })
        }

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_show_favorite -> {
                listener.onFavoriteChange()
                true
            }

            else -> false
        }
    }

    fun setFavoriteCheckbox(flag: Boolean) {
        if (this::favoriteCheckbox.isInitialized)
            favoriteCheckbox.isChecked = flag
        else
            isFavoriteCheckboxChecked = flag

    }
}