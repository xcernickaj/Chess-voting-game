package com.jurajcernickaproject.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 * A GUI component representing the window which pops up when a piece is about to be promoted, letting the admin choose which piece the moving piece will get promoted to.*/
public class Promotion_Panel extends JPanel {
    private final JButton queen_button;
    private final JButton knight_button;
    private final JButton rook_button;
    private final JButton bishop_button;
    /**
    * Creates a new promotion panel, where the admin will have to choose which piece to promote the moving piece to.
     * @param tile_listener A tile_listener situated at the tile where the promotion is happening.*/
    public Promotion_Panel(Chess_Board_Panel.Active_Tile_Listener tile_listener) {
        queen_button = new JButton("Queen");
        knight_button = new JButton("Knight");
        rook_button = new JButton("Rook");
        bishop_button = new JButton("Bishop");

        add(queen_button); // Adding buttons for each possible promotion possibility.
        add(knight_button);
        add(rook_button);
        add(bishop_button);

        JDialog dialog = new JDialog();
        dialog.setTitle("Pawn Promotion");
        dialog.setModal(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                tile_listener.set_promotion_piece(null); // If the window gets closed, the promotion piece will be null.
                dialog.dispose();
            }
        });
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        ActionListener listener = new Promotion_Listener(tile_listener, dialog);
        this.addActionListeners(listener);

        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    /**
     * Adds the necessary action listener to the promotion buttons.
     * @param listener The ActionListener which will react to a button being pushed.*/
    public void addActionListeners(ActionListener listener) {
        queen_button.addActionListener(listener);
        knight_button.addActionListener(listener);
        rook_button.addActionListener(listener);
        bishop_button.addActionListener(listener);
    }

    /**
     * A nested class which represents an actionListener for the promotion window. It reacts to a button being pushed by taking the label of said button and
     * setting the Tile_Listener's promotion piece attribute equal to said label. This way we can have one ActionListener for all four buttons.*/
    private static class Promotion_Listener implements ActionListener {
        private final Chess_Board_Panel.Active_Tile_Listener tile_listener;
        private final JDialog dialog;
        /**
         * Initializes the attributes based on the parameters.
         * @param tile_listener The Tile_Listener of the tile where the promotion happens.
         * @param dialog A JDialog which represents the popped up window where the admin has to choose which piece to promote the moving piece into.*/
        Promotion_Listener(Chess_Board_Panel.Active_Tile_Listener tile_listener, JDialog dialog)
        {
            this.tile_listener = tile_listener;
            this.dialog = dialog;
        }
        /**
         * Takes the label of the pressed button and sets the attribute on the Tile_Listener equal to said label.
         * @param e ActionEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void actionPerformed(ActionEvent e) {
            String selected_piece = ((JButton) e.getSource()).getText().toLowerCase();
            tile_listener.set_promotion_piece(selected_piece);

            dialog.dispose();
        }
    }
}
