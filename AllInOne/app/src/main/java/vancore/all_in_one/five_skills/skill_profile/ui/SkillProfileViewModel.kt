package vancore.all_in_one.five_skills.skill_profile.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import vancore.all_in_one.five_skills.skill_profile.data.SkillProfileRepository
import vancore.all_in_one.five_skills.skill_profile.data.models.LoginValidation
import javax.inject.Inject

class SkillProfileViewModel @Inject constructor(
    private val profileRepository: SkillProfileRepository
) : ViewModel() {

    var currentUser: FirebaseUser? = null

    private val _isUserOnline: MutableLiveData<FirebaseUser> = MutableLiveData()
    val isUserOnline: LiveData<FirebaseUser>
        get() = _isUserOnline

    private val _inputValidation: MutableLiveData<LoginValidation> = MutableLiveData()
    val inputValidation: LiveData<LoginValidation>
        get() = _inputValidation

    fun doSomething() {
        profileRepository.profileMethod()
    }

    fun checkIfUserIsOnline(auth: FirebaseAuth) {
        profileRepository.profileMethod()
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = auth.currentUser
        if (currentUser != null) {
            _isUserOnline.postValue(currentUser)
        } else {
            _isUserOnline.postValue(currentUser)
        }
    }

    fun onLoginSuccessful(user: FirebaseUser?) {
        currentUser = user
        _isUserOnline.postValue(currentUser)
    }

    fun onLoginFailed() {
        _isUserOnline.postValue(currentUser)
    }

    fun onSignOut() {
        currentUser = null
        _isUserOnline.postValue(currentUser)
    }

    fun onUserCreationSuccessFully(user: FirebaseUser?) {
        currentUser = user
        _isUserOnline.postValue(currentUser)
    }

    fun onUserCreationFailed() {
        //_isUserOnline.postValue()
    }

    fun validateInput(email: String, password: String) {
        var validation = when {
            email.isEmpty() && password.isEmpty() -> LoginValidation.InvalidEmailAndPassword
            email.isEmpty() -> LoginValidation.InvalidEmail
            password.isEmpty() -> LoginValidation.InvalidPassword
            else -> LoginValidation.Valid
        }
        _inputValidation.postValue(validation)
    }
}