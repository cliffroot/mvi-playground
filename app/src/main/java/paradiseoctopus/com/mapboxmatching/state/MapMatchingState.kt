package paradiseoctopus.com.mapboxmatching.state

import com.mapbox.api.directions.v5.models.DirectionsRoute
import paradiseoctopus.com.mapboxmatching.entity.Destination

/**
 * Created by edanylenko on 3/27/18.
 */
sealed class MapMatchingState {

    data class MapMatchingStateLoading(val destination : Destination) : MapMatchingState()

    data class MapMatchingStateSuccess(val numberOfPoints: Int,
                                       val destination: Destination,
                                       val mapMatching : DirectionsRoute) : MapMatchingState()

    object MapMapMatchingStateError : MapMatchingState()

}