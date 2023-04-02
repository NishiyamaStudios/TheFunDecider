package se.nishiyamastudios.fundeciderproject.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.ui.start.StartViewModel

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {


    lateinit var frag : FavoritesFragment

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView
        val placeDelete: ImageView
        val placeInfo: ImageView

        init {

            placeName = view.findViewById(R.id.shopNameTV)
            placeDelete = view.findViewById(R.id.favoriteDeleteImage)
            placeInfo = view.findViewById(R.id.favoriteInfoImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // viewType: Int = kan definiera olika typer av rader, produktrad, headerrad etc..

        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentFavorite = frag.fbUtil.favoritePlaces.value!![position]
        val favoritesInfo = frag.binding.linearLayoutFavoriteInfo
        val favoritesRV = frag.binding.favoritesRV

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

        frag.binding.closeFavoriteInfoImage.setOnClickListener {
            favoritesRV.bringToFront()
            favoritesInfo.visibility = View.GONE
            frag.binding.favoritesTV.visibility = View.VISIBLE
        }




        /*
        //ligga och lyssna på om checkboxen är ikryssad eller inte
        holder.shoppingCheckbox.setOnCheckedChangeListener { compoundButton, shopchecked ->
            frag.model.doneShop(currentShop, shopchecked)
        }

         */

        /*
        holder.itemView.setOnClickListener {
            // TODO: Gå till läs mer
            frag.model.deleteShop(currentShop)
        }
        */

    }

    override fun getItemCount(): Int {
        //value?.let innebär att detta utförs om value inte är null
        frag.fbUtil.favoritePlaces.value?.let {
            return it.size
        }
        return 0
    }

}