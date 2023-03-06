package se.nishiyamastudios.fundeciderproject.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    var _binding : FragmentLoginBinding? = null
    val binding get() = _binding!!

    val viewModel by viewModels<LoginViewModel>()

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observera vårt felmeddelande
        val errorObserver  = Observer<String> {errorMess ->
            //Vad skall hända när det kommer ett felmeddelande
            Toast.makeText(requireContext(),errorMess, Toast.LENGTH_LONG).show()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner, errorObserver)

        binding.loginButton.setOnClickListener {
            val userEmail = binding.loginEmailET.text.toString()
            val userPassword = binding.loginPasswordET.text.toString()

            viewModel.login(userEmail, userPassword)

            if (Firebase.auth.currentUser != null) {

                findNavController().navigate(R.id.action_loginFragment_to_startFragment)

            }
        }

        binding.registerButton.setOnClickListener {
            val userEmail = binding.loginEmailET.text.toString()
            val userPassword = binding.loginPasswordET.text.toString()

            viewModel.register(userEmail, userPassword)

            if (Firebase.auth.currentUser != null) {

                findNavController().navigate(R.id.action_loginFragment_to_startFragment)

            }

        }

    }

}