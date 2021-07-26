package vancore.all_in_one.five_skills.skill_profile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.all_in_one.R
import com.example.all_in_one.databinding.FragmentSkillProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import vancore.all_in_one.five_skills.extensions.hideKeyboard
import javax.inject.Inject


@AndroidEntryPoint
class SkillProfileFragment : Fragment() {

    private var _binding: FragmentSkillProfileBinding? = null
    private val binding get() = _binding!!

    private var currentSnackbar: Snackbar? = null

    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var viewModel: SkillProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSkillProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        viewModel.isUserOnline.observe(viewLifecycleOwner, { user ->
            binding.bLogin.isVisible = user == null
            binding.bRegister.isVisible = user == null
            binding.bLogout.isVisible = user != null

            if (user != null) {
                // What is user.displayName?
                binding.tvLogin.text = getString(R.string.profile_login_logged_in, user.email)
                binding.tietAccountName.visibility = View.GONE
                binding.tilPassword.visibility = View.GONE
            } else {
                binding.tvLogin.text = getString(R.string.profile_login_logged_out)
                binding.tietAccountName.visibility = View.VISIBLE
                binding.tilPassword.visibility = View.VISIBLE
            }
        })

        binding.bLogin.setOnClickListener {
            signIn(binding.tietAccountName.text.toString(), binding.tietPassword.text.toString())
            requireActivity().hideKeyboard()
        }

        binding.bLogout.setOnClickListener {
            signOut()
        }

        binding.bRegister.setOnClickListener {
            createUserWithMailAndPassword(
                binding.tietAccountName.text.toString(),
                binding.tietPassword.text.toString()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.doSomething()
        viewModel.checkIfUserIsOnline(auth)
    }

    private fun signIn(email: String, password: String) {
        // ToDo: Email and Password validation
        val drawable = CircularProgressDrawable(requireContext())
        binding.bLogin.setCompoundDrawables(drawable, null, null, null)
        drawable.start()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                drawable.stop()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")
                    val user = auth.currentUser
                    // Update from ViewModel
                    viewModel.onLoginSuccessful(user)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    showSnackBar(task.exception?.localizedMessage)
                    // Update from ViewModel
                    viewModel.onLoginFailed()
                    //updateUI(user)
                }
            }
    }

    private fun signOut() {
        auth.signOut()
        val drawable = CircularProgressDrawable(requireContext())
        binding.bLogout.setCompoundDrawables(drawable, null, null, null)
        drawable.start()
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                viewModel.onSignOut()
                drawable.stop()
            }
        }
    }

    private fun createUserWithMailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    // Update from ViewModel
                    viewModel.onUserCreationSuccessFully(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    //Update from ViewModel
                    //updateUI(null)
                    viewModel.onUserCreationFailed()
                }
            }
    }

    private fun showSnackBar(text: String?) {
        dismissCurrentErrorSnackbar()
        (view?.findViewById(R.id.snackbar_container) ?: view)?.let { container ->
            val backgroundColor = ResourcesCompat.getColor(
                resources,
                R.color.snackbar_error,
                context?.theme
            )
            val snackbar = Snackbar.make(container, text ?: "Error", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(backgroundColor)

            val snackbarView = snackbar.view
            val textView =
                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.maxLines = 3
            snackbar.show()
            currentSnackbar = snackbar
        }
    }

    private fun dismissCurrentErrorSnackbar() {
        if (currentSnackbar?.isShownOrQueued == true) {
            currentSnackbar?.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}