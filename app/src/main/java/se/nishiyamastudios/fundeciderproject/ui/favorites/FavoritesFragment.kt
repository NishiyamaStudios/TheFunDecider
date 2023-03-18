package se.nishiyamastudios.fundeciderproject.ui.favorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import se.nishiyamastudios.fundeciderproject.FirebaseListObject
import se.nishiyamastudios.fundeciderproject.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentDetailsBinding
import se.nishiyamastudios.fundeciderproject.databinding.FragmentFavoritesBinding
import se.nishiyamastudios.fundeciderproject.ui.login.LoginViewModel

class FavoritesFragment : Fragment() {

    var _binding : FragmentFavoritesBinding? = null
    val binding get() = _binding!!

    val viewModel by viewModels<FavoritesViewModel>()
    val fbUtil by viewModels<FirebaseUtility>()

    var favoritesadapter = FavoritesAdapter()

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesadapter.frag = this

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoritesRV.adapter = favoritesadapter
        binding.favoritesRV.layoutManager = LinearLayoutManager(requireContext())

        val favoritesObserver = Observer<List<FirebaseListObject>> {
            favoritesadapter.notifyDataSetChanged()
        }

        fbUtil.favoritePlaces.observe(viewLifecycleOwner, favoritesObserver)

        fbUtil.loadFavorites()

    }

}