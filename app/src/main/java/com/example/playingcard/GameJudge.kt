package com.example.playingcard

import androidx.fragment.app.activityViewModels

class GameJudge
{

    private val ranks = listOf("One","Two","Three")
    private val shapes = listOf("Diamond","Squiggle","Oval")
    private val shading = listOf("Solid","Striped","Open")
    private val colors = listOf("Red","Green","Purple")

    private var availableCards: MutableList<Card> = mutableListOf<Card>()
    private var doneCards: MutableList<Card> = mutableListOf<Card>()
    private var historyCount = -1

    private var choosedCard: MutableList<Card> = mutableListOf<Card>()

    init {

    }

    public fun initCard()
    {
        doneCards.clear()
        availableCards.clear()
        for(r in ranks)
        {
            for(shape in shapes)
            {
                for(shade in shading)
                {
                    for(c in colors)
                    {
                        val card = Card(rank = r, shape= shape, shade=shade, cardColor = c)
                        availableCards.add(card)
                    }
                }
            }
        }
    }

    public fun setCardHistory(allCards: MutableList<Card>)
    {
        val tmp_availableCards = mutableListOf<Card>()
        val tmp_doneCards = mutableListOf<Card>()

        for(card in allCards)
        {
            if(card.history<=-1){ tmp_availableCards.add(card) }
            else{
                tmp_doneCards.add(card)
                val currentCount = card.history
                if(currentCount>historyCount){ historyCount = currentCount }
            }
        }

        availableCards = tmp_availableCards
        doneCards = tmp_doneCards

        doneCards.sortBy { it.history }
    }

    public fun getAvailable(): MutableList<Card>{ return availableCards }
    public fun getDone(): MutableList<Card>{ return doneCards }
    public fun getAllCards(): MutableList<Card>{
        val allcards = availableCards.let{
                ava -> doneCards.let{ done->done+ava }
        }.toMutableList()
        return allcards
    }
    public fun getHistoryCount(): Int{ return historyCount }
    public fun getChosenCard(): MutableList<Card>{ return choosedCard }

    public fun getShuffle(current:MutableList<Card>, targetAmount:Int = 12) : MutableList<Card>
    {
        var check_not_exist = availableCards.toMutableList()
        for(c in current){
            check_not_exist.remove(c)
        }
        check_not_exist = check_not_exist.shuffled().toMutableList()
        val sublist = check_not_exist.subList(0,targetAmount - current.size)
        val mergeList = sublist.let{
            add -> current.let{ current_element->current_element+add }
        }.toMutableList()
        return mergeList
    }

    public fun setChoose(card: Card, condition: Boolean)
    {
        if(condition){
            choosedCard.add(card)
        }
        else{
            choosedCard.remove(card)
        }
        availableCards[availableCards.indexOf(card)].choose = condition
    }

    public fun clearChosenCards()
    {
        choosedCard.clear()
    }

    public fun setHistoryCount( count:Int = -1)
    {
        historyCount = count
    }

    public fun Judge(): Boolean
    {
        val rankSet = mutableSetOf<String>()
        val shapeSet = mutableSetOf<String>()
        val shadeSet = mutableSetOf<String>()
        val colorSet = mutableSetOf<String>()

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

        historyCount += 1
        for(c in choosedCard)
        {
            // if paired, log its paired history(round),
            // and remove it from available cards and add it to done cards
            c.history = historyCount
            availableCards.remove(c)
            doneCards.add(c)
        }
        return true
    }
}