package dev.vegui.eventdeck;

import javax.swing.*;

public abstract class View extends JPanel {

    /**
     * Se ejecuta cada vez que la vista está por mostrarse.
     */
    public void onShow() {
        reload();
    }

    /**
     * Se ejecuta antes de abandonar la vista.
     */
    public void onHide() {
    }

    /**
     * Recarga la información visible.
     */
    public void reload() {
    }

    /**
     * Obtiene el estado global de la aplicación.
     */
    public MainState getMainState() {
        return Main.getState();
    }

}
