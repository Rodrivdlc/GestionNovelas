package com.example.novelas

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class NovelaWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        // Aquí puedes obtener los datos de la novela del día
        val novela = getNovelaDelDia()

        views.setTextViewText(R.id.widgetNovelaTitle, novela.titulo)
        views.setTextViewText(R.id.widgetNovelaAuthor, novela.autor)

        // Configura un intent para abrir la aplicación al hacer clic en el widget
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        views.setOnClickPendingIntent(R.id.widgetTitle, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getNovelaDelDia(): Novela {
        return Novela("Título de Ejemplo", "Autor de Ejemplo", 2023, "Sinopsis de Ejemplo")
    }
}