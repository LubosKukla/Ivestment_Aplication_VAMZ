package com.example.ivestmentaplicationvamz.ui.component

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ivestmentaplicationvamz.R

class ReminderReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(ctx: Context, intent: Intent) {
        Toast.makeText(ctx, "Upozornenie odoslané", Toast.LENGTH_SHORT).show()

        val princ   = intent.getStringExtra("EXTRA_PRINCIPAL") ?: "-"
        val contrib = intent.getStringExtra("EXTRA_CONTRIBUTION") ?: "-"
        val yrs     = intent.getStringExtra("EXTRA_YEARS") ?: "-"
        val rate    = intent.getStringExtra("EXTRA_RATE") ?: "-"
        val freqLbl = intent.getStringExtra("EXTRA_FREQUENCY_LABEL") ?: "-"

        val content = "Vklad: $princ €, príspevok: $contrib €/rok, " +
                "$yrs rokov, úrok $rate %, zopakovať: $freqLbl"

        val notif = NotificationCompat.Builder(ctx, "invest_reminders")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Investičná pripomienka")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(ctx).notify(1001, notif)
    }
}