package se.nishiyamastudios.fundeciderproject.utilityclass

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority

class LocationUtility : AppCompatActivity() {

    fun requestLocation(interval: Long, fastestinterval: Long, maxwait: Long): LocationRequest {

        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(fastestinterval)
            .setMaxUpdateDelayMillis(maxwait)
            .build()
    }

}