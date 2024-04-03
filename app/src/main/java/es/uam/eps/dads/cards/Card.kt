package es.uam.eps.dads.cards

import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.max
import kotlin.math.roundToLong
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question")
    var question: String,
    var answer: String,
    var date: String = LocalDateTime.now().toString(),
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var deckId: String = UUID.randomUUID().toString()
) {
    var answered = false
    var quality = 0
    var repetitions = 0
    var interval = 1L
    var nextPracticeDate = date
    var easiness = 2.5

    fun update(currentDate: LocalDateTime) {
        val value: Double = easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
        easiness = max(1.3, value)

        if (quality < 3)
            repetitions = 0
        else
            repetitions += 1

        interval = if (repetitions <= 1) 1L
        else if (repetitions == 2) 6L
        else (easiness * interval).roundToLong()

        nextPracticeDate = currentDate.plusDays(interval).toString()
    }

    fun isDue(date: LocalDateTime) = LocalDateTime.parse(nextPracticeDate) <= date
}