package se.nishiyamastudios.fundeciderproject.ui.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentHelpBinding
import se.nishiyamastudios.fundeciderproject.ui.login.LoginFragment
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility

class HelpFragment : Fragment() {

    private var _binding : FragmentHelpBinding? = null
    private val binding get() = _binding!!
    private val fbUtil = FirebaseUtility()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snackbarMessage: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        val snackbarObserver = Observer<String> { mess ->
            Snackbar.make(requireView(), mess, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.helpCL)
                .show()
        }

        snackbarMessage.observe(viewLifecycleOwner, snackbarObserver)


        binding.closeHelpInfoImage.setOnClickListener {
            binding.deleteAccountAreYouSureButton.visibility = View.GONE
            binding.savedDataTV.visibility = View.GONE
            binding.deleteAccountButton.visibility = View.VISIBLE
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.deleteAccountButton.setOnClickListener {

            binding.deleteAccountButton.visibility = View.GONE
            binding.savedDataTV.visibility = View.VISIBLE
            binding.deleteAccountAreYouSureButton.visibility = View.VISIBLE
        }

        binding.deleteAccountAreYouSureButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {

                // Delete user saved data from funfavorite and funblacklist
                fbUtil.deleteSavedUserData()

                val activity  = it.context as? AppCompatActivity
                val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

                // Re-authenticate user before delete in case it is needed
                val credential = EmailAuthProvider.getCredential(R.id.loginEmailET.toString(), R.id.loginPasswordET.toString())
                currentUser.reauthenticate(credential)

                // Delete user account and navigate to login
                currentUser.delete().addOnSuccessListener {

                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragNavCon, LoginFragment())?.commit()
                    if (bottomNavigationView != null) {
                        bottomNavigationView.menu.findItem(R.id.startPage).isChecked = true
                    }
                    if (bottomNavigationView != null) {
                        bottomNavigationView.visibility = View.GONE
                    }
                }
            } else {
                snackbarMessage.value = "Could not find any user."
            }
        }


    }


}