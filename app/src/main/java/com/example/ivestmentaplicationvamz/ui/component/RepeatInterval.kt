package com.example.ivestmentaplicationvamz.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration

enum class RepeatInterval(val label: String, val millis: Long) {
    @RequiresApi(Build.VERSION_CODES.O)
    DAILY("Denne", Duration.ofDays(1).toMillis()),
    @RequiresApi(Build.VERSION_CODES.O)
    WEEKLY("Týždenne", Duration.ofDays(7).toMillis()),
    @RequiresApi(Build.VERSION_CODES.O)
    MONTHLY("Mesačne", Duration.ofDays(30).toMillis()),
    @RequiresApi(Build.VERSION_CODES.O)
    YEARLY("Ročne", Duration.ofDays(365).toMillis())
}