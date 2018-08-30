package paradiseoctopus.com.mapboxmatching

import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_DRIVING
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.matching.v5.MapboxMapMatching
import com.mapbox.api.matching.v5.models.MapMatchingResponse
import com.mapbox.api.matching.v5.models.MapMatchingTracepoint
import com.mapbox.geojson.Point
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import paradiseoctopus.com.mapboxmatching.MapboxApplication.Companion.ACCESS_TOKEN
import paradiseoctopus.com.mapboxmatching.entity.Destination
import paradiseoctopus.com.mapboxmatching.state.MapMatchingState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by edanylenko on 3/27/18.
 */

/**
 * business logic
 */
class MapMatcher {

    var currentNumberOfPoints : Int = 23
    var currentDestination : Destination? = null

    fun match (numberOfPoints: Int = currentNumberOfPoints,
               destination: Destination? = currentDestination) : Observable<MapMatchingState> {

        currentNumberOfPoints = numberOfPoints
        currentDestination = destination

        return Observable.just(matchingRequest(destination, numberOfPoints))
                .subscribeOn(Schedulers.io())
                .map { it.executeCall() }
                .map { tracePointsFromResponse(it) }
                .map { listOfMatchings -> listOfMatchings.map {point -> Point.fromLngLat(point.location()!!.longitude(), point.location()!!.latitude())} }
                .map (this::navigationRequest)
                .flatMapSingle(this::directionsRouteSubject)
                .map { directionsRoute ->
                    MapMatchingState.MapMatchingStateSuccess(numberOfPoints, destination!!, directionsRoute) as MapMatchingState }
                .startWith(MapMatchingState.MapMatchingStateLoading(destination!!)) // show if we're loading
                .onErrorReturn { MapMatchingState.MapMapMatchingStateError } // don't propagate error to `onError`, rather emit a new state
    }

    private fun matchingRequest (destination: Destination?, numberOfPoints: Int) : MapboxMapMatching =
            MapboxMapMatching.builder()
                .accessToken(ACCESS_TOKEN)
                .coordinates(destination?.routePoints(numberOfPoints)!!.map { Point.fromLngLat(it!!.longitude, it.latitude) })
                .steps(true)
                .tidy(true)
                .profile(PROFILE_DRIVING)
                .build()

    private fun tracePointsFromResponse(result: Response<MapMatchingResponse>): List<MapMatchingTracepoint> =
            if (result.isSuccessful && result.body() != null)
                result.body()!!.tracepoints()!!
            else
                listOf()

    private fun navigationRequest (matchingResult : List<Point> ) : NavigationRoute.Builder =
            NavigationRoute.builder()
                .accessToken(ACCESS_TOKEN)
                .profile(PROFILE_DRIVING)
                .origin(matchingResult[0])
                .voiceUnits(DirectionsCriteria.METRIC)
                .destination(matchingResult[matchingResult.size - 1])
                .radiuses(*arrayOfNulls<Double>(matchingResult.size).map { _ -> 20.toDouble() }.toDoubleArray())
                .apply { matchingResult.subList(1, matchingResult.size - 1).forEach {addWaypoint(it)} }

    private fun directionsRouteSubject (builder : NavigationRoute.Builder) : SingleSubject<DirectionsRoute> {
        val resultSubject : SingleSubject<DirectionsRoute> = SingleSubject.create()
        builder.build().getRoute(object: Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>?, response: Response<DirectionsResponse>?) {
                val body = response!!.body()
                if (body != null && body.routes().size > 0) {
                    resultSubject.onSuccess(body.routes()[0])
                } else {
                    resultSubject.onError(IllegalAccessException("Failed to get DirectionRoute from MAP_BOX"))
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>?, t: Throwable) {
                resultSubject.onError(t)
            }
        })
        return resultSubject
    }
}