package com.example.playingcard

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedViewModel(app:Application): AndroidViewModel(app) {

    private val ranks = listOf("One","Two","Three")
    private val shapes = listOf("Diamond","Squiggle","Oval")
    private val shading = listOf("Solid","Striped","Open")
    private val colors = listOf("Red","Green","Purple")
    private var DEFAULT_CARDS:Int = 12

    //Game data
    val cardList  = MutableLiveData<List<Card>>()

    private val dao:SaveDataDao = SaveDataDB.getInstance(app.applicationContext).dao

    fun reloadList(default_cards:Int = 12) = viewModelScope.launch {
        cardList.value = listOf<Card>()
        dao.clear()
        DEFAULT_CARDS = default_cards
        loadList()
    }

    fun saveData() = viewModelScope.launch {
        dao.clear()
        cardList.value?.let{
            dao.insertList(it)
        }
        loadList()
    }

    fun loadList(){
        cardList.value?.let{
            if(!it.isEmpty()){
                cardList.value = cardList.value
                return
            }
        }

        viewModelScope.launch {
            //isRefreshing.value = true
            val list = dao.loadList()
            if(list.size==81){
                cardList.value = list
            } else{
                initCard()
                cardList.value?.let{
                    dao.insertList(it)
                }
            }
        }
    }

    public fun initCard()
    {
        cardList.value = listOf<Card>()
        val initCardList = mutableListOf<Card>()
        for(r in ranks)
        {
            for(shape in shapes)
            {
                for(shade in shading)
                {
                    for(c in colors)
                    {
                        val card = Card(rank = r, shape= shape, shade=shade, cardColor = c)
                        initCardList.add(card)
                    }
                }
            }
        }

        cardList.value = initCardList.shuffled().toList()
        AddCard(DEFAULT_CARDS)
    }
    public fun getOnBoard():List<Card>{
        val onBoardCards = mutableListOf<Card>()
        for(c in cardList.value!!){ if(c.onBoard){onBoardCards.add(c)} }
        return onBoardCards.toList()
    }

    public fun getAvailable(): List<Card>{
        val availableCards = mutableListOf<Card>()
        for(c in cardList.value!!){ if(c.history<=-1){availableCards.add(c)} }
        return availableCards.toList()
    }
    public fun getDone(): List<Card>{
        val doneCards = mutableListOf<Card>()
        for(c in cardList.value!!){ if(c.history>-1){doneCards.add(c)} }
        return doneCards.toList()
    }
    public fun getHistoryCounts(): Int{
        var count = 0
        for(c in cardList.value!!){ if(c.history>-1){count+=1} }
        return count / 3
    }
    public fun AddCard(targetAmount:Int = 12)
    {
        val onBoardCount = getOnBoard().size
        if(targetAmount > onBoardCount){
            var availableAndNotOnBoard = mutableListOf<Card>()
            val tmpCardList = cardList.value!!.toList()
            for(c in tmpCardList)
            {
                if(!c.onBoard && c.history<=-1){ availableAndNotOnBoard.add(c) }
            }
            val addlist = availableAndNotOnBoard.subList(0, targetAmount - onBoardCount)
            for(a in addlist){ tmpCardList[tmpCardList.indexOf(a)].onBoard = true }
            cardList.value = tmpCardList
        }
        saveData()
    }

    public fun getChooseCount(): Int
    {
        var chooseCount = 0
        for(t in cardList.value!!){ if(t.choose){chooseCount+=1} }
        return chooseCount
    }

    public fun setChoose(card: Card, condition: Boolean)
    {
        val tmpCardList = cardList.value!!.toList()
        tmpCardList[tmpCardList.indexOf(card)].choose = condition
        cardList.value = tmpCardList
        saveData()
    }

    public fun clearChosenCards()
    {
        val tmpCardList = cardList.value!!.toList()
        for(t in tmpCardList){ if(t.choose){ t.choose = false } }
        cardList.value = tmpCardList
        saveData()
    }

    public fun Judge(): Boolean
    {
        val rankSet = mutableSetOf<String>()
        val shapeSet = mutableSetOf<String>()
        val shadeSet = mutableSetOf<String>()
        val colorSet = mutableSetOf<String>()

        val tmpCardList = cardList.value!!.toList()
        val choosedCard = mutableListOf<Card>()
        var doneRounds = getHistoryCounts()
        for(c in cardList.value!!){
            if(c.choose){ choosedCard.add(c) }
        }

        for(c in choosedCard)
        {
            rankSet.add(c.rank)
            shapeSet.add(c.shape)
            shadeSet.add(c.shade)
            colorSet.add(c.cardColor)
        }

        if(rankSet.size==1 || rankSet.size==3) { /*pass*/} else {return false}
        if(shapeSet.size==1 || shapeSet.size==3) { /*pass*/ } else {return false}
        if(shadeSet.size==1 || shadeSet.size==3) { /*pass*/ } else {return false}
        if(colorSet.size==1 || colorSet.size==3) { /*pass*/ } else {return false}

        doneRounds += 1
        for(c in choosedCard)
        {
            val index = tmpCardList.indexOf(c)
            // if paired, log its paired history(round),
            // and remove it from available cards and add it to done cards

            tmpCardList[index].onBoard = false
            tmpCardList[index].choose = false
            tmpCardList[index].history = doneRounds
        }
        cardList.value = tmpCardList

        saveData()

        return true
    }
}