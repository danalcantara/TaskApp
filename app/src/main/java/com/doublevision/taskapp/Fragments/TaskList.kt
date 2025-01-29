package com.doublevision.taskapp.Fragments

import TaskAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
//import com.doublevision.taskapp.Adapter.TaskListAdapter
import com.doublevision.taskapp.Model.Task
import com.doublevision.taskapp.R
import com.doublevision.taskapp.databinding.FragmentTaskListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import java.util.Date

class TaskList : Fragment() {
    private var _binding : FragmentTaskListBinding? = null
    private val binding get() = _binding
    lateinit var task_adapter:TaskAdapter
    private var listaTask:MutableList<Task> = mutableListOf()

    val firebasestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }
    lateinit var eventSnapshot: ListenerRegistration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        recuperarTask()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        task_adapter = TaskAdapter{
            task ->

            Toast.makeText(requireContext(),"Clicado com sucesso", Toast.LENGTH_SHORT).show()

        }
        binding?.rcListTask?.adapter = task_adapter
        binding?.rcListTask?.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun recuperarTask(){

        var id_usuario = firebaseauth.currentUser?.uid
        if (id_usuario != null) {
            eventSnapshot = firebasestore.collection("task").document(id_usuario).collection("task_disponivel")
                    .addSnapshotListener { task, e ->
                        if (e != null){
                            return@addSnapshotListener
                        }

                        listaTask.clear()
                    if (task != null) {
                        for (document in task.documents) {
                            document.toObject(Task::class.java)?.let { listaTask.add(it) }

                            Toast.makeText(requireContext(),"Sucesso ao recuperar task", Toast.LENGTH_SHORT)

                        }
                        if (listaTask.isNotEmpty()){
                            task_adapter.adicionaItensALista(listaTask)
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        eventSnapshot?.remove()
    }
}