package com.example.mywatchtrace.utils

import android.content.Context
import android.net.Uri
import com.example.mywatchtrace.data.entity.WatchEntity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object WatchCsvUtils {

    fun exportWatchesToCsv(context: Context, uri: Uri, watches: List<WatchEntity>) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                    // Header
                    writer.write("brand,model,category,productionYear,movementType,movementModel,hasBox,hasPapers,serialNumber,price,currency,status,soldPrice,soldCurrency,soldDate,soldTo\n")
                    watches.forEach { w ->
                        val line = "\"${w.brand}\",\"${w.model}\",\"${w.category}\",\"${w.productionYear}\",\"${w.movementType}\",\"${w.movementModel}\",${w.hasBox},${w.hasPapers},\"${w.serialNumber}\",\"${w.price}\",\"${w.currency}\",\"${w.status}\",\"${w.soldPrice}\",\"${w.soldCurrency}\",\"${w.soldDate}\",\"${w.soldTo}\"\n"
                        writer.write(line)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    fun importWatchesFromCsv(context: Context, uri: Uri, onWatchParsed: (WatchEntity) -> Unit) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    reader.readLine() // skip header
                    var line = reader.readLine()
                    while (line != null) {
                        val tokens = parseCsvLine(line)
                        if (tokens.size >= 12) {
                            val watch = WatchEntity(
                                brand = tokens.getOrNull(0) ?: "",
                                model = tokens.getOrNull(1) ?: "",
                                colorHex = "#2196F3",
                                category = tokens.getOrNull(2) ?: "Diver",
                                productionYear = tokens.getOrNull(3) ?: "",
                                movementType = tokens.getOrNull(4) ?: "Otomatik",
                                movementModel = tokens.getOrNull(5) ?: "",
                                hasBox = tokens.getOrNull(6)?.toBoolean() ?: false,
                                hasPapers = tokens.getOrNull(7)?.toBoolean() ?: false,
                                serialNumber = tokens.getOrNull(8) ?: "",
                                price = tokens.getOrNull(9) ?: "",
                                currency = tokens.getOrNull(10) ?: "TL",
                                status = tokens.getOrNull(11) ?: "Aktif",
                                soldPrice = tokens.getOrNull(12) ?: "",
                                soldCurrency = tokens.getOrNull(13) ?: "TL",
                                soldDate = tokens.getOrNull(14) ?: "",
                                soldTo = tokens.getOrNull(15) ?: ""
                            )
                            onWatchParsed(watch)
                        }
                        line = reader.readLine()
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        val currentField = StringBuilder()
        for (c in line) {
            when {
                c == '"' -> inQuotes = !inQuotes
                c == ',' && !inQuotes -> {
                    result.add(currentField.toString().trim())
                    currentField.clear()
                }
                else -> currentField.append(c)
            }
        }
        result.add(currentField.toString().trim())
        return result
    }
}
