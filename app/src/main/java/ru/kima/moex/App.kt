package ru.kima.moex

import android.app.Application
import ru.kima.moex.model.SecurityService

class App : Application() {
    val securityService = SecurityService()
}