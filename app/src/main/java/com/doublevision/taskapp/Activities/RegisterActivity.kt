package com.doublevision.taskapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.doublevision.taskapp.Api.RetrofitHelper
import com.doublevision.taskapp.Constants.Constants
import com.doublevision.taskapp.Interfaces.IEnderecoAPI
import com.doublevision.taskapp.Model.Endereco
import com.doublevision.taskapp.R
import com.doublevision.taskapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class RegisterActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    val retrofit = RetrofitHelper.retrofit

    val firebasestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }
    lateinit var usuarioDadosEndereco: Endereco

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        desativarPorCampoPorPadrao()


    }

    fun desativarPorCampoPorPadrao() {
        var endereco = findViewById<EditText>(R.id.inputEndereco)
        endereco.isEnabled = false
    }

    fun ativaAnimacaoCarregamento() {
        binding.loadingAnimation.visibility = View.VISIBLE

        binding.loadingAnimation.playAnimation()

    }

    fun pausaAnimacaoCarregamento() {
        binding.loadingAnimation.visibility = View.INVISIBLE

        binding.loadingAnimation.pauseAnimation()

    }

    fun desabilitarCampos() {
        if (binding.CepRegister.isEnabled) {
            binding.CepRegister.isEnabled = false
            binding.NomeRegister.isEnabled = false
            binding.EmailRegister.isEnabled = false
            binding.PasswordRegister.isEnabled = false
            binding.PasswordRegisterConfirm.isEnabled = false
            binding.btnRegister.isEnabled = false
            binding.btnProcurarcep.isEnabled = false
        }
    }

    fun validarCampos(): Boolean {
        var camposValidos = true


        if (!binding.CepRegister.isEnabled ||
            !binding.NomeRegister.isEnabled ||
            !binding.EmailRegister.isEnabled ||
            !binding.PasswordRegister.isEnabled ||
            !binding.PasswordRegisterConfirm.isEnabled ||
            !binding.btnRegister.isEnabled ||
            !binding.btnProcurarcep.isEnabled
        ) {
            camposValidos = false
        }


        if (binding.inputCep.text.isNullOrBlank()) {
            binding.CepRegister.error = "O CEP é obrigatório"
            camposValidos = false
        }
        if (binding.inputNome.text.isNullOrBlank()) {
            binding.NomeRegister.error = "O nome é obrigatório"
            camposValidos = false
        }
        if (binding.inputEmail.text.isNullOrBlank()) {
            binding.EmailRegister.error = "O e-mail é obrigatório"
            camposValidos = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString())
                .matches()
        ) {
            binding.EmailRegister.error = "E-mail inválido"
            camposValidos = false
        }
        if (binding.inputSenha.text.isNullOrBlank()) {
            binding.PasswordRegister.error = "A senha é obrigatória"
            camposValidos = false
        } else if (binding.inputConfirmSenha.text.toString().length < 6) {
            binding.PasswordRegister.error = "A senha deve ter pelo menos 6 caracteres"
            camposValidos = false
        }
        if (binding.inputConfirmSenha.text.isNullOrBlank()) {
            binding.PasswordRegisterConfirm.error = "A confirmação da senha é obrigatória"
            camposValidos = false
        } else if (binding.inputConfirmSenha.text.toString() != binding.inputSenha.text.toString()) {
            binding.PasswordRegisterConfirm.error = "As senhas não coincidem"
            camposValidos = false
        }
        if (binding.inputEndereco.text.toString() == "Cep Invalido" || binding.inputEndereco.text.toString()
                .isNullOrBlank()
        ) {
            camposValidos = false
        }

        return camposValidos
    }

    fun habilitarCampos() {
        if (!binding.CepRegister.isEnabled) {
            binding.CepRegister.isEnabled = true
            binding.NomeRegister.isEnabled = true
            binding.EmailRegister.isEnabled = true
            binding.PasswordRegister.isEnabled = true
            binding.PasswordRegisterConfirm.isEnabled = true
            binding.btnRegister.isEnabled = true
            binding.btnProcurarcep.isEnabled = true
        }
    }

    fun adicionaEnderecoPeloCep(view: View) {
        desabilitarCampos()
        ativaAnimacaoCarregamento()
        lifecycleScope.launch {
            try {
                var retro = retrofit.create(IEnderecoAPI::class.java)

                var retorno = retro.getEndereco(binding.inputCep.text.toString())
                if (retorno.isSuccessful) {
                    val corpo = retorno.body()
                    if (corpo != null) {
                        usuarioDadosEndereco = corpo
                        withContext(Dispatchers.Main) {
                            if (corpo?.bairro != null) {
                                Log.i("log", "Deu Certo")
                                binding.inputEndereco.setText("${corpo?.bairro}")
                                pausaAnimacaoCarregamento()
                                habilitarCampos()
                            } else {

                                binding.inputEndereco.setText("Cep Invalido")
                                pausaAnimacaoCarregamento()
                                habilitarCampos()
                            }
                        }
                    }

                } else {
                    pausaAnimacaoCarregamento()
                    habilitarCampos()
                    binding.inputEndereco.setText("Cep Invalido")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                pausaAnimacaoCarregamento()
                habilitarCampos()
            }
        }


    }
    suspend fun verificarEmailExistente(email: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext verificarEmailExistenteBack(email)
    }
    private suspend fun verificarEmailExistenteBack(email: String): Boolean = suspendCancellableCoroutine withContext@{ continuable ->
        try {
            val querySnapshot = firebasestore.collection(Constants.COLECAO_USUARIO)
                .whereEqualTo("email", email)
                .get().addOnSuccessListener {  querySnapshot ->

                    continuable.resume(!querySnapshot.isEmpty)
                }

        } catch (e: Exception) {
            Log.e("VerificaçãoEmail", "Erro ao verificar e-mail: ${e.message}")
            continuable.resume(false)

        }
    }

    suspend fun salvarDadosBanco(uid: String?): Boolean =
        suspendCancellableCoroutine { continuable ->
            if (uid != null) {
                var dadosUsuario = mapOf(
                    "id" to uid,
                    "nome" to binding.inputNome.text.toString(),
                    "email" to binding.inputEmail.text.toString(),
                    "cep" to usuarioDadosEndereco.cep,
                    "localidade" to usuarioDadosEndereco.localidade,
                    "regiao" to usuarioDadosEndereco.regiao,
                    "ddd" to usuarioDadosEndereco.ddd,
                    "estado" to usuarioDadosEndereco.estado,
                )
                firebasestore.collection(Constants.COLECAO_USUARIO).document(uid)
                    .collection(Constants.DADOS_USUARIO).add(dadosUsuario).addOnSuccessListener {
                        Toast.makeText(this, "Dados Salvos", Toast.LENGTH_SHORT).show()
                        continuable.resume(true)
                    }.addOnFailureListener {
                        continuable.resume(false)
                        Toast.makeText(
                            this,
                            "Algo deu Errado no Salvamento de Dados",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }

            }
        }

    suspend fun salvarUsuario(): Boolean = suspendCancellableCoroutine { continuable ->
        firebaseauth.createUserWithEmailAndPassword(
            binding.inputEmail.text.toString(),
            binding.inputSenha.text.toString()
        ).addOnSuccessListener { usuario ->

            Toast.makeText(this, "Usuario Criado", Toast.LENGTH_SHORT).show()
            continuable.resume(true)
        }.addOnFailureListener {
            Toast.makeText(this, "Algo deu errado na criação do Usuario", Toast.LENGTH_SHORT).show()
            continuable.resume(false)
        }
    }

    fun salvarUsuarioNovo(view: View) {
        Log.i("erro", "Entrou no salvar")

        if (validarCampos()) {
            desabilitarCampos()
            ativaAnimacaoCarregamento()
            lifecycleScope.launch {
                Log.i("erro", "Campos validados")

                val emailExistente = verificarEmailExistente(binding.inputEmail.text.toString())
                if (emailExistente) {
                    withContext(Dispatchers.Main) {
                        habilitarCampos()
                        pausaAnimacaoCarregamento()
                        Toast.makeText(
                            this@RegisterActivity,
                            "E-mail já cadastrado. Use outro ou tente recuperar sua senha.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val validacao = salvarUsuario()
                    if (validacao) {
                        val validaDB = salvarDadosBanco(firebaseauth.currentUser?.uid)
                        if (validaDB) {
                            withContext(Dispatchers.Main) {
                                habilitarCampos()
                                pausaAnimacaoCarregamento()
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Parabéns por criar sua Conta",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            }
                        } else {
                            habilitarCampos()
                            pausaAnimacaoCarregamento()
                        }
                    } else {
                        habilitarCampos()
                        pausaAnimacaoCarregamento()
                    }
                }
            }
        } else {
            habilitarCampos()
            pausaAnimacaoCarregamento()
        }
    }
}
