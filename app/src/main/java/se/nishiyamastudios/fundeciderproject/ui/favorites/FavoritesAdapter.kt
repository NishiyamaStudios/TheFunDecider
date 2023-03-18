package se.nishiyamastudios.fundeciderproject.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.nishiyamastudios.fundeciderproject.R

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {


    lateinit var frag : FavoritesFragment

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView
        val placeDelete: ImageView

        init {

            placeName = view.findViewById(R.id.shopNameTV)
            placeDelete = view.findViewById(R.id.shopDeleteImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // viewType: Int = kan definiera olika typer av rader, produktrad, headerrad etc..

        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentFavorite = frag.fbUtil.favoritePlaces.value!![position]

        holder.placeName.text = currentFavorite.placename

        holder.placeDelete.setOnClickListener {
            frag.fbUtil.deleteFavoriteItem(currentFavorite)
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