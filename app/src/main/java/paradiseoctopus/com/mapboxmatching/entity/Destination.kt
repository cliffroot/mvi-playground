package paradiseoctopus.com.mapboxmatching.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Created by edanylenko on 3/27/18.
 */
class Destination {

    @SerializedName("location")
    @Expose
    var location: Point? = null
        private set

    @SerializedName("radius")
    @Expose
    var radius: Double? = null

    @SerializedName("name")
    @Expose
    var address: String? = null
        private set

    @SerializedName("prob")
    @Expose
    var prob: Double? = null

    @SerializedName("route")
    @Expose
    lateinit var routeEdges: List<Edge>

    var pointsToDestination: Array<LatLng>? = null
        private set


    fun routePoints(numberOfPoints: Int) : Array<LatLng?> {
            val geoPoints: Array<LatLng?>

            val points = mergeEdgePoints()

            val originalPointsAmount = points.size

            if (originalPointsAmount > numberOfPoints) {
                geoPoints = arrayOfNulls(numberOfPoints)
                for (i in 0 until numberOfPoints - 1) {
                    val correspondingIndex = Math.ceil(i.toDouble() * originalPointsAmount / numberOfPoints).toInt()
                    geoPoints[i] = points[correspondingIndex].asLatLng()
                }
                geoPoints[geoPoints.size - 1] = points[originalPointsAmount - 1].asLatLng()
            } else {
                geoPoints = points.map { it.asLatLng() }.toTypedArray()
            }

            return geoPoints
    }


    constructor() {}

    private fun mergeEdgePoints(): List<Point> {
        return routeEdges
                .mapIndexed { index, edge -> edge.points.subList(if (index == 0) 0 else 1, edge.points.size ) }
                .fold(mutableListOf()) { acc, edgePoints -> acc.apply { addAll(edgePoints) }}
    }



    internal fun withPointsToDestination(pointsToDestination: Array<LatLng>): Destination {
        this.pointsToDestination = pointsToDestination
        return this
    }

    override fun toString(): String {
        return ("Destination{" +
                "location=" + location +
                ", radius=" + radius +
                ", address='" + address + '\'' +
                ", prob=" + prob +
                ", routes=" + routeEdges +
                '}')
    }

}
