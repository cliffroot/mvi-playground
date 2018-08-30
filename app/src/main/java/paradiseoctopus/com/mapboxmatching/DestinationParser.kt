package paradiseoctopus.com.mapboxmatching

import com.google.gson.Gson
import paradiseoctopus.com.mapboxmatching.entity.RoutePrediction

/**
 * Created by edanylenko on 3/27/18.
 */
class DestinationParser {

    private val gson = Gson()

    fun deserializeRoutePrediction(payload: String): RoutePrediction {
        return deserialize(payload, RoutePrediction::class.java)
    }

    private fun <T> deserialize(payload: String, clazz: Class<T>): T {
        return gson.fromJson(payload, clazz)
    }
}