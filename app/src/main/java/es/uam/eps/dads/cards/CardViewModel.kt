package es.uam.eps.dads.cards

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import es.uam.eps.dads.cards.database.CardDao
import es.uam.eps.dads.cards.database.CardDatabase
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class CardViewModel(application: Application): ViewModel() {
    val cards: LiveData<List<Card>>
    val dueCard: LiveData<Card?>
    val nDueCards: LiveData<Int>
    private val cardDao: CardDao

    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao

        deleteCards()
        deleteDecks()

        val englishDeck = Deck(name = "English")
        addDeck(englishDeck)

        addCard(Card("To wake up", "Despertarse", deckId = englishDeck.deckId))
        addCard(Card("To slow down", "Ralentizar", deckId = englishDeck.deckId))
        addCard(Card("To give up", "Rendirse", deckId = englishDeck.deckId))
        addCard(Card("To come up", "Acercarse", deckId = englishDeck.deckId))

        cards = cardDao.getCards()
        dueCard = cards.map {
            it.filter { card -> card.isDue(LocalDateTime.now()) }.run {
                if (any()) random() else null
            }
        }

        nDueCards = cards.map {
            it.filter { card ->
                card.isDue(LocalDateTime.now()) }.run {
                if (any()) size else 0
            }
        }
    }

    fun addCard(card: Card){
        viewModelScope.launch {
            cardDao.addCard(card)
        }
    }

    fun addDeck(deck: Deck){
        viewModelScope.launch {
            cardDao.addDeck(deck)
        }
    }

    fun deleteCards(){
        viewModelScope.launch {
            cardDao.deleteCards()
        }
    }

    fun deleteDecks(){
        viewModelScope.launch {
            cardDao.deleteDecks()
        }
    }

    fun getCard(cardId: String) = cardDao.getCard(cardId)

    fun updateCard(card: Card){
        viewModelScope.launch {
            cardDao.updateCard(card)
        }
    }

    fun update(card: Card, quality: Int){
        card.quality = quality
        card.update(LocalDateTime.now())
        updateCard(card)
    }

    fun getCardsByDeckName(deckName: String): LiveData<List<Card>> {
        return cardDao.getCardsByDeckName(deckName)
    }

}

class CardViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardViewModel(application) as T
    }
}