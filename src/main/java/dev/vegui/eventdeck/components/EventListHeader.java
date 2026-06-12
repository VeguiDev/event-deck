package dev.vegui.eventdeck.components;

import dev.vegui.eventdeck.Main;
import dev.vegui.eventdeck.MainFrame;
import dev.vegui.eventdeck.Routes;

import javax.swing.*;
import java.awt.*;

public class EventListHeader extends JPanel {

    public EventListHeader() {
        setLayout(new BorderLayout());
        setBorder(
                BorderFactory.createEmptyBorder(5, 16, 25, 16)
        );

        JLabel label = new JLabel("Tus eventos");
        label.setFont(
                label.getFont().deriveFont(Font.BOLD, 18f)
        );
        JButton button = new JButton("Crear");

        button.addActionListener((e) ->{
            Main.getState().getRouter().navigate(Routes.EVENT_CREATE);
        });

        add(label, BorderLayout.WEST);
        add(button,  BorderLayout.EAST);


    }

}
