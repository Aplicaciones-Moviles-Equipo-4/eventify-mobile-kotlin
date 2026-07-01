package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.*

private val Indigo = Color(0xFF2E2E8F)

// ============================ TASKS / KANBAN ============================

@Composable
fun TasksKanbanTab(eventId: String) {
    var selectedStatus by remember { mutableStateOf(TaskStatus.PENDIENTE) }
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<LocalTask?>(null) }

    val all = LocalStore.tasks.filter { it.eventId == eventId }
    val columns = listOf(
        TaskStatus.PENDIENTE to "Pendiente",
        TaskStatus.EN_PROGRESO to "En progreso",
        TaskStatus.COMPLETADA to "Completada"
    )

    if (showDialog || editing != null) {
        TaskDialog(
            task = editing,
            onDismiss = { showDialog = false; editing = null },
            onSave = { title, responsible, due, priority ->
                if (editing == null) {
                    LocalStore.addTask(LocalTask(eventId = eventId, title = title, responsible = responsible, dueDate = due, priority = priority, status = selectedStatus))
                } else {
                    LocalStore.updateTask(editing!!.copy(title = title, responsible = responsible, dueDate = due, priority = priority))
                }
                showDialog = false; editing = null
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Column selector (each acts as a Kanban column)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            columns.forEach { (status, label) ->
                val count = all.count { it.status == status }
                val selected = selectedStatus == status
                Surface(
                    color = if (selected) Indigo else Color(0xFFF0F0F5),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$count", fontWeight = FontWeight.Bold, color = if (selected) Color.White else Indigo, fontSize = 18.sp)
                        Text(label, fontSize = 10.sp, color = if (selected) Color.White else Color.Gray)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val tasks = all.filter { it.status == selectedStatus }
        if (tasks.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Sin tareas en esta columna", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tasks) { task ->
                    KanbanTaskCard(
                        task = task,
                        onEdit = { editing = task },
                        onDelete = { LocalStore.deleteTask(task.id) },
                        onMovePrev = LocalStore.prevStatus(task.status)?.let { prev -> { LocalStore.moveTask(task.id, prev) } },
                        onMoveNext = LocalStore.nextStatus(task.status)?.let { next -> { LocalStore.moveTask(task.id, next) } }
                    )
                }
            }
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Nueva tarea")
        }
    }
}

@Composable
private fun KanbanTaskCard(
    task: LocalTask,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMovePrev: (() -> Unit)?,
    onMoveNext: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black, modifier = Modifier.weight(1f))
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp), tint = Color.Gray)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(16.dp), tint = Color.Red)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriorityChip(task.priority)
                if (task.dueDate.isNotBlank()) {
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Text(" ${task.dueDate}", fontSize = 10.sp, color = Color.Gray)
                }
                if (task.responsible.isNotBlank()) {
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.size(26.dp).background(Indigo, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(task.responsible.take(2).uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onMovePrev != null) {
                    OutlinedButton(
                        onClick = onMovePrev,
                        modifier = Modifier.weight(1f).height(40.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Retroceder", modifier = Modifier.size(18.dp), tint = Indigo)
                    }
                }
                if (onMoveNext != null) {
                    Button(
                        onClick = onMoveNext,
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Avanzar", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: TaskPriority) {
    val (bg, fg, label) = when (priority) {
        TaskPriority.ALTA -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Alta")
        TaskPriority.MEDIA -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "Media")
        TaskPriority.BAJA -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Baja")
    }
    Surface(color = bg, shape = RoundedCornerShape(4.dp)) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = fg, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TaskDialog(
    task: LocalTask?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var responsible by remember { mutableStateOf(task?.responsible ?: "") }
    var due by remember { mutableStateOf(task?.dueDate ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: TaskPriority.MEDIA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true)
                OutlinedTextField(value = responsible, onValueChange = { responsible = it }, label = { Text("Responsable") }, singleLine = true)
                OutlinedTextField(value = due, onValueChange = { due = it }, label = { Text("Fecha límite (dd/mm)") }, singleLine = true)
                Text("Prioridad", fontSize = 12.sp, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(title, responsible, due, priority) }, enabled = title.isNotBlank()) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// ============================ CRONOGRAMA ============================

@Composable
fun CronogramaTab(eventId: String) {
    var showDialog by remember { mutableStateOf(false) }
    val activities = LocalStore.agenda.filter { it.eventId == eventId }.sortedBy { it.time }

    if (showDialog) {
        var time by remember { mutableStateOf("") }
        var title by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva actividad") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Hora (ej. 10:00)") }, singleLine = true)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Actividad") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { LocalStore.addActivity(AgendaActivity(eventId = eventId, time = time, title = title)); showDialog = false },
                    enabled = title.isNotBlank() && time.isNotBlank()
                ) { Text("Agregar") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (activities.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Aún no hay actividades en el cronograma", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(activities) { activity ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(activity.time, fontWeight = FontWeight.Bold, color = Indigo, modifier = Modifier.width(56.dp))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                activity.title,
                                fontSize = 14.sp,
                                color = if (activity.done) Color.Gray else Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = { LocalStore.toggleActivity(activity.id) }) {
                            Icon(
                                if (activity.done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Completar",
                                tint = if (activity.done) Color(0xFF2E7D32) else Color.LightGray
                            )
                        }
                        IconButton(onClick = { LocalStore.deleteActivity(activity.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(18.dp), tint = Color.Red)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                }
            }
        }
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar actividad")
        }
    }
}

// ============================ PRESUPUESTO ============================

@Composable
fun BudgetTab(eventId: String) {
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }

    val budget = LocalStore.budgetForEvent(eventId)
    val expenses = LocalStore.expenses.filter { it.eventId == eventId }
    val spent = expenses.sumOf { it.amount }
    val remaining = budget - spent

    if (showBudgetDialog) {
        var value by remember { mutableStateOf(if (budget > 0) budget.toInt().toString() else "") }
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Definir presupuesto") },
            text = {
                OutlinedTextField(
                    value = value, onValueChange = { value = it },
                    label = { Text("Monto total (S/)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { LocalStore.setBudget(eventId, value.toDoubleOrNull() ?: 0.0); showBudgetDialog = false }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showBudgetDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showExpenseDialog) {
        var desc by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Catering") }
        var amount by remember { mutableStateOf("") }
        val cats = listOf("Catering", "Logística", "Decoración", "Música", "Otros")
        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = { Text("Registrar gasto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, singleLine = true)
                    OutlinedTextField(
                        value = amount, onValueChange = { amount = it }, label = { Text("Monto (S/)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                    Text("Categoría", fontSize = 12.sp, color = Color.Gray)
                    cats.forEach { c ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { category = c }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = category == c, onClick = { category = c })
                            Text(c)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        LocalStore.addExpense(Expense(eventId = eventId, description = desc, category = category, amount = amount.toDoubleOrNull() ?: 0.0))
                        showExpenseDialog = false
                    },
                    enabled = desc.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
                ) { Text("Agregar") }
            },
            dismissButton = { TextButton(onClick = { showExpenseDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Indigo),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Presupuesto", color = Color.White.copy(alpha = 0.8f))
                    TextButton(onClick = { showBudgetDialog = true }) {
                        Text(if (budget > 0) "Editar" else "Definir", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Text("S/ ${budget.toInt()}", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Gastado", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp); Text("S/ ${spent.toInt()}", color = Color.White, fontWeight = FontWeight.Bold) }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Saldo", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("S/ ${remaining.toInt()}", color = if (remaining < 0) Color(0xFFFFCDD2) else Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Gastos por categoría", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            IconButton(onClick = { showExpenseDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Agregar gasto", tint = Indigo) }
        }

        if (expenses.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Aún no registras gastos", color = Color.Gray)
            }
        } else {
            val grouped = expenses.groupBy { it.category }
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                grouped.forEach { (cat, items) ->
                    item {
                        Text(
                            "$cat  ·  S/ ${items.sumOf { it.amount }.toInt()}",
                            fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Indigo,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(items) { e ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(e.description, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Text("S/ ${e.amount.toInt()}", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { LocalStore.deleteExpense(e.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(16.dp), tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================ helpers ============================

/** Small helper: status ordering for the Kanban move buttons. */
private fun LocalStore.nextStatus(status: TaskStatus): TaskStatus? = when (status) {
    TaskStatus.PENDIENTE -> TaskStatus.EN_PROGRESO
    TaskStatus.EN_PROGRESO -> TaskStatus.COMPLETADA
    TaskStatus.COMPLETADA -> null
}

private fun LocalStore.prevStatus(status: TaskStatus): TaskStatus? = when (status) {
    TaskStatus.PENDIENTE -> null
    TaskStatus.EN_PROGRESO -> TaskStatus.PENDIENTE
    TaskStatus.COMPLETADA -> TaskStatus.EN_PROGRESO
}
