package com.doublevision.taskapp.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublevision.taskapp.Model.Task
import com.doublevision.taskapp.R
import com.doublevision.taskapp.databinding.ActivityCreateNewTaskBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class CreateNewTaskActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityCreateNewTaskBinding.inflate(layoutInflater)
    }
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebasestore by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var task: Task
    private lateinit var textInputEditTextDate: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        eventosDeClick()
        textInputEditTextDate = binding.inputDate

        // Adiciona um TextWatcher para formatar a data
        textInputEditTextDate.addTextChangedListener(dateTextWatcher)
    }

    private fun eventosDeClick() {
        binding.btnVoltar.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnCriarTarefa.setOnClickListener{
            if (verificaCampos()){
                task = Task(binding.inputTitulo.text.toString(), binding.inputDescricao.text.toString(),if(!(binding.inputDate.text.isNullOrBlank())){ Date(binding.inputDate.text.toString())}else{null})
                criarTaskNoBanco()

            }
        }
    }

    private fun criarTaskNoBanco() {
        var id_usuario = firebaseauth.currentUser?.uid
        var campos = mapOf(
            "titulo" to task.titulo,
            "descricao" to task.descricao,
            "data_limite" to task.data_limite,
            "status_conclusao" to task.status_conclusao,
            "dataCriacao" to task.dataCriacao,

        )
        if (id_usuario != null){
            firebasestore.collection("task").document(id_usuario).collection("task_disponivel").add(campos).addOnSuccessListener {
                Toast.makeText(this, "Criado nova task", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }


    fun verificaCampos(): Boolean {
        var camposValidos = true

        if (!binding.containerTitle.isEnabled ||
            !binding.containerDescription.isEnabled ||
            !binding.inputDate.isEnabled

        ) {
            camposValidos = false
        }


        if (binding.inputTitulo.text.isNullOrBlank() || binding.inputTitulo.text?.length!! < 4) {
            binding.containerTitle.error = "Titulo e obrigatorio e deve ter mais de 4 caracteres"
            camposValidos = false
        }

        return camposValidos
    }

    private val dateTextWatcher = object : TextWatcher {
        private var current = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                textInputEditTextDate.removeTextChangedListener(this)
                val formattedDate = formatDate(s.toString())
                current = formattedDate
                textInputEditTextDate.setText(formattedDate)
                textInputEditTextDate.setSelection(formattedDate.length)
                textInputEditTextDate.addTextChangedListener(this)
            }
        }

        private fun formatDate(input: String): String {
            val cleanedInput = input.replace(Regex("[^\\d]"), "")
            val stringBuilder = StringBuilder()

            for (i in cleanedInput.indices) {
                if (i == 2 || i == 4) {
                    stringBuilder.append('/')
                }
                if (i >= 8) break
                stringBuilder.append(cleanedInput[i])
            }

            return stringBuilder.toString()
        }
    }
}
