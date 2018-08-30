package paradiseoctopus.com.mapboxmatching.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxbinding2.widget.RxSeekBar
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.Polyline
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import paradiseoctopus.com.mapboxmatching.DestinationParser
import paradiseoctopus.com.mapboxmatching.MapboxApplication
import paradiseoctopus.com.mapboxmatching.MatchingPresenter
import paradiseoctopus.com.mapboxmatching.R
import paradiseoctopus.com.mapboxmatching.entity.Destination
import paradiseoctopus.com.mapboxmatching.intent.MatchingView
import paradiseoctopus.com.mapboxmatching.state.MapMatchingState
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : MatchingView, MviActivity<MainActivity,  MatchingPresenter>() {

    override fun createPresenter(): MatchingPresenter {
        //fixme: provided by dependency injection
        return (application as MapboxApplication).matchingPresenter
    }

    /**
     * intent of the view (selected destination)
     * returns observable of the currently selected destination
     * just imagine
     */
    override fun destinationIntent() : Observable<Destination> = Observable.just(
                DestinationParser().deserializeRoutePrediction(File("/sdcard/predictions_current.json")
                                    .bufferedReader()
                                    .use { it.readText() }).destinations!![0])


    /**
     * intent of the view (selected number of points)
     * observable of currently selected number of points
     */
    override fun numberOfPointsIntent() : Observable<Int> = RxSeekBar.userChanges(numberOfDestinationsPicker)
                .map {it + 5}
                .debounce(500, TimeUnit.MILLISECONDS)

    /**
     * view should be able to render incoming model
     */
    override fun render(mapMapMatchingState: MapMatchingState) {
        when (mapMapMatchingState) {
            is MapMatchingState.MapMatchingStateSuccess -> mapView.getMapAsync {
                map ->
                    removeMarkersAndPolylines(map)
                    drawPolylineFromRoute(map, mapMapMatchingState.mapMatching)
                    drawMarkersFromDestination(map, mapMapMatchingState.destination, mapMapMatchingState.numberOfPoints)
                    zoomToPoint(map, mapMapMatchingState.destination.location.let { LatLng(it!!.lat!!, it.lon!!)})
                    numberOfDestinationsText.text = mapMapMatchingState.numberOfPoints.toString()
                    progressBarLoading.visibility = View.INVISIBLE
            }
            is MapMatchingState.MapMatchingStateLoading -> progressBarLoading.visibility = View.VISIBLE
            is MapMatchingState.MapMapMatchingStateError -> Toast.makeText(this, "Error has happened", Toast.LENGTH_LONG).show()
        }
    }

    private lateinit var mapView : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.getInt("numberOfPoints")?.apply { numberOfDestinationsPicker.progress = this }

        mapView = MapView(this)
        mapView.setStyleUrl(getString(R.string.mapbox_style_mapbox_streets))
        mapViewContainter.addView(mapView)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("numberOfPoints", numberOfDestinationsPicker.progress)
    }


    private var polylines : MutableSet<Polyline> = mutableSetOf()
    private var markers : MutableSet<Marker> = mutableSetOf()

    private fun removeMarkersAndPolylines(map : MapboxMap) : Unit {
        polylines.forEach {map.removePolyline(it)}
        markers.forEach {map.removeMarker(it)}
    }

    private fun zoomToPoint (map: MapboxMap, point : LatLng) : Unit {
        map.cameraPosition = CameraPosition.Builder().target(point).zoom(12.toDouble()).build()
    }

    private fun drawPolylineFromRoute(map : MapboxMap, route : DirectionsRoute) : Unit {
        polylines.add(map
                .addPolyline(PolylineOptions()
                    .addAll(PolylineUtils.decode(route.geometry()!!, 6)
                            .map { LatLng(it.latitude(), it.longitude()) })))
    }

    private fun drawMarkersFromDestination(map : MapboxMap, destination: Destination, number: Int) : Unit {
        destination.routePoints(number)
                .map { LatLng(it!!.latitude, it.longitude) }
                .forEach { markers.add(map.addMarker(MarkerOptions().position(it))) }
    }
}
