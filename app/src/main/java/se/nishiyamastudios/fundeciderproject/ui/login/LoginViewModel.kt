package se.nishiyamastudios.fundeciderproject.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun login(email : String, password : String) {

        if (email == "") {
            errorMessage.value = "Please enter your email."
            return
        }

        if (password == "") {
            errorMessage.value = "Please enter your password."
            return
        }

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    errorMessage.value = "Login not successfull."
                }
            }
    }

    fun register(email: String, password: String) {

        if (email == "") {
            errorMessage.value = "Please enter your email."
            return
        }

        if (password == "") {
            errorMessage.value = "Please enter a password."
            return
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    errorMessage.value = task.exception!!.localizedMessage!!
                }
            }
    }
}