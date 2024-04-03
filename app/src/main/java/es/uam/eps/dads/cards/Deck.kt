package es.uam.eps.dads.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "decks_table")
class Deck(
    @PrimaryKey
    val deckId: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "DECK DESCRIPTION"
)