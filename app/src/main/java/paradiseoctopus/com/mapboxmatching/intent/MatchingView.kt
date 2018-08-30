package paradiseoctopus.com.mapboxmatching.intent

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import paradiseoctopus.com.mapboxmatching.entity.Destination
import paradiseoctopus.com.mapboxmatching.state.MapMatchingState


/**
 * Created by edanylenko on 3/27/18.
 */
/**
 * Contract for the view
 */
interface MatchingView : MvpView {

    fun destinationIntent() : Observable<Destination>

    fun numberOfPointsIntent() : Observable<Int>

    fun render(matchingState: MapMatchingState) : Unit
}