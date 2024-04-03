package es.uam.eps.dads.cards

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uam.eps.dads.cards.ui.theme.CardsTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardsTheme {
                // A surface container using the 'background' color from the theme
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: CardViewModel = viewModel(
                            it,
                            "CardViewModel",
                            CardViewModelFactory(
                                LocalContext.current.applicationContext as Application
                            )
                        )
                        //CardList(viewModel)
                        Study(viewModel = viewModel)
                    }
                }
                /*val decks = mutableListOf<Deck>()
                val english = Deck(
                    name = "English",
                    description = "English phrasal verbs"
                )
                val french = Deck(
                    name = "French",
                    description = "French phrasal verbs"
                )
                decks += english
                decks += french
                val context = LocalContext.current
                val testCard = Card("To Wake Up", "Despertarse", deckId = english.deckId)
                val cardTwo = Card("To slow down", " Ralentizar", deckId = english.deckId)
                val c3 = Card("Test", "Test", deckId = english.deckId)
                val c4 = Card("Test", "Test", deckId = french.deckId)
                val c5 = Card("Test", "Test", deckId = french.deckId)
                val cardList = mutableListOf(testCard, cardTwo, c3, c4, c5)
                val numbersOfCardInDeck = cardList.filter {it.deckId == english.deckId}.size
                DeckList(cards = cardList, decks = decks)
                //CardList(cards = cardList, onItemClick = onItemClick)
                //Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    //  CardView(cardList, Modifier)
                //}*/
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CardsTheme {
        Greeting("Android")
    }
}

@Composable
fun DeckList(
    cards: List<Card>,
    decks: List<Deck>
){
    val context = LocalContext.current
    val onItemClick = {
        deck: Deck ->
        Toast.makeText(
            context,
            deck.name + " selected",
            Toast.LENGTH_SHORT
        ).show()
    }
    LazyColumn{
        items(decks) {
            deck -> DeckItem(deck = deck, cards = cards, onItemClick = onItemClick)
        }
    }
}

@Composable
fun DeckItem(
    deck: Deck,
    cards: List<Card>,
    onItemClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
            .clickable { onItemClick(deck) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = deck.name,
                modifier = modifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(text = deck.description,
                modifier = modifier,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(text = cards.filter { it.deckId == deck.deckId }.size.toString())
    }
}

@Composable
fun CardView(viewModel: CardViewModel, card: Card, modifier: Modifier = Modifier){
    var answered by remember { mutableStateOf(false)}
    val onAnswered = { it: Boolean -> answered = it }
    var currentCard by remember { mutableIntStateOf(0) }
    Column(modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val leftCards = viewModel.nDueCards.observeAsState().value
        Text(text = "Number of cards left = " + leftCards.toString())
        Text(text = card.question)
        if (answered){
            DifficultyButtons(answer = card.answer, card, viewModel, answered, onAnswered)

        }else{
            ViewAnswerButton(answered = answered, onValueChange = onAnswered)
        }
    }

}

@Composable
fun ViewAnswerButton(answered: Boolean, onValueChange: (Boolean) -> Unit){
    Button(onClick = { onValueChange(!answered) }, modifier = Modifier.border(
        width = 2.dp,
        shape = RoundedCornerShape(8.dp),
        color = Color.Blue)) {
        Text(text = "View Answer")
    }
}

@Composable
fun DifficultyButtons(answer: String,card: Card ,viewModel: CardViewModel,
                      answered: Boolean, onValueChange: (Boolean) -> Unit){
    Text(text = answer)

    val onClickAnswer = { value: Int ->
        viewModel.update(card, value)
        onValueChange(!answered)
    }
    val context = LocalContext.current
    Row {
        Button(onClick = { onClickAnswer(0) }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Green,
            contentColor = Color.Black
        )) {
            Text(text = "Easy")
        }
        Button(onClick = { onClickAnswer(3) }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Cyan,
            contentColor = Color.Black
        )) {
            Text(text = "Doubt")
        }
        Button(onClick = { onClickAnswer(5) }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
            contentColor = Color.Black
        )) {
            Text(text = "Hard")
        }
    }
}

@Composable
fun CardList(viewModel: CardViewModel){
    val cards by viewModel.getCardsByDeckName("English").observeAsState(listOf())
}

@Composable
fun CardListBuff(viewModel: CardViewModel){
    val cards by viewModel.cards.observeAsState()

    val context = LocalContext.current
    val onItemClick = { card: Card ->
        Toast.makeText(
            context,
            card.question,
            Toast.LENGTH_SHORT
        ).show()
    }
    LazyColumn {
        cards?.let {
            items(it) {
                    card -> CardItem(card = card, onItemClick = { })
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    modifier: Modifier = Modifier,
    onItemClick: (Card) -> Unit
){
    Log.d("JetpackCompose.app", "CardItem")
    Row (
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
            .clickable { onItemClick(card) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ){

        var switchState by remember { mutableStateOf(false)}
        val onSwitchChange = { it: Boolean -> switchState = it }
        SwitchIcon(switchState = switchState, onSwitchChange = onSwitchChange)
        CardData(card = card, modifier = modifier, onSwitchChange = onSwitchChange,
            switchState = switchState)
    }
}

@Composable
fun CardData(card: Card,
             modifier: Modifier,
             onSwitchChange: (Boolean) -> Unit,
             switchState: Boolean){
    Column {
        Log.d("JetpackCompose.app", "Col1")
        Text(text = card.question,
            modifier = modifier,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(text = card.answer,
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium
        )
        if (switchState){
            Text(text = "  Quality = " + card.quality.toString(), style = MaterialTheme.typography.bodySmall)
            Text("  Easiness = " + card.easiness.toString(), style = MaterialTheme.typography.bodySmall)
            Text(text = "  Repetitions = " + card.repetitions.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
    Column {
        Log.d("JetpackCompose.app", "Col2")
        Text(card.date.toString().substring(0..9),
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SwitchIcon(switchState: Boolean, onSwitchChange: (Boolean) -> Unit){
    val drawableResource = if (switchState) R.drawable.baseline_keyboard_double_arrow_up_24
    else R.drawable.baseline_keyboard_double_arrow_down_24

    Icon(painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier.clickable { onSwitchChange(!switchState) })
}

@Composable
fun Study(viewModel: CardViewModel){
    val card by viewModel.dueCard.observeAsState()
    card?.let {
        CardView(viewModel, it)
    }
}

@Composable
fun StateDemo() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var counter = remember { mutableIntStateOf(0) }
        Button(onClick = { counter.intValue++ }) {
            Text(text = "CLICK")
            Log.d("Borrar", "Button recomposed")
        }
        MyText(text = "The button was clicked ${counter.intValue} times")
        MyText(text = "This text doesn't change")
    }
}

@Composable
fun MyText(text: String) {
    Log.d("StateDemo", text)
    Text(text = text, modifier = Modifier.padding(all = 10.dp))
}

/*
@Preview(showBackground = true)
@Composable
fun Screen(){
    CardItem("To Wake Up",
        "Despertarse",
            Modifier
                .size(
                    width = 100.dp,
                    height = 50.dp
                )
        )
}
*/