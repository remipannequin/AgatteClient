package com.agatteclient.agatte;

import android.os.Bundle;

/**
 * Created by Rémi Pannequin on 14/04/14.
 */
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
