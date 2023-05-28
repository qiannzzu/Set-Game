package com.example.playingcard

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Game.newInstance] factory method to
 * create an instance of this fragment.
 */
class Game : Fragment() {
    // TODO: Rename and change types of parameters
    private val model: SharedViewModel by activityViewModels()

    private var eachColContains: Int = 0
    private var DEFAULT_CARDS: Int = 0
    //private var CARDS_ON_BOARD: Int = 0
    private var GAME_WEIGHT: Int = 0
    private var HISTORY_WEIGHT:Int = 0
    private var Row: Int = 0
    private var buttonHeight: Int? = 0
    //private val gameJudge= GameJudge()
    private var currentCardsOnBoard = mutableListOf<Card>()
    //private var ChoosedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eachColContains = resources.getInteger(R.integer.eachCol_contains)
        DEFAULT_CARDS = resources.getInteger(R.integer.default_cards)
        //CARDS_ON_BOARD = model.getOnBoard().size
        //ChoosedCount = model.getChosenCard().size
    }

    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_game, container, false)
        //renderGrid()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //gameJudge.initCard()
        //model.initCard()
        model.loadList()
//        if(model.getOnBoard().isEmpty()){
//            //model.initCard()
//            //model.AddCard(DEFAULT_CARDS)
//        }
        //model.Shuffle(DEFAULT_CARDS)
        //renderGrid()
        model.cardList.observe(viewLifecycleOwner, Observer {
            val allcards = it.toList()
            currentCardsOnBoard = mutableListOf<Card>()
            for(c in allcards)
            {
                if(c.onBoard){ currentCardsOnBoard.add(c) }
            }
            setRow()
            renderGrid()
        })
    }

    private val BTN_HEIGHT_PERCENTAGE = 0.05f
    private val BTN_STD_HEIGHT = 126.00f
    private val CARD_STD_HEIGHT = 530.00f
    private val TXT_STD_SIZE = 18.0f
    private val GAME_HEIGHT_PERCENTAGE = 0.75f
    private val GLOBAL_WIDTH_PERCENTAGE = 0.9f

    private fun getOnBoardAmount(): Int {
        return model.getOnBoard().size
    }

    private fun setRow(){
        val cards_onBoard_count = getOnBoardAmount()
        if(cards_onBoard_count % eachColContains == 0) {Row = cards_onBoard_count / eachColContains}
        else {Row = cards_onBoard_count / eachColContains + 1}
    }

    private fun renderGrid(){
        //setRow()
        val cardsInfo = model.getOnBoard()

        val main_row_linearlayout = rootView.findViewById<LinearLayout>(R.id.main_row_linearlayout)
        val gameRowLinearLayout = rootView.findViewById<LinearLayout>(R.id.game_row_linearlayout)
        gameRowLinearLayout.removeAllViews()
        var full_width = rootView.resources.displayMetrics.widthPixels
        if(parentFragment==null){
            GAME_WEIGHT = resources.getInteger(R.integer.game_weight_on_tablet)
            HISTORY_WEIGHT = resources.getInteger(R.integer.history_weight_on_tablet)
            full_width = (rootView.resources.displayMetrics.widthPixels
                    * (GAME_WEIGHT.toFloat() / (GAME_WEIGHT+HISTORY_WEIGHT).toFloat())
                    ).toInt()
        }
        val full_height = rootView.resources.displayMetrics.heightPixels
        val RestartBTN:Button = rootView.findViewById<Button>(R.id.RestartBTN)
        val AddBTN: Button = rootView.findViewById<Button>(R.id.AddBTN)
        val HistoryBTN:Button = rootView.findViewById<Button>(R.id.HistoryBTN)
        RestartBTN.setOnClickListener {
            //CARDS_ON_BOARD = DEFAULT_CARDS
            //model.setAmount(DEFAULT_CARDS)
            //setRow()
            //gameJudge.initCard()
            model.reloadList(DEFAULT_CARDS)
            //model.initCard()
            //model.AddCard(DEFAULT_CARDS)
            //model.setAmountOnBoard(DEFAULT_CARDS)
            //CARDS_ON_BOARD = model.getAmountOnBoard()
            //setRow()
//            currentCardsOnBoard = mutableListOf<Card>()
//            val allcards = gameJudge.getAllCards()
//            gameJudge.setCardHistory(allcards)
//            gameJudge.setHistoryCount(-1)
            //val allcards = model.getAllCards()
            //model.setCardHistory(allcards)
            //model.setHistoryCountsTo(-1)
            //renderGrid()
        }
        AddBTN.setOnClickListener {
            val cards_onBoard_count = getOnBoardAmount()
            //if(CARDS_ON_BOARD<=(gameJudge.getAvailable().size - 3)) {
            if(cards_onBoard_count<=(model.getAvailable().size - 3)) {
                //CARDS_ON_BOARD += 3
                model.AddCard(cards_onBoard_count+3)
                //model.setAmountOnBoard(CARDS_ON_BOARD)
                //setRow()
                //renderGrid()
            }
            else{Toast.makeText(context, "All Cards is in the display!!", Toast.LENGTH_SHORT).show()}
        }
        HistoryBTN.setOnClickListener {
            Toast.makeText(context,
                //"Completed Round: " + (gameJudge.getHistoryCount() +1 ).toString(),
                "Completed Round: " + (model.getHistoryCounts() ).toString(),
                Toast.LENGTH_SHORT).show()
            //
            if(parentFragment!=null){
                val action = GameDirections.actionGameToHistory()
                findNavController().navigate(action)
            }
        }
        RestartBTN.height = (full_height * BTN_HEIGHT_PERCENTAGE).toInt()
        AddBTN.height = (full_height * BTN_HEIGHT_PERCENTAGE).toInt()
        HistoryBTN.height = (full_height * BTN_HEIGHT_PERCENTAGE).toInt()
        RestartBTN.width = (full_width*GLOBAL_WIDTH_PERCENTAGE / 3.0f).toInt()
        AddBTN.width = (full_width * GLOBAL_WIDTH_PERCENTAGE / 3.0f).toInt()
        HistoryBTN.width = (full_width*GLOBAL_WIDTH_PERCENTAGE / 3.0f).toInt()
        RestartBTN.textSize = TXT_STD_SIZE * ((full_height * BTN_HEIGHT_PERCENTAGE) / BTN_STD_HEIGHT)
        HistoryBTN.textSize = TXT_STD_SIZE * ((full_height * BTN_HEIGHT_PERCENTAGE) / BTN_STD_HEIGHT)
        val cardheight = ((full_height * GAME_HEIGHT_PERCENTAGE) / Row).toInt()
        val cardwidth = ((full_width) / eachColContains * GLOBAL_WIDTH_PERCENTAGE).toInt()

//        val cardsInfo = gameJudge.getShuffle(currentCardsOnBoard, CARDS_ON_BOARD);
//        val target_amount = model.getAmountOnBoard()
//        model.Shuffle(targetAmount = CARDS_ON_BOARD)

        for(i in 0 until Row){
            val aRow = LinearLayout(rootView.context)
            aRow.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            aRow.orientation = LinearLayout.HORIZONTAL

            for(j in 0 until eachColContains){
                val card = PlayingCardView(rootView.context)
                val cardInfo = cardsInfo[i*eachColContains+j]
                card.rank = cardInfo.rank
                card.shape = cardInfo.shape
                card.shade = cardInfo.shade
                card.colorX = cardInfo.cardColor
                card.choose = cardInfo.choose

                card.setOnClickListener {
                    var count = model.getChooseCount()
                    if(!cardInfo.choose) { count += 1 }
                    else { count -= 1 }

                    if(count<=3){
                        //val cardInfoStr = card.rank+card.shape+card.shade+card.color
                        //Toast.makeText(context, cardInfoStr, Toast.LENGTH_SHORT).show()
//                        gameJudge.setChoose(cardInfo, !cardInfo.choose)
                        model.setChoose(cardInfo, !cardInfo.choose)
                        card.choose = !card.choose

                        if(count==3)
                        {
//                            val judge = gameJudge.Judge()
                            val judge = model.Judge()
                            if(judge){
                                Toast.makeText(context,
                                    //"Correct pair, history count = "+(gameJudge.getHistoryCount()+1).toString(),
                                    "Correct pair, history count = "+(model.getHistoryCounts()).toString(),
                                    Toast.LENGTH_SHORT).show()
                                //val removeCards = gameJudge.getChosenCard()
                                //val removeCards = model.getChosenCard()
                                //for(rc in removeCards){ currentCardsOnBoard.remove(rc) }
//                                gameJudge.clearChosenCards()
                                model.clearChosenCards()
                                //ChoosedCount = 0
                                val cards_onBoard_count = model.getOnBoard().size
                                if(cards_onBoard_count >= DEFAULT_CARDS){
                                    //if already more than 12
                                    //CARDS_ON_BOARD -=3
                                    model.AddCard(cards_onBoard_count-3)
                                    //model.setAmount(CARDS_ON_BOARD)
                                    setRow()
                                }
//                                else if(gameJudge.getAvailable().size <= DEFAULT_CARDS){
                                else if(model.getAvailable().size <= DEFAULT_CARDS){
                                    //if available cards less than 12
                                    model.AddCard(cards_onBoard_count)
                                    setRow()
                                }
                                else{
                                    //if not more than 12 and available cards more than 12
                                    model.AddCard(DEFAULT_CARDS)
                                    setRow()
                                }
                                //model.saveAll()
                                //renderGrid()
                            }
                            else{
                                Toast.makeText(context,
                                    "Wrong pair!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(context,"Already Choosed 3 cards!", Toast.LENGTH_SHORT).show()
                    }

                }

                val displaypmetric = DisplayMetrics()
                card.layoutParams = ViewGroup.LayoutParams(cardwidth,cardheight)

                aRow.addView(card)
            }

            gameRowLinearLayout.addView(aRow)
            //currentCardsOnBoard = cardsInfo
        }
    }

    private fun saveData()
    {
        //val historyCount = gameJudge.getHistoryCount()
        //val card_on_board_count = currentCardsOnBoard.size
        //val
        //model.saveAll(historyCount = )
        //currentCardsOnBoard
    }
}