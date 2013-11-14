package com.compomics.colims.client.controller;

/**
 *
 * @author niels
 */
public interface Controllable {

    /**
     * Init the controller; add bindings, action listeners, ...
     */
    void init();

    /**
     * Show the (main) view controlled by this controller. Normally, one
     * controller controls one view.
     */
    void showView();
}