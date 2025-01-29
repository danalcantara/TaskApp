import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.doublevision.taskapp.Model.Task
import com.doublevision.taskapp.databinding.ItemReciclerTaskListBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(val taskbody : (Task)->Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val taskList = mutableListOf<Task>()

    fun adicionaItensALista(lista: List<Task>) {
        taskList.clear()
        taskList.addAll(lista)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(private val binding: ItemReciclerTaskListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task:Task){
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.titleTask.text = task.titulo
        binding.dateField.text = task.data_limite?.let { dateFormat.format(it) } ?: "Sem data"

            binding.checkBoxTask.setOnClickListener {
                taskbody(task)
            }
            binding.cardTasks.setOnClickListener {
                taskbody(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemReciclerTaskListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.bind(task)

    }

    override fun getItemCount(): Int = taskList.size
}
