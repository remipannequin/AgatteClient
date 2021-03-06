/*
 * This file is part of AgatteClient.
 *
 * AgatteClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgatteClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2014 Rémi Pannequin (remi.pannequin@gmail.com).
 */

package com.agatteclient.agatte;

/**
 * This file is part of AgatteClient
 * <p/>
 * Created by remi on 17/06/14.
 */
public class InvalidPunchingConditionException extends AgatteException {

    public InvalidPunchingConditionException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidPunchingConditionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidPunchingConditionException(Throwable throwable) {
        super(throwable);
    }

    public InvalidPunchingConditionException() {
    }
}
