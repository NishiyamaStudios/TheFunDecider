package se.nishiyamastudios.fundeciderproject.ui.start

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentDetailsBinding
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding
import se.nishiyamastudios.fundeciderproject.ui.login.LoginViewModel

class StartFragment : Fragment() {

    var _binding : FragmentStartBinding? = null
    val binding get() = _binding!!

    val viewModel by viewModels<LoginViewModel>()

    companion object {
        fun newInstance() = StartFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity  = view.context as? AppCompatActivity

        if (activity != null) {
            val navView = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.visibility = View.VISIBLE
        }


    }

}