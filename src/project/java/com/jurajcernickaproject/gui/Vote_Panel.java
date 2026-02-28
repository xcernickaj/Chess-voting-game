package com.jurajcernickaproject.gui;

import com.jurajcernickaproject.stringifier.MoveStringifier;
import com.jurajcernickaproject.gamelogic.GameController;
import com.jurajcernickaproject.gamelogic.Vote;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * A GUI element representing the window where the player chooses which move to vote for.*/
public class Vote_Panel extends JPanel {
    private final GameController gameController;
    private final boolean whites_turn;

    /**
     * Creates a window where the player has to choose which move to vote for.
     * @param gameController The gameController. Necessary to send as a parameter here in order to send the chosen vote to it.
     * @param voter_container The Voter_List_Container. Necessary to send as a parameter here in order to delete the players name from the list after said player has made their vote.
     * @param whites_turn A boolean value indicating if it's the white player's turn right now.*/
    public Vote_Panel(GameController gameController, Voter_List_Container voter_container, boolean whites_turn) {
        this.gameController = gameController;
        this.whites_turn = whites_turn;
        JButton[] move_buttons = new JButton[5];

        Vote[] votes = gameController.get_move_pool();
        int array_size = gameController.get_array_size();

        this.setPreferredSize(new Dimension(200, 200));

        JDialog dialog = new JDialog();
        dialog.setTitle("Vote Window");
        dialog.setModal(true);
        dialog.setSize(new Dimension(200, 200));

        if(array_size == 0) { // If there are no moves to choose from.
            dialog.setSize(new Dimension(150, 140));
            JLabel noMoves = new JLabel("<html><center><font size='3'>No moves to choose from.</font></center></html>");
            noMoves.setBorder(new EmptyBorder(10, 0, 0, 0));
            noMoves.setHorizontalAlignment(SwingConstants.CENTER);
            noMoves.setPreferredSize(new Dimension(40, noMoves.getPreferredSize().height));
            JButton closeButton = new JButton("<html><font size='2'>Ok</font></html>");
            this.setLayout(new BorderLayout());

            closeButton.setPreferredSize(new Dimension(40, 20));
            closeButton.addActionListener(e -> dialog.dispose());

            JPanel closeButtonPanel = new JPanel();
            closeButtonPanel.setBorder(new EmptyBorder(0, 10, 4, 10));
            closeButtonPanel.add(closeButton);

            this.add(noMoves, BorderLayout.CENTER);
            this.add(closeButtonPanel, BorderLayout.SOUTH);

            dialog.setResizable(false);
        } else { // If there is at least one move to choose from.
            this.setLayout(new BorderLayout());
            JLabel chooseMessage = new JLabel("Choose your move:");
            chooseMessage.setBorder(new EmptyBorder(10, 0, 15, 0));
            chooseMessage.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(chooseMessage, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new FlowLayout());

            for(int i = 0; i < array_size; i++) { // Add a button for each move.
                move_buttons[i] = new JButton(MoveStringifier.stringifyVote(votes[i]));
                move_buttons[i].addActionListener(new Vote_Listener(votes[i], dialog, voter_container));
                buttonPanel.add(move_buttons[i]);
            }

            this.add(buttonPanel, BorderLayout.CENTER);
        }

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(this);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    /**
     * A nested class representing an ActionListener which reacts to a vote being chosen by the player.*/
    public class Vote_Listener implements ActionListener {
        private final Vote vote;
        private final JDialog dialog;
        private final Voter_List_Container vote_container;
        /**
         * Initializes the attributes based on the parameters.
         * @param vote The vote which this particular implementation of Vote_Listener is reacting to.
         * @param dialog The JDialog which represents the popped up window with different move options for the player to vote for.
         * @param vote_container The Voter_List_Container containing all the player names.*/
        Vote_Listener(Vote vote, JDialog dialog, Voter_List_Container vote_container) {
            this.vote = vote;
            this.dialog = dialog;
            this.vote_container = vote_container;
        }
        /**
         * Reaction to a vote being chosen. Adds the vote to the gameController and deletes the player name from the vote_container.
         * * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void actionPerformed(ActionEvent e) {
            gameController.add_vote(vote, whites_turn);
            vote_container.delete_highlight(whites_turn);
            dialog.dispose();
        }
    }
}
