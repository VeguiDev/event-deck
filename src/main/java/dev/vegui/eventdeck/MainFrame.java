package dev.vegui.eventdeck;

import javax.swing.*;

public class MainFrame extends JFrame {

    private final Router router;

    public MainFrame(Router router) {
        this.router = router;
        setTitle("EventDeck");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(router.getRoot());
    }

    public Router getRouter() {
        return router;
    }
}
