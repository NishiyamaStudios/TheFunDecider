package se.nishiyamastudios.fundeciderproject.ui.blacklist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentBlacklistBinding
import se.nishiyamastudios.fundeciderproject.ui.login.LoginViewModel

class BlacklistFragment : Fragment() {

    var _binding : FragmentBlacklistBinding? = null
    val binding get() = _binding!!

    val viewModel by viewModels<LoginViewModel>()

    companion object {
        fun newInstance() = BlacklistFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBlacklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}