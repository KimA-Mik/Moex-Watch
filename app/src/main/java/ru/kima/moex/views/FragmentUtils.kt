package ru.kima.moex.views

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.kima.moex.App
import ru.kima.moex.views.secdetails.SecurityDetailsViewModel
import ru.kima.moex.views.seclist.SeclistViewModel

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            SeclistViewModel::class.java -> {
                SeclistViewModel(app.securityService, app.databaseService)
            }

            SecurityDetailsViewModel::class.java -> {
                SecurityDetailsViewModel(app.securityService, app.databaseService)
            }

            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)
const val MILLISECONDS_IN_DAY = 86400000f
const val MAGIC_DAYS = 11409f
const val MAGIC_DAYS2 = 15000f