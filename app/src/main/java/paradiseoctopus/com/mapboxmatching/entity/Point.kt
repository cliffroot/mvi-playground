package paradiseoctopus.com.mapboxmatching.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Created by edanylenko on 3/27/18.
 */
class Point : Parcelable {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lon")
    @Expose
    var lon: Double? = null

    @SerializedName("alt")
    @Expose
    var alt: Double? = null

    fun asLatLng() : LatLng = LatLng(lat!!, lon!!, Double.NaN)

    override fun toString(): String {
        return "Point{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                '}'
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val point = o as Point?

        if (lat != point!!.lat) return false
        if (lon != point.lon) return false
        return if (alt != null) alt == point.alt else point.alt == null
    }

    override fun hashCode(): Int {
        var result = lat!!.hashCode()
        result = 31 * result + lon!!.hashCode()
        result = 31 * result + alt!!.hashCode()
        return result
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(this.lat)
        dest.writeValue(this.lon)
        dest.writeValue(this.alt)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        this.lat = `in`.readValue(Double::class.java.classLoader) as Double
        this.lon = `in`.readValue(Double::class.java.classLoader) as Double
        this.alt = `in`.readValue(Double::class.java.classLoader) as Double
    }

    companion object {

        val CREATOR: Parcelable.Creator<Point> = object : Parcelable.Creator<Point> {
            override fun createFromParcel(source: Parcel): Point {
                return Point(source)
            }

            override fun newArray(size: Int): Array<Point?> {
                return arrayOfNulls(size)
            }
        }
    }
}
