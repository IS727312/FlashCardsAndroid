package es.uam.eps.dads.cards.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import es.uam.eps.dads.cards.Card
import es.uam.eps.dads.cards.Deck

@Dao
interface CardDao {
    @Query("SELECT * FROM cards_table")
    fun getCards() : LiveData<List<Card>>

    @Query("SELECT * FROM cards_table WHERE id == :cardId")
    fun getCard(cardId: String): Card

    @Query("SELECT * FROM cards_table WHERE deckId ==:deckId")
    fun getCardsByDeckId(deckId: String): LiveData<List<Card>>

    @Query("SELECT * FROM cards_table " +
            "INNER JOIN decks_table ON decks_table.deckId == cards_table.deckId " +
            "WHERE decks_table.name == :deckName")
    fun getCardsByDeckName(deckName: String): LiveData<List<Card>>

    @Insert
    suspend fun addCard(card: Card)

    @Insert
    suspend fun addDeck(deck: Deck)

    @Query("DELETE FROM cards_table")
    suspend fun deleteCards()

    @Query("DELETE FROM decks_table")
    suspend fun deleteDecks()

    @Update
    suspend fun updateCard(card: Card)
}