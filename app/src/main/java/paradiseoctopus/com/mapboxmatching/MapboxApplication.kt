package paradiseoctopus.com.mapboxmatching

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

/**
 * Created by edanylenko on 3/27/18.
 */

class MapboxApplication : Application() {

    val matchingPresenter : MatchingPresenter by lazy {
        MatchingPresenter(MapMatcher())
    }

    companion object {
        val ACCESS_TOKEN : String = ">>>INSERT_YOUR_TOKEN<<<"
    }

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(this.applicationContext, ACCESS_TOKEN)
    }
}