package se.nishiyamastudios.fundeciderproject.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.R

class LoginViewModel : ViewModel() {

    //live data till felmeddelande som vi kan lyssna på i LoginFragment
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun login(email : String, password : String) {

        if (email == "") {
            errorMessage.value = "Fyll i epost"
            return //funktionen är klar, kör inte längre
        }

        if (password == "") {
            errorMessage.value = "Fyll i lösenord"
            return //funktionen är klar, kör inte längre
        }

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    errorMessage.value = "Fel inloggning"
                }

                /*
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Toast.makeText(requireContext(), "Login ok.", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    //Toast.makeText(requireContext(), "Login failed.", Toast.LENGTH_SHORT).show()
                    errorMessage.value = "Fel inloggning"
                }
            }
                 */
            }
    }

    fun register(email: String, password: String) {

        if (email == "") {
            errorMessage.value = "Fyll i epost"
            return //funktionen är klar, kör inte längre
        }

        if (password == "") {
            errorMessage.value = "Fyll i lösenord"
            return //funktionen är klar, kör inte längre
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                //eller task.isSuccessful == false
                if (!task.isSuccessful) {
                    errorMessage.value = task.exception!!.localizedMessage!!
                }


                /*
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                //Toast.makeText(requireContext(), "Register ok.", Toast.LENGTH_SHORT).show()
            } else {
                // If sign in fails, display a message to the user.
                //Toast.makeText(requireContext(), "Register failed.", Toast.LENGTH_SHORT).show()
                //errorMessage.value = "Fel registrering"

                errorMessage.value = task.exception!!.localizedMessage!!

                /*
                Log.i("pia11debug",task.exception!!.message!!)
                Log.i("pia11debug",task.exception!!.localizedMessage!!)

                 */

             */

            }
    }
}