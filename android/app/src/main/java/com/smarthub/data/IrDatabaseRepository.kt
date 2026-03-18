package com.smarthub.data

import android.content.Context
import org.json.JSONArray
import java.io.InputStream
import java.nio.charset.Charset

data class DeviceCode(
    val type: String,
    val brand: String,
    val powerCommand: IntArray
)

object IrDatabaseRepository {
    
    private var codes: List<DeviceCode> = emptyList()

    fun loadDatabase(context: Context) {
        if (codes.isNotEmpty()) return // Already cached

        try {
            val inputStream: InputStream = context.assets.open("ir_database.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            
            val jsonString = String(buffer, Charset.forName("UTF-8"))
            val jsonArray = JSONArray(jsonString)
            
            val loadedCodes = mutableListOf<DeviceCode>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val type = obj.getString("type")
                val brand = obj.getString("brand")
                
                val commandsObj = obj.getJSONObject("commands")
                if (commandsObj.has("power")) {
                    val powerArray = commandsObj.getJSONArray("power")
                    val intArr = IntArray(powerArray.length())
                    for (j in 0 until powerArray.length()) {
                        intArr[j] = powerArray.getInt(j)
                    }
                    loadedCodes.add(DeviceCode(type, brand, intArr))
                }
            }
            codes = loadedCodes
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCodesForType(type: String): List<DeviceCode> {
        return codes.filter { it.type.equals(type, ignoreCase = true) }
    }
}
