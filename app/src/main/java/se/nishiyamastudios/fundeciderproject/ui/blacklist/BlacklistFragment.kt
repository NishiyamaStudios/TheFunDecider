package se.nishiyamastudios.fundeciderproject.ui.blacklist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.nishiyamastudios.fundeciderproject.R

class BlacklistFragment : Fragment() {

    companion object {
        fun newInstance() = BlacklistFragment()
    }

    private lateinit var viewModel: BlacklistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blacklist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BlacklistViewModel::class.java)
        // TODO: Use the ViewModel
    }

}