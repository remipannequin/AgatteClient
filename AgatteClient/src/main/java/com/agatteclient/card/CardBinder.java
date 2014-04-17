/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient.card;

import java.util.LinkedList;

/**
 * Created by remi on 29/10/13.
 */
public class CardBinder {
    private static final CardBinder ourInstance = new CardBinder();

    public static CardBinder getInstance() {
        return ourInstance;
    }

    private final LinkedList<DayCard> cards;
    private DayCard current;

    private CardBinder() {
        cards = new LinkedList<DayCard>();
    }

    /**
     * Return Today card
     */
    public DayCard getTodayCard() {
        if (current == null || !current.isCurrentDay()) {
            DayCard new_card = new DayCard();
            cards.addLast(new_card);
            current = new_card;
        }
        return current;
    }
}
