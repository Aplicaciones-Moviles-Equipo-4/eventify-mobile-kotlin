package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Lightweight local persistence for the modules that (for now) live only on the device:
 * tasks (Kanban), budget/expenses, event agenda, chat and notifications.
 *
 * It uses SharedPreferences + Gson (already available through the Retrofit Gson converter),
 * so no extra dependency or build change is required. Each collection is exposed as an
 * observable [SnapshotStateList] so Compose screens recompose automatically on mutation,
 * and every mutation is persisted immediately.
 *
 * These modules are intentionally backend-agnostic: promoting any of them to a REST resource
 * later only requires swapping the persist/load calls for API calls.
 */
object LocalStore {

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private var seeded = false

    val tasks: SnapshotStateList<LocalTask> = mutableStateListOf()
    val expenses: SnapshotStateList<Expense> = mutableStateListOf()
    val budgets: SnapshotStateList<EventBudget> = mutableStateListOf()
    val agenda: SnapshotStateList<AgendaActivity> = mutableStateListOf()
    val messages: SnapshotStateList<ChatMessage> = mutableStateListOf()
    val notifications: SnapshotStateList<AppNotification> = mutableStateListOf()

    private val _subscription = mutableStateOf(Subscription())
    val subscription: Subscription get() = _subscription.value

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences("eventify_local", Context.MODE_PRIVATE)
        loadAll()
        if (!seeded && prefs.getBoolean("seeded", false).not()) {
            seedDemoData()
            prefs.edit().putBoolean("seeded", true).apply()
            seeded = true
        }
    }

    // ---- Generic helpers -------------------------------------------------

    private inline fun <reified T> loadList(key: String): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = TypeToken.getParameterized(List::class.java, T::class.java).type
        return runCatching { gson.fromJson<List<T>>(json, type) }.getOrDefault(emptyList())
    }

    private fun persist(key: String, value: Any) {
        prefs.edit().putString(key, gson.toJson(value)).apply()
    }

    private fun loadAll() {
        tasks.addAll(loadList<LocalTask>("tasks"))
        expenses.addAll(loadList<Expense>("expenses"))
        budgets.addAll(loadList<EventBudget>("budgets"))
        agenda.addAll(loadList<AgendaActivity>("agenda"))
        messages.addAll(loadList<ChatMessage>("messages"))
        notifications.addAll(loadList<AppNotification>("notifications"))
        prefs.getString("subscription", null)?.let {
            runCatching { gson.fromJson(it, Subscription::class.java) }.getOrNull()?.let { s -> _subscription.value = s }
        }
    }

    private fun newId(): String = "local_" + java.util.UUID.randomUUID().toString().take(8)

    // ---- Tasks -----------------------------------------------------------

    fun addTask(task: LocalTask) {
        tasks.add(task.copy(id = newId()))
        persist("tasks", tasks.toList())
        pushNotification("Nueva tarea", task.title, NotificationType.TASK)
    }

    fun updateTask(task: LocalTask) {
        val idx = tasks.indexOfFirst { it.id == task.id }
        if (idx >= 0) {
            tasks[idx] = task
            persist("tasks", tasks.toList())
        }
    }

    fun moveTask(taskId: String, status: TaskStatus) {
        val idx = tasks.indexOfFirst { it.id == taskId }
        if (idx >= 0) {
            tasks[idx] = tasks[idx].copy(status = status)
            persist("tasks", tasks.toList())
        }
    }

    fun deleteTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
        persist("tasks", tasks.toList())
    }

    fun tasksForEvent(eventId: String): List<LocalTask> = tasks.filter { it.eventId == eventId }

    // ---- Budget & expenses ----------------------------------------------

    fun setBudget(eventId: String, total: Double) {
        val idx = budgets.indexOfFirst { it.eventId == eventId }
        if (idx >= 0) budgets[idx] = budgets[idx].copy(total = total)
        else budgets.add(EventBudget(eventId, total))
        persist("budgets", budgets.toList())
    }

    fun budgetForEvent(eventId: String): Double = budgets.firstOrNull { it.eventId == eventId }?.total ?: 0.0

    fun addExpense(expense: Expense) {
        expenses.add(expense.copy(id = newId()))
        persist("expenses", expenses.toList())
    }

    fun deleteExpense(expenseId: String) {
        expenses.removeAll { it.id == expenseId }
        persist("expenses", expenses.toList())
    }

    fun expensesForEvent(eventId: String): List<Expense> = expenses.filter { it.eventId == eventId }

    fun spentForEvent(eventId: String): Double = expensesForEvent(eventId).sumOf { it.amount }

    // ---- Agenda / cronograma --------------------------------------------

    fun addActivity(activity: AgendaActivity) {
        agenda.add(activity.copy(id = newId()))
        persist("agenda", agenda.toList())
    }

    fun toggleActivity(activityId: String) {
        val idx = agenda.indexOfFirst { it.id == activityId }
        if (idx >= 0) {
            agenda[idx] = agenda[idx].copy(done = !agenda[idx].done)
            persist("agenda", agenda.toList())
        }
    }

    fun deleteActivity(activityId: String) {
        agenda.removeAll { it.id == activityId }
        persist("agenda", agenda.toList())
    }

    fun agendaForEvent(eventId: String): List<AgendaActivity> =
        agenda.filter { it.eventId == eventId }.sortedBy { it.time }

    // ---- Chat ------------------------------------------------------------

    fun conversations(): List<ChatContact> =
        messages.groupBy { it.contactName }
            .map { (name, msgs) ->
                val last = msgs.maxByOrNull { it.timestamp }
                ChatContact(
                    contactName = name,
                    lastMessage = last?.text ?: "",
                    lastTimestamp = last?.timestamp ?: 0L,
                    unread = msgs.count { !it.fromMe && !it.read }
                )
            }.sortedByDescending { it.lastTimestamp }

    fun messagesWith(contactName: String): List<ChatMessage> =
        messages.filter { it.contactName == contactName }.sortedBy { it.timestamp }

    fun sendMessage(contactName: String, text: String, timestamp: Long) {
        messages.add(ChatMessage(newId(), contactName, text, true, timestamp, read = true))
        persist("messages", messages.toList())
        // Simulate an automatic reply so the chat feels alive without a backend.
        val reply = autoReply(text)
        messages.add(ChatMessage(newId(), contactName, reply, false, timestamp + 1, read = false))
        persist("messages", messages.toList())
        pushNotification("Mensaje de $contactName", reply, NotificationType.MESSAGE)
    }

    fun markConversationRead(contactName: String) {
        var changed = false
        messages.forEachIndexed { i, m ->
            if (m.contactName == contactName && !m.fromMe && !m.read) {
                messages[i] = m.copy(read = true); changed = true
            }
        }
        if (changed) persist("messages", messages.toList())
    }

    private fun autoReply(text: String): String {
        val t = text.lowercase()
        return when {
            "precio" in t || "costo" in t || "cotiz" in t -> "Con gusto, te envío la cotización actualizada hoy mismo."
            "hola" in t || "buenas" in t -> "¡Hola! Gracias por escribir. ¿En qué puedo ayudarte con tu evento?"
            "gracias" in t -> "¡A ti! Cualquier cosa quedo atento."
            "?" in text -> "Buena pregunta, déjame confirmarlo y te aviso enseguida."
            else -> "Perfecto, lo tomo en cuenta. Te confirmo los detalles a la brevedad."
        }
    }

    // ---- Notifications ---------------------------------------------------

    fun pushNotification(title: String, body: String, type: NotificationType) {
        notifications.add(0, AppNotification(newId(), title, body, type, false, System.currentTimeMillis()))
        persist("notifications", notifications.toList())
    }

    fun markNotificationRead(id: String) {
        val idx = notifications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            notifications[idx] = notifications[idx].copy(read = true)
            persist("notifications", notifications.toList())
        }
    }

    fun markAllNotificationsRead() {
        var changed = false
        notifications.forEachIndexed { i, n -> if (!n.read) { notifications[i] = n.copy(read = true); changed = true } }
        if (changed) persist("notifications", notifications.toList())
    }

    fun unreadNotifications(): Int = notifications.count { !it.read }

    // ---- Subscription ----------------------------------------------------

    fun setSubscription(plan: String, renewalDate: String) {
        _subscription.value = Subscription(plan = plan, active = true, renewalDate = renewalDate)
        persist("subscription", _subscription.value)
        pushNotification("Suscripción activada", "Ahora tienes el plan $plan.", NotificationType.SYSTEM)
    }

    // ---- Demo seed -------------------------------------------------------

    private fun seedDemoData() {
        pushNotification("¡Bienvenido a Eventify!", "Gestiona tus eventos, tareas y cotizaciones desde un solo lugar.", NotificationType.SYSTEM)
        messages.add(ChatMessage(newId(), "Ana Pérez", "Hola, ¿me puedes enviar la cotización del catering?", false, System.currentTimeMillis() - 3600_000, read = false))
        persist("messages", messages.toList())
        persist("notifications", notifications.toList())
    }
}

