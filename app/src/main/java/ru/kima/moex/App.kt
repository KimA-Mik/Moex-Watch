package ru.kima.moex

import android.app.Application
import ru.kima.moex.model.DatabaseSecurityService
import ru.kima.moex.model.SecurityService

class App : Application() {
    val securityService = SecurityService()
    val databaseService by lazy { DatabaseSecurityService(applicationContext) }
}