package fr.isen.soubry.isensmartcompanion.data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InteractionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HistoryDatabase.getDatabase(application)
    private val interactionDao = db.interactionDao()

    private val _allInteractions = MutableStateFlow<List<Interaction>>(emptyList())
    val allInteractions = _allInteractions.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _allInteractions.value = interactionDao.getAllInteractions()
        }
    }

    fun insertInteraction(question: String, answer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val interaction = Interaction(
                question = question,
                answer = answer,
                date = System.currentTimeMillis()
            )
            interactionDao.insertInteraction(interaction)
            _allInteractions.value = interactionDao.getAllInteractions()
        }
    }

    fun deleteInteraction(interaction: Interaction) {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.deleteInteraction(interaction)
            _allInteractions.value = interactionDao.getAllInteractions()
        }
    }

    fun deleteAllInteractions() {
        viewModelScope.launch(Dispatchers.IO) {
            interactionDao.deleteAllInteractions()
            _allInteractions.value = interactionDao.getAllInteractions()
        }
    }
}
