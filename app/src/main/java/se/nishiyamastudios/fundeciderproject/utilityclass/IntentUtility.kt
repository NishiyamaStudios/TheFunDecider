package se.nishiyamastudios.fundeciderproject.utilityclass

import android.content.Intent
import android.net.Uri

class IntentUtility {

    fun buildMapBrowserIntent (address: String, url: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse((url+address)))
    }
    fun buildBrowserIntent(url: String?): Intent {
        var newUrl = ""
        if (url != null) {
            // Handle URLs without http://
            newUrl = if (url.substring(0, 3) == "www") {
                url.replace("www", "http://www")
            } else {
                url
            }
        }

        return Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
    }

    fun buildEmailIntent(toaddress: String?, subject: String, body: String): Intent {
        val uri = Uri.parse("mailto:$toaddress")
            .buildUpon()
            .appendQueryParameter("subject", subject)
            .appendQueryParameter("body", body)
            .appendQueryParameter("to", toaddress)
            .build()

        return Intent(Intent.ACTION_SENDTO, uri)
    }

    fun sharePlace(placename: String, subject: String, body: String): Intent {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val sub = "$subject$body $placename together.. :D"
        intent.putExtra(Intent.EXTRA_TEXT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, sub)

        return intent

    }

}