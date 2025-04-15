package com.example.bookxpert.presentation.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bookxpert.R
import com.example.bookxpert.data.local.database.AppDatabase
import com.example.bookxpert.domain.repository.AuthRepository
import com.example.bookxpert.domain.model.User
import com.example.bookxpert.presentation.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

class SplashScreen : AppCompatActivity() {

    private var user: User? = null
    private var intent: Intent? = null
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authRepository = AuthRepository(
            FirebaseAuth.getInstance(),
            AppDatabase.getDatabase(this).userDao()
        )

        lifecycleScope.launch {
            val currentUser = authRepository.getCurrentUser(FirebaseAuth.getInstance().currentUser?.uid ?: "").firstOrNull()

            Handler().postDelayed({
                if (currentUser?.isLoggedIn == true) {
                    intent = Intent(this@SplashScreen, HomeActivity::class.java)
                } else {
                    intent = Intent(this@SplashScreen, LoginActivity::class.java)
                }
                startActivity(intent)
                finish()
            }, 3000)
        }
    }
}