// ---- Data models ---------------------------------------------------------

enum class TaskPriority { ALTA, MEDIA, BAJA }
enum class TaskStatus { PENDIENTE, EN_PROGRESO, COMPLETADA }

data class LocalTask(
    val id: String = "",
    val eventId: String,
    val title: String,
    val description: String = "",
    val responsible: String = "",
    val dueDate: String = "",
    val priority: TaskPriority = TaskPriority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDIENTE
)

data class EventBudget(val eventId: String, val total: Double)

data class Expense(
    val id: String = "",
    val eventId: String,
    val description: String,
    val category: String,
    val amount: Double
)

data class AgendaActivity(
    val id: String = "",
    val eventId: String,
    val time: String,
    val title: String,
    val done: Boolean = false
)

data class ChatMessage(
    val id: String = "",
    val contactName: String,
    val text: String,
    val fromMe: Boolean,
    val timestamp: Long,
    val read: Boolean = false
)

data class ChatContact(
    val contactName: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unread: Int
)

enum class NotificationType { MESSAGE, QUOTE, TASK, PAYMENT, SYSTEM }

data class AppNotification(
    val id: String = "",
    val title: String,
    val body: String,
    val type: NotificationType,
    val read: Boolean,
    val timestamp: Long
)

data class Subscription(
    val plan: String = "Free",
    val active: Boolean = false,
    val renewalDate: String = ""
)
