package com.example.bookxpert.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.bookxpert.presentation.home.HomeActivity
import com.example.bookxpert.R
import com.example.bookxpert.data.local.database.AppDatabase
import com.example.bookxpert.databinding.ActivityMainBinding
import com.example.bookxpert.domain.repository.AuthRepository
import com.example.bookxpert.domain.usecase.GetCurrentUserUseCase
import com.example.bookxpert.domain.usecase.SignInWithGoogleUseCase
import com.example.bookxpert.domain.usecase.SignOutUseCase
import com.example.bookxpert.domain.model.User
import com.example.bookxpert.presentation.auth.viewmodel.AuthState
import com.example.bookxpert.presentation.auth.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Manual Dependency Creation
        val authRepository = AuthRepository(auth, AppDatabase.getDatabase(this).userDao())
        val signInUseCase = SignInWithGoogleUseCase(authRepository)
        val signOutUseCase = SignOutUseCase(authRepository)
        val getUserUseCase = GetCurrentUserUseCase(authRepository)

        // ViewModel Factory
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(signInUseCase, signOutUseCase, getUserUseCase) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]

        setupObservers()
        setupClickListeners()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModel.getCurrentUser(currentUser.uid)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.authState.collectLatest { state ->
                when (state) {
                    is AuthState.Loading -> showLoading()
                    is AuthState.Success -> showSuccess(state.user)
                    is AuthState.Error -> showError(state.message)
                    is AuthState.SignedOut -> showSignedOut()
                    is AuthState.Idle -> showIdle()
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.text = "Loading..."
    }

    private fun showSuccess(user: User) {
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = "Welcome, ${user.name}"

        // Navigate to home screen
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("USER_NAME", user.name)
        startActivity(intent)
        finish()
    }



    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = "Error: $message"
    }

    private fun showSignedOut() {
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = "Signed out"
    }

    private fun showIdle() {
        binding.progressBar.visibility = View.GONE
        binding.tvStatus.text = "Please sign in"
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            signIn()
        }

        /*
    binding.btnSignOut.setOnClickListener {
        viewModel.signOut()
    }*/

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { idToken ->
                    viewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                binding.tvStatus.text = "Google sign in failed: ${e.message}"
            }
        }
    }
}