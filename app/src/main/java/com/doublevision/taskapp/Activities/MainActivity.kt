package com.doublevision.taskapp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublevision.taskapp.R
import com.doublevision.taskapp.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
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
        toolbarInitializer(binding.toolbarMain)
    }

    fun toolbarInitializer(tool: MaterialToolbar) {
        setSupportActionBar(tool)  //adiciona support ao action bar
        supportActionBar?.apply {
            setIcon(R.drawable.img_logo)
        }
        //adiciona menu na barra
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId){
                        R.id.btn_arquivado -> Toast.makeText(this@MainActivity, "Botao de arquivo", Toast.LENGTH_SHORT)
                        R.id.btn_logout -> deslogarUsuario()
                    }
                    return true
                }

            }
        )
}
    fun criarTarefaClick(view: View){
        startActivity(Intent(this, CreateNewTaskActivity::class.java))
    }

    fun deslogarUsuario(){
        firebaseauth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}