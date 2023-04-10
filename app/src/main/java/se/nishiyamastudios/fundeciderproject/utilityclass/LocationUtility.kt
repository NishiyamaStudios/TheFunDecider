package se.nishiyamastudios.fundeciderproject.utilityclass

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority

class LocationUtility : AppCompatActivity() {

    fun RequestLocation(interval : Long, fastestinterval : Long, maxwait : Long): LocationRequest {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(fastestinterval)
            .setMaxUpdateDelayMillis(maxwait)
            .build()

        return locationRequest
    }

}