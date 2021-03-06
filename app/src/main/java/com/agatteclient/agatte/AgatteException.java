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

import android.os.Bundle;


public class AgatteException extends Exception {

    public AgatteException(String detailMessage) {
        super(detailMessage);
    }

    public AgatteException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AgatteException(Throwable throwable) {
        super(throwable);
    }

    public AgatteException() {
        super();
    }

    Bundle toBundle() {
        Bundle result = new Bundle();
        result.putString("message", this.getLocalizedMessage()); //NON-NLS
        return result;
    }

}
