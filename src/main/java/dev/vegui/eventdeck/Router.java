package dev.vegui.eventdeck;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class Router {

    private final JPanel root;
    private final CardLayout layout;
    private final Map<Routes, JPanel> screens = new EnumMap<>(Routes.class);

    public Router() {
        this.layout = new CardLayout();
        this.root = new JPanel(layout);
    }

    public JPanel getRoot() {
        return root;
    }

    public void register(Routes route, JPanel panel) {
        screens.put(route, panel);
        root.add(panel, route.name());
    }

    public void navigate(Routes route) {
        layout.show(root, route.name());
    }

}
