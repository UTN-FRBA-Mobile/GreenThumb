package com.utn.greenthumb.alarm
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Este método se llama cuando la alarma se dispara.
        // El sistema puede diferir esta llamada si se usa una alarma inexacta.

        Log.d("AlarmReceiver", "¡Alarma recibida! Hora: ${System.currentTimeMillis()}")
        Toast.makeText(context, "¡Alarma diferida recibida!", Toast.LENGTH_LONG).show()

        // Aquí puedes iniciar un servicio, mostrar una notificación, etc.

        // NOTA: Si usas una alarma repetitiva, en este punto deberías
        // considerar reprogramar la próxima alarma si la lógica lo requiere.
        // Sin embargo, para setInexactRepeating, el sistema se encarga de repetir.
    }
}