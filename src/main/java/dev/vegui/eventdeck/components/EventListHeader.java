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
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton claimButton = new JButton("Claim ticket");
        JButton button = new JButton("Crear");

        claimButton.addActionListener((e) -> {
            Main.getState().setCurrentEvent(null);
            Main.getState().setCurrentTicket(null);
            Main.getState().getRouter().navigate(Routes.TICKET_USE);
        });
        button.addActionListener((e) ->{
            Main.getState().getRouter().navigate(Routes.EVENT_CREATE);
        });

        actions.add(claimButton);
        actions.add(button);

        add(label, BorderLayout.WEST);
        add(actions,  BorderLayout.EAST);


    }

}
