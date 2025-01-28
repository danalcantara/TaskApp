package com.doublevision.taskapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doublevision.taskapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        createNotificationChannel()
        if ( firebaseauth.currentUser?.uid != null){
            startActivity(Intent(this ,MainActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        createNotificationChannel()
    }
    fun validarCampos(): Boolean {
        var camposValidos = true

        if (binding.inputEmailLogin.text.isNullOrBlank()) {
            binding.EmailLogin.error = "O e-mail é obrigatório"
            camposValidos = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmailLogin.text.toString())
                .matches()
        ) {
            binding.EmailLogin.error = "E-mail inválido"
            camposValidos = false
        }

        if (binding.inputSenhaLogin.text.isNullOrBlank()) {
            binding.PasswordLogin.error = "A senha é obrigatória"
            camposValidos = false
        } else if (binding.inputSenhaLogin.text.toString().length < 6) {
            binding.PasswordLogin.error = "A senha deve ter pelo menos 6 caracteres"
            camposValidos = false
        }

        return camposValidos
    }
    fun desabilitarCampos(){
        if(binding.EmailLogin.isEnabled) {
            binding.EmailLogin.isEnabled = false
            binding.PasswordLogin.isEnabled = false
            binding.btnLogin.isEnabled = false
        }
    }

    fun habilitarCampos(){
        if(!binding.EmailLogin.isEnabled) {
            binding.EmailLogin.isEnabled = true
            binding.PasswordLogin.isEnabled = true
            binding.btnLogin.isEnabled = true
        }
    }

    fun LogarUsuario(view: View) {
        if (validarCampos()){
            desabilitarCampos()
            ativaAnimacaoCarregamento()
            lifecycleScope.launch {
                var usuarioLogado = logarUsuarioVerificacao()
                if (usuarioLogado){
                    pausaAnimacaoCarregamento()
                    habilitarCampos()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    habilitarCampos()
                    pausaAnimacaoCarregamento()
                }
            }

        } else {
            habilitarCampos()
            pausaAnimacaoCarregamento()
        }
    }
     private  suspend fun logarUsuarioVerificacao(): Boolean = suspendCancellableCoroutine { continuable ->
         firebaseauth.signInWithEmailAndPassword(
             binding.inputEmailLogin.text.toString(),
             binding.inputSenhaLogin.text.toString()
         ).addOnSuccessListener {
             Toast.makeText(this@LoginActivity, "Login Efetuado", Toast.LENGTH_SHORT)

             continuable.resume(true)

         }.addOnFailureListener {
             Toast.makeText(this@LoginActivity, "Algo deu Errado no Login", Toast.LENGTH_SHORT)

             continuable.resume(false)
         }
     }
    fun navegarParaRegistro(view:View){
        startActivity(
            Intent(this, RegisterActivity::class.java)
        )
    }

    fun ativaAnimacaoCarregamento() {
        binding.loadingAnimationLogin.visibility = View.VISIBLE

        binding.loadingAnimationLogin.playAnimation()

    }

    fun pausaAnimacaoCarregamento() {
        binding.loadingAnimationLogin.visibility = View.INVISIBLE

        binding.loadingAnimationLogin.pauseAnimation()

    }
    fun createNotification(){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "login_not")
            .setSmallIcon(R.drawable.img_logo).setContentTitle("Tentando Logar")
            .setContentText("Alguem apertou em login cara")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentIntent(pendingIntent).setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@LoginActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@LoginActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101 // Código de solicitação
                )
                return@with

            }
            var unique_id = System.currentTimeMillis().toInt()
            notify(unique_id, builder.build())
        }
    }
    private fun createNotificationChannel():Unit {
        var id_channel = "login_not"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nome = "Notificações de Login"
            val descricaoTexto = "Notificações relacionadas às tentativas de login."
            val importancia = NotificationManager.IMPORTANCE_HIGH
            var channel = NotificationChannel(id_channel, nome, importancia).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = android.graphics.Color.RED
                description = descricaoTexto
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }
}