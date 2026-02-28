package com.jurajcernickaproject.gui;

import com.jurajcernickaproject.exceptions.NoVotersException;
import com.jurajcernickaproject.gamelogic.GameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * A GUI container for lists of voters for each side. Also contains the start game button and later the button for voting.*/
public class Voter_List_Container extends JPanel {
    private final JButton game_button;
    private final VoterList white_list;
    private final VoterList black_list;
    private final GameController gameController;
    private final Chess_Board_Panel boardPanel;
    /**
     * Returns the voter list for the black side.
     * @return The black side VoterList.*/
    public VoterList getBlack_list() {
        return black_list;
    }
    /**
     * Returns the voter list for the white side.
     * @return The white side VoterList.*/
    public VoterList getWhite_list() {
        return white_list;
    }
    /**
     * Creates the list of voters for each side as well as initializing attributes to their default states or based on parameters.
     * @param gameController The GameController. For checking if the game can start as well as sending it as a parameter to a Vote_Panel when it is created.
     * @param boardPanel The visual representation of the chess board. For manipulating with the current position state as well as updating the visual representation.
     * @param white_button The add white player button. After the start the game button has been pressed, this button changes functionality to going back to the previous chess state.
     * @param black_button The add black player button. After the start the game button has been pressed, this button changes functionality to going back to the next chess state.
     * @param white_button_listener The ActionListener taking care of the white button functionality. It changes after the start game button has been pressed.
     * @param black_button_listener The ActionListener taking care of the black button functionality. It changes after the start game button has been pressed.*/
    public Voter_List_Container(GameController gameController, Chess_Board_Panel boardPanel, JButton white_button, JButton black_button, ActionListener white_button_listener, ActionListener black_button_listener)
    {
        super();
        this.gameController = gameController;
        this.boardPanel = boardPanel;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        white_list = new VoterList();
        black_list = new VoterList();
        white_list.lock();
        black_list.lock();


        JButton game_button = new JButton("Start Game");
        game_button.setPreferredSize(new Dimension(150, 50));
        game_button.addActionListener(new Start_Button_Listener(this, white_button, black_button, white_button_listener, black_button_listener));
        this.game_button = game_button;
        game_button.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        buttonPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));


        white_list.setPreferredSize(new Dimension(200, 150));
        black_list.setPreferredSize(new Dimension(200, 150));


        buttonPanel.add(game_button);
        buttonPanel.setPreferredSize(new Dimension(140, 70));

        add(black_list);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(buttonPanel);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(white_list);
    }
    /**
     * Deletes the highlighted name from the list of voters. Gets called after said player has voted, so the same player can't be chosen to vote several times.
     * @param white A boolean value indicating the side of the player.*/
    public void delete_highlight(boolean white) {
        if(white)
            white_list.deleteSelectedLine();
        else
            black_list.deleteSelectedLine();
    }
    /**
     * Returns if a player is highlighted. To make sure a player is highlighted before giving the option to vote.
     * @return A boolean value indicating if any player's name is highlighted.*/
    public boolean line_highlighted() {
        return white_list.getSelectedLine() != -1 || black_list.getSelectedLine() != -1;
    }

    /**
     * A nested class representing the ActionListener for the start button.*/
    public class Start_Button_Listener implements ActionListener {
        private final Voter_List_Container voter_container;
        private final JButton white_button;
        private final JButton black_button;
        private final ActionListener white_button_listener;
        private final ActionListener black_button_listener;
        /**
         * Controls what happens after the start button has been pressed. Either starts the game if each side has at least one voter, or creates a window informing the player that both sides need at least one voter
         * to start the game. After the game starts, changes the functionality of several buttons and unlocks the game board to be playable.
         * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(!gameController.game_can_start())
                    throw new NoVotersException();
            }
            catch(NoVotersException exception) { // Creates a window informing the player that each side needs at least one voter.
                JDialog dialog = new JDialog();
                dialog.setTitle("Error");
                dialog.setModal(true);

                dialog.setSize(new Dimension(200, 130));
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.setLocationRelativeTo(null);

                JButton close_button = new JButton("<html><font size='2'>Ok</font></html>");
                close_button.setPreferredSize(new Dimension(40, 20));
                close_button.addActionListener(e1 -> dialog.dispose());

                JPanel button_panel = new JPanel();
                button_panel.setBorder(new EmptyBorder(0, 10, 4, 10));

                button_panel.add(close_button);

                JLabel panelDialog = new JLabel("<html><center><font size='3'>You have to add voters to both sides!</font></center></html>");
                panelDialog.setBorder(new EmptyBorder(12, 0, 10, 10));
                panelDialog.setHorizontalAlignment(SwingConstants.CENTER);

                JPanel contentPane = new JPanel(new BorderLayout());
                contentPane.add(panelDialog, BorderLayout.CENTER);
                contentPane.add(button_panel, BorderLayout.SOUTH);
                dialog.setContentPane(contentPane);
                dialog.setLocationRelativeTo(null);

                dialog.setVisible(true);

                return;
            }

            boardPanel.unlock(); // Unlocks the board, changes the functionality of several buttons.
            white_list.unlock();
            game_button.setText("Choose Vote");
            game_button.removeActionListener(this);
            game_button.addActionListener(new Vote_Button_Listener(voter_container));
            white_button.removeActionListener(white_button_listener);
            black_button.removeActionListener(black_button_listener);
            white_button.setText("Previous board state");
            black_button.setText("Next board state");

            white_button.addActionListener(e12 -> { // White_button now changes the board state to the previous board state.
                if(!boardPanel.get_position().getPreviousStates().isEmpty()) {
                    gameController.reset_move_pool();
                    boardPanel.get_position().goBackToPreviousState();
                    boardPanel.resetTiles();
                    toggle_lock();
                }
            });

            black_button.addActionListener(e13 -> { // Black_button now changes the board state to the next board state.
                if(!boardPanel.get_position().getFutureStates().isEmpty()) {
                    gameController.reset_move_pool();
                    boardPanel.get_position().goToNextState();
                    boardPanel.resetTiles();
                    toggle_lock();
                }
            });
        }
        /**
         * Initializes the attributes based on the parameters.
         * @param voter_container The GUI container containing the voterLists for both sides.
         * @param white_button The white button which serves either to add a white voter or to go to the previous board state, depending on the game stage.
         * @param black_button The black button which serves either to add a black voter or to go to the next board state, depending on the game stage.
         * @param white_button_listener The ActionListener taking care of the white button functionality. It changes after the start game button has been pressed.
         * @param black_button_listener The ActionListener taking care of the black button functionality. It changes after the start game button has been pressed.*/
        Start_Button_Listener(Voter_List_Container voter_container, JButton white_button, JButton black_button, ActionListener white_button_listener, ActionListener black_button_listener) {
            this.voter_container = voter_container;
            this.white_button = white_button;
            this.black_button = black_button;
            this.white_button_listener = white_button_listener;
            this.black_button_listener = black_button_listener;
        }
    }
    /**
     * A nested class. An ActionListener for the vote button. Takes care of what happens when a player wants to add a new vote.*/
    public class Vote_Button_Listener implements ActionListener {
        private final Voter_List_Container voter_container;
        /**
         * In case a name is highlighted on the voter list, a new vote panel is created where the highlighted player can vote.
         * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void actionPerformed(ActionEvent e) {
            if(voter_container.line_highlighted())
                new Vote_Panel(gameController, voter_container, boardPanel.get_position().get_turn());
        }
        /**
         * Initializes the voter_container attribute based on the parameter.
         * @param voter_container The GUI container containing the voterLists for both sides.*/
        Vote_Button_Listener(Voter_List_Container voter_container) {
            this.voter_container = voter_container;
        }
    }
    /**
     * Toggles the locks of the voter list on both sides. Gets after a move gets played, so the currently unlocked list of players gets locked and vise versa.*/
    public void toggle_lock() {
        white_list.toggle_lock();
        black_list.toggle_lock();
    }
}
