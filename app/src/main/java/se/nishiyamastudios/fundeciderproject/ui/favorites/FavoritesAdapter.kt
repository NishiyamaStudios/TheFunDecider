package se.nishiyamastudios.fundeciderproject.ui.favorites

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.ktx.api.model.place
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.utilityclass.IntentUtility

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {


    lateinit var frag : FavoritesFragment

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView
        val placeDelete: ImageView
        val placeInfo: ImageView
        val placeShare: ImageView

        init {

            placeName = view.findViewById(R.id.shopNameTV)
            placeDelete = view.findViewById(R.id.favoriteDeleteImage)
            placeInfo = view.findViewById(R.id.favoriteInfoImage)
            placeShare = view.findViewById(R.id.favoriteShareImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentFavorite = frag.fbUtil.favoritePlaces.value!![position]
        val favoritesInfo = frag.binding.linearLayoutFavoriteInfo
        val favoritesRV = frag.binding.favoritesRV
        val intentUtil = IntentUtility()

        holder.placeName.text = currentFavorite.placename



        holder.placeDelete.setOnClickListener {
            frag.fbUtil.deleteFavoriteItem(currentFavorite)
        }


        holder.placeInfo.setOnClickListener {

            frag.binding.selectedFavoritePlaceTV.setText(currentFavorite.placename)
            frag.binding.favoritePlaceStreetTV.setText(currentFavorite.placestreet+" "+currentFavorite.housenumber)
            frag.binding.favoritePlacePhoneTV.setText(currentFavorite.phone)
            frag.binding.favoritePlaceEmailTV.setText(currentFavorite.email)
            frag.binding.favoritePlaceWebsiteTV.setText(currentFavorite.website)
            frag.binding.favoritePlaceOpeningHoursMT.setText(currentFavorite.openinghours)
            frag.binding.selectedFavoritePlaceTV.setText(currentFavorite.placename)

            favoritesInfo.bringToFront()
            favoritesInfo.visibility = View.VISIBLE
            frag.binding.favoritesTV.visibility = View.INVISIBLE
        }

        holder.placeShare?.setOnClickListener {
            val placeName =  currentFavorite.placename.toString()
            val subject = "Hey! This is one of my favorite places!\n"
            val body = "Let's check out"
            val shareIntent = intentUtil.sharePlace(
                placeName,
                subject,
                body
            )
            try {
                frag.startActivity(shareIntent)
            } catch (e: Exception) {

            }
            Log.i("FUNFUN", placeName)
        }

        frag.binding.closeFavoriteInfoImage.setOnClickListener {
            favoritesRV.bringToFront()
            favoritesInfo.visibility = View.GONE
            frag.binding.favoritesTV.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        frag.fbUtil.favoritePlaces.value?.let {
            return it.size
        }
        return 0
    }

}