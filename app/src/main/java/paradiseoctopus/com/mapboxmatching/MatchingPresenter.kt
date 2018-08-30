package paradiseoctopus.com.mapboxmatching

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import paradiseoctopus.com.mapboxmatching.state.MapMatchingState
import paradiseoctopus.com.mapboxmatching.view.MainActivity


/**
 * Created by edanylenko on 3/27/18.
 */

/**
 * Pass intents received from the view
 * Down to the business logic
 * Generating corresponding model (state)
 * Which will be passed back to the view
 */
class MatchingPresenter(val mapMatcher : MapMatcher) : MviBasePresenter<MainActivity, MapMatchingState>() {

    override fun bindIntents() {

        /**
         * MODEL(INTENT)
         */
        val destinationObservable: Observable<MapMatchingState> =
                intent(MainActivity::destinationIntent)
                        .switchMap { newDestination -> mapMatcher.match(destination = newDestination) }
                        .observeOn(AndroidSchedulers.mainThread())


        val numberOfPointsObservable : Observable<MapMatchingState> =
                intent(MainActivity::numberOfPointsIntent)
                        .switchMap { numberOfPoints -> mapMatcher.match(numberOfPoints = numberOfPoints) }
                        .observeOn(AndroidSchedulers.mainThread())


        /**
         * VIEW(MODEL)
         */
        subscribeViewState(Observable.merge(destinationObservable, numberOfPointsObservable), MainActivity::render)

    }

}