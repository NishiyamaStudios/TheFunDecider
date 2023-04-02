package se.nishiyamastudios.fundeciderproject.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
            }
    }

    fun register(email: String, password: String) {

        if (email == "") {
            errorMessage.value = "Fyll i epost"
            return
        }

        if (password == "") {
            errorMessage.value = "Fyll i lösenord"
            return
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                //eller task.isSuccessful == false
                if (!task.isSuccessful) {
                    errorMessage.value = task.exception!!.localizedMessage!!
                }
            }
    }
}