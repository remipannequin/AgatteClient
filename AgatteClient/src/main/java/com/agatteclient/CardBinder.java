package com.agatteclient;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by remi on 29/10/13.
 */
public class CardBinder {
    private static CardBinder ourInstance = new CardBinder();

    public static CardBinder getInstance() {
        return ourInstance;
    }

    private LinkedList<DayCard> cards;
    private DayCard current;

    private CardBinder() {
        cards = new LinkedList<DayCard>();
    }

    /**
     * Return Today card
     */
    public DayCard getTodayCard() {
        if (!current.isCurrentDay()) {
            DayCard new_card = new DayCard();
            cards.addLast(new_card);
            current = new_card;
        }
        return current;
    }
}
