package com.cut.android.running.provider.resources

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.cut.android.running.models.User
import com.cut.android.running.models.dto.UserDto
import com.cut.android.running.provider.BDsqlite
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManejadorAccionesFallidas(private val context: Context) {
    //instancia de UserService
    private val userService: UserService = RetrofitInstance.getRetrofit().create(
        UserService::class.java)
    fun guardarAccionFallida(accionNueva: AccionFallida) {
        val sharedPreferences = context.getSharedPreferences("AccionesFallidas", Context.MODE_PRIVATE)
        val accionesExistentes = sharedPreferences.getString("acciones", "[]")
        val accionesList = Gson().fromJson(accionesExistentes, Array<AccionFallida>::class.java).toMutableList()

        Log.d("MANEJADORACCIONES","INICIANDO $accionNueva")
        // Verifica si la acción ya existe en la lista
        val accionExistente = accionesList.find {
            it.tipo == accionNueva.tipo && it.payload == accionNueva.payload
        }

        // Si la acción no existe, agrégala a la lista
        if (accionExistente == null) {
            Log.d("MANEJADORACCIONES","NO EXISTE $accionNueva")

            accionesList.add(accionNueva)
            val editor = sharedPreferences.edit()
            editor.putString("acciones", Gson().toJson(accionesList))
            editor.apply()
        } else {
            // Si la acción ya existe
            Log.d("MANEJADORACCIONES","EXISTE $accionNueva :  $accionExistente")

        }
    }


    fun obtenerAccionesFallidas(): List<AccionFallida> {
        val sharedPreferences = context.getSharedPreferences("AccionesFallidas", Context.MODE_PRIVATE)
        val accionesExistentes = sharedPreferences.getString("acciones", "[]")
        return Gson().fromJson(accionesExistentes, Array<AccionFallida>::class.java).toList()
    }

    fun accionPendiente(): Pair<Boolean, String?> {
        val accionesFallidas = obtenerAccionesFallidas()
        if (accionesFallidas.isNotEmpty()) {
            // Retorna verdadero y el tipo de la primera acción pendiente
            return Pair(true, accionesFallidas.first().tipo)
        }
        return Pair(false, null)
    }


    fun limpiarAccionesFallidas() {
        val sharedPreferences = context.getSharedPreferences("AccionesFallidas", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("acciones").apply()
    }

    fun reintentarAccionesFallidas(scope: CoroutineScope) {
        val accionesFallidas = obtenerAccionesFallidas()

        accionesFallidas.forEach { accion ->
            when (accion.tipo) {
                "RegistroUsuario" -> reintentarRegistroUsuario(accion, scope)
                // Aquí puedes agregar más casos para diferentes tipos de acciones
            }
        }
    }
    fun reintentarRegistroUsuario(accion: AccionFallida, scope: CoroutineScope) {
        val usuarioDto = Gson().fromJson(accion.payload, UserDto::class.java)
        val usuarioSQLite = User(firstname = usuarioDto.firstname, email = usuarioDto.email) // Ajusta según tu modelo

        scope.launch(Dispatchers.IO) {
            try {
                val response = userService.addUser(usuarioDto)
                if (response.isSuccessful) {
                    // Registro exitoso
                    Log.d("Reintento Registro", "Registro exitoso de ${usuarioDto.email}")
                    GenerarSQLite(usuarioSQLite)
                    eliminarAccionFallida(accion)
                } else {
                    Log.d("Reintento Registro", "Error al reintentar registro de ${usuarioDto.email}")
                }
            } catch (e: Exception) {
                Log.d("Reintento Registro", "Excepción al reintentar registro de ${usuarioDto.email}: $e")
            }
        }
    }

    private fun GenerarSQLite(user: User) {
        val specialidad = user.specialty?.id
        Log.d("LF data","datos: $user")

        // Crear una instancia de la base de datos
        val db = BDsqlite(context)
        // Preparar los valores por defecto

        val values = ContentValues()
        values.put(BDsqlite.COLUMN_CODE, user.code)
        values.put(BDsqlite.COLUMN_FIRSTNAME, user.firstname)
        values.put(BDsqlite.COLUMN_EMAIL, user.email)
        values.put(BDsqlite.COLUMN_PASOS_HOY, 0)
        values.put(BDsqlite.COLUMN_PASOS_TOTALES, user.totalsteps)
        values.put(BDsqlite.COLUMN_DISTANCIA, user.totaldistance)
        values.put(BDsqlite.COLUMN_EDAD, user.age)
        values.put(BDsqlite.COLUMN_ESTATURA, user.height)
        values.put(BDsqlite.COLUMN_DISTANCEPERSTEP, user.distanceperstep)
        values.put(BDsqlite.COLUMN_PESO, user.weight)
        values.put(BDsqlite.COLUMN_SPECIALITYID, specialidad)
        values.put(BDsqlite.COLUMN_UPDATE_DATE, user.updateDate)

        // crear datos base en SQLite
        try {
            db.newData(user.email, values)
            Log.d("LF bd","bd creada con exito")

        }catch (e:Exception){
            Log.d("LF bd Error","bd creada sin exito, da la siguiente Exception: $e")
        }

    }

    fun eliminarAccionFallida(accionAEliminar: AccionFallida) {
        val sharedPreferences = context.getSharedPreferences("AccionesFallidas", Context.MODE_PRIVATE)
        // Obtener la lista actual de acciones fallidas
        val accionesExistentes = sharedPreferences.getString("acciones", "[]")
        val accionesList = Gson().fromJson(accionesExistentes, Array<AccionFallida>::class.java).toMutableList()

        // Eliminar la acción especificada
        val iterador = accionesList.iterator()
        while (iterador.hasNext()) {
            val accion = iterador.next()
            // Aquí asumimos que puedes comparar las acciones basado en el payload o algún otro identificador único
            if (accion.payload == accionAEliminar.payload && accion.tipo == accionAEliminar.tipo) {
                iterador.remove()
                break // Suponiendo que solo hay una coincidencia, salimos del bucle
            }
        }

        // Guardar la lista actualizada de acciones fallidas
        val editor = sharedPreferences.edit()
        editor.putString("acciones", Gson().toJson(accionesList))
        editor.apply()
    }

    private fun guardarAccionesActualizadas(acciones: List<AccionFallida>) {
        val sharedPreferences = context.getSharedPreferences("AccionesFallidas", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("acciones", Gson().toJson(acciones)).apply()
    }

}
