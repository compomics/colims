package com.compomics.colims.client.controller;

/**
 *
 * @author Niels Hulstaert
 */
public interface Controllable {

    /**
     * Init the controller; add bindings, action listeners, ...
     */
    void init();

    /**
     * Show the (main) view controlled by this controller and/or perform some
     * additional actions (clear/reset selection). Normally, one controller
     * controls one view.
     */
    void showView();
}
