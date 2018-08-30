package paradiseoctopus.com.mapboxmatching.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by edanylenko on 3/27/18.
 */
class RoutePrediction {

    @SerializedName("destinations")
    @Expose
    var destinations: List<Destination>? = null

    override fun toString(): String {
        return "RoutePrediction{" +
                "destinations=" + destinations +
                '}'
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as RoutePrediction?

        return if (destinations != null) destinations == that!!.destinations else that!!.destinations == null
    }

    override fun hashCode(): Int {
        return if (destinations != null) destinations!!.hashCode() else 0
    }
}
