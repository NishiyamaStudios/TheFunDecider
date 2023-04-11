package se.nishiyamastudios.fundeciderproject.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.utilityclass.IntentUtility

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    lateinit var frag : FavoritesFragment

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val favoritePlaceName: TextView
        val deleteFavoritePlace: ImageView
        val getFavoritePlaceInfo: ImageView
        val shareFavoritePlace: ImageView

        init {
            favoritePlaceName = view.findViewById(R.id.shopNameTV)
            deleteFavoritePlace = view.findViewById(R.id.favoriteDeleteImage)
            getFavoritePlaceInfo = view.findViewById(R.id.favoriteInfoImage)
            shareFavoritePlace = view.findViewById(R.id.favoriteShareImage)
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

        holder.favoritePlaceName.text = currentFavorite.placename



        holder.deleteFavoritePlace.setOnClickListener {
            frag.fbUtil.deleteFavoriteItem(currentFavorite)
        }


        holder.getFavoritePlaceInfo.setOnClickListener {

            frag.binding.selectedFavoritePlaceTV.text = currentFavorite.placename
            frag.binding.favoritePlaceStreetTV.text = currentFavorite.placestreet+" "+currentFavorite.housenumber
            frag.binding.favoritePlacePhoneTV.text = currentFavorite.phone
            frag.binding.favoritePlaceEmailTV.text = currentFavorite.email
            frag.binding.favoritePlaceWebsiteTV.text = currentFavorite.website
            frag.binding.favoritePlaceOpeningHoursMT.setText(currentFavorite.openinghours)
            frag.binding.selectedFavoritePlaceTV.text = currentFavorite.placename

            favoritesInfo.bringToFront()
            favoritesInfo.visibility = View.VISIBLE
            frag.binding.favoritesTV.visibility = View.INVISIBLE
        }

        holder.shareFavoritePlace.setOnClickListener {
            val placeName =  currentFavorite.placename.toString()
            val subject = "Hey! Come with me to one of my favorite places!\n"
            val body = "Let's check out"
            val shareIntent = intentUtil.sharePlace(
                placeName,
                subject,
                body
            )
            try {
                frag.startActivity(shareIntent)
            } catch (e: Exception) {
                // Do nothing
            }
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