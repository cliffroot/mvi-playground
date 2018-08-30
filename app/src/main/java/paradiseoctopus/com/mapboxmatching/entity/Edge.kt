package paradiseoctopus.com.mapboxmatching.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by edanylenko on 3/27/18.
 */
class Edge {

    @SerializedName("points")
    @Expose
    val points = emptyList<Point>()
}
