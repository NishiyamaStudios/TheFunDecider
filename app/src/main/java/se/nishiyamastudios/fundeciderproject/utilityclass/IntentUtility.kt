package se.nishiyamastudios.fundeciderproject.utilityclass

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel

class IntentUtility {

    fun buildMapBrowserIntent (address: String, url: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse((url+address)))
    }
    fun buildBrowserIntent(url: String?): Intent {
        var newUrl = ""
        if (url != null) {
            if (url.substring(0, 3) == "www") {
                newUrl = url.replace("www", "http://www")
            } else {
                newUrl = url
            }
        }

        return Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
    }

    fun buildEmailIntent(toaddress: String?, subject: String, body: String): Intent {
        val uri = Uri.parse("mailto:" + toaddress)
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
        val mainbody = subject
        val sub = subject + body +" " + placename +" together.. :D"
        intent.putExtra(Intent.EXTRA_TEXT, mainbody)
        intent.putExtra(Intent.EXTRA_TEXT, sub)

        return intent

    }

}