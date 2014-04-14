package com.agatteclient.agatte;

/**
 * Created by Rémi pannequin on 14/04/14.
 */
public enum AgatteResultCode {
    punch_ok,
    query_ok,
    query_counter_ok,
    query_counter_unavailable,
    exception,
    io_exception,
    login_failed,
    network_not_authorized;
}
