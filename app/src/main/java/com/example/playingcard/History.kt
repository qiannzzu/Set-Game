package com.example.playingcard

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [History.newInstance] factory method to
 * create an instance of this fragment.
 */
class History : Fragment() {
    // TODO: Rename and change types of parameters
    private var eachColContains: Int = 0
    private var GAME_WEIGHT: Int = 1
    private var HISTORY_WEIGHT:Int = 1
    private var DEFAULT_LEAST_CARD:Int = 0
    private var Row: Int = 0
    private var historyCards: MutableList<Card> = mutableListOf<Card>()
    private val gameJudge= GameJudge()
    private val model:SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eachColContains = resources.getInteger(R.integer.eachCol_contains)
        DEFAULT_LEAST_CARD = resources.getInteger(R.integer.default_cards)
        setRow()
    }

    lateinit var historyRootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        historyRootView = inflater.inflate(R.layout.fragment_history, container, false)
        return historyRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.cardList.observe(viewLifecycleOwner, Observer {
            val allCards = it.toList()
            historyCards = mutableListOf<Card>()
            for(c in allCards)
            {
                if(c.history>-1){ historyCards.add(c) }
            }
            historyCards.sortBy { it.history }
            setRow()
            renderGrid()
        })
    }

    private fun setRow(){
        if(historyCards.size % eachColContains == 0) {Row = historyCards.size / eachColContains}
        else {Row = historyCards.size / eachColContains + 1}
    }

    private val TXT_HEIGHT_PERCENTAGE = 0.05f
    private val TXT_STD_HEIGHT = 126.00f
    private val CARD_STD_HEIGHT = 530.00f
    private val TXT_STD_SIZE = 18.0f
    private val HISTORY_HEIGHT_PERCENTAGE = 0.75f
    private val GLOBAL_WIDTH_PERCENTAGE = 0.9f

    private fun renderGrid(){
        //historyCards = gameJudge.getDone()
        //historyCards = model.getDone().toMutableList()
        val main_row_linearlayout = historyRootView.findViewById<LinearLayout>(R.id.history_main_row_linearlayout)
        val gameRowLinearLayout = historyRootView.findViewById<LinearLayout>(R.id.history_row_linearlayout)
        gameRowLinearLayout.removeAllViews()
        var full_width = historyRootView.resources.displayMetrics.widthPixels
        if(parentFragment==null){
            GAME_WEIGHT = resources.getInteger(R.integer.game_weight_on_tablet)
            HISTORY_WEIGHT = resources.getInteger(R.integer.history_weight_on_tablet)
            full_width = (historyRootView.resources.displayMetrics.widthPixels
                    * (HISTORY_WEIGHT.toFloat() / (GAME_WEIGHT+HISTORY_WEIGHT).toFloat())
                    ).toInt()
        }
        val full_height = historyRootView.resources.displayMetrics.heightPixels
        val history_TXT = historyRootView.findViewById<TextView>(R.id.HistoryCountText)
        history_TXT.height = (full_height * TXT_HEIGHT_PERCENTAGE).toInt()
        history_TXT.width = (full_width*GLOBAL_WIDTH_PERCENTAGE).toInt()
        history_TXT.text = "Current completed sets count: "+(model.getHistoryCounts()).toString()
        history_TXT.textSize = TXT_STD_SIZE * ((full_height * TXT_HEIGHT_PERCENTAGE) / TXT_STD_HEIGHT)
        var cardheight:Int = 0
        if(DEFAULT_LEAST_CARD / eachColContains >= Row){ cardheight = ((full_height * HISTORY_HEIGHT_PERCENTAGE) / (DEFAULT_LEAST_CARD / eachColContains)).toInt()}
        else{ cardheight = ((full_height * HISTORY_HEIGHT_PERCENTAGE) / Row).toInt()}
        //if(cardheight>CARD_STD_HEIGHT){ cardheight = CARD_STD_HEIGHT.toInt() }
        val cardwidth = ((full_width) / eachColContains * GLOBAL_WIDTH_PERCENTAGE).toInt()

        for(i in 0 until Row){
            val aRow = LinearLayout(historyRootView.context)
            aRow.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            aRow.orientation = LinearLayout.HORIZONTAL

            for(j in 0 until eachColContains){
                val card = PlayingCardView(historyRootView.context)
                val cardInfo = historyCards[i*eachColContains+j]
                card.rank = cardInfo.rank
                card.shape = cardInfo.shape
                card.shade = cardInfo.shade
                card.colorX = cardInfo.cardColor
                card.choose = false

                val displaypmetric = DisplayMetrics()
                card.layoutParams = ViewGroup.LayoutParams(cardwidth,cardheight)

                aRow.addView(card)
            }

            gameRowLinearLayout.addView(aRow)
        }
    }
}