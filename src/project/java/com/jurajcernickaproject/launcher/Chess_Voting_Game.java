package com.jurajcernickaproject.launcher;

import com.jurajcernickaproject.gui.Chess_Icons;
import com.jurajcernickaproject.gamelogic.Chess_Tile;
import com.jurajcernickaproject.exceptions.NoInputException;
import com.jurajcernickaproject.gui.Chess_Board_Panel;
import com.jurajcernickaproject.gui.Voter_List_Container;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.pieces.*;
import com.jurajcernickaproject.gamelogic.GameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Class mainly responsible for containing the main function.*/
public class Chess_Voting_Game
{
    /**
     * Creates a matrix of chess tiles, which contains the starting composition of pieces. Used to set up a new chess board.
     * @return A matrix of Chess_Tile with the starting composition of pieces.*/
    private static Chess_Tile[][] create_starting_position_pieces()
    {
        LinkedList<Piece> pieces = new LinkedList<>();

        pieces.add(new Rook(new Coordinates(0, 0), false, false));
        pieces.add(new Rook(new Coordinates(7, 0), true, false));
        pieces.add(new Knight(new Coordinates(0, 1), false));
        pieces.add(new Knight(new Coordinates(7, 1), true));
        pieces.add(new Bishop(new Coordinates(0, 2), false));
        pieces.add(new Bishop(new Coordinates(7, 2), true));

        pieces.add(new Rook(new Coordinates(0, 7), false, false));
        pieces.add(new Rook(new Coordinates(7, 7), true, false));
        pieces.add(new Knight(new Coordinates(0, 6), false));
        pieces.add(new Knight(new Coordinates(7, 6), true));
        pieces.add(new Bishop(new Coordinates(0, 5), false));
        pieces.add(new Bishop(new Coordinates(7, 5), true));

        pieces.add(new Queen(new Coordinates(0, 3), false));
        pieces.add(new Queen(new Coordinates(7, 3), true));
        pieces.add(new King(new Coordinates(0, 4), false, false));
        pieces.add(new King(new Coordinates(7, 4), true, false));

        for(int i = 0; i < 8; i++)
            pieces.add(new Pawn(new Coordinates(6, i), true, false, false));

        for(int i = 0; i < 8; i++)
            pieces.add(new Pawn(new Coordinates(1, i), false, false, false));

        Chess_Tile[][] tiles = new Chess_Tile[8][8];

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                tiles[i][j] = new Chess_Tile(new Coordinates(i, j));

        for(Piece piece : pieces)
            tiles[piece.get_coordinates().get_x()][piece.get_coordinates().get_y()].set_piece(piece);

        return tiles;
    }
    /**
     * The main function for starting the program. Sets up the main game window.
     * @param args Unused argument.*/
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            Chess_Icons.load_chess_icons();

            JFrame frame = new JFrame("Chess Voting Game"); // Setting up gui.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            Chess_Tile[][] pieces = create_starting_position_pieces();

            Chess_Board_Panel chessBoardPanel = new Chess_Board_Panel(pieces, true);
            GameController gameController = new GameController(chessBoardPanel);
            chessBoardPanel.lock();
            chessBoardPanel.setGameController(gameController);

            chessBoardPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            frame.add(chessBoardPanel, BorderLayout.CENTER);

            JButton white_player_button = new JButton("Add White Player");
            white_player_button.setPreferredSize(new Dimension(200, 50));
            Add_Voter_Button_Listener white_button_listener = new Add_Voter_Button_Listener(gameController, true);
            white_player_button.addActionListener(white_button_listener);

            JButton black_player_button = new JButton("Add Black Player");
            black_player_button.setPreferredSize(new Dimension(200, 50));
            Add_Voter_Button_Listener black_button_listener = new Add_Voter_Button_Listener(gameController, false);
            black_player_button.addActionListener(black_button_listener);


            Voter_List_Container voterList = new Voter_List_Container(gameController, chessBoardPanel, white_player_button, black_player_button, white_button_listener, black_button_listener);
            white_button_listener.add_voter_panel(voterList);
            black_button_listener.add_voter_panel(voterList);

            frame.add(voterList, BorderLayout.EAST);

            gameController.add_voter_container(voterList);

            frame.add(white_player_button, BorderLayout.SOUTH);
            frame.add(black_player_button, BorderLayout.NORTH);

            frame.setSize(960, 828);

            frame.setResizable(false);

            frame.setVisible(true);
        });
    }
    /**
     * A nested class, an ActionListener which takes care of the functionality of the button for adding new voters.*/
    public static class Add_Voter_Button_Listener implements ActionListener {
        private final GameController gameController;
        private  Voter_List_Container voter_panel;
        private final boolean white;

        /**
         * Takes care of what happens after the button has been pressed. Creates a new window where the user inputs the name of the added player.
         * If no name has been inputted, a new window telling the user about the incorrect name format is created.
         * @param e ActionEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void actionPerformed(ActionEvent e) {

            JFrame frame = new JFrame("Add Voter Dialog Example"); // GUI code to create a new window, which will prompt the user to name the new player.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);

            JButton okButton = new JButton("OK");
            okButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextField textField = new JTextField(20);

            JLabel label = new JLabel("Add a new voter:");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);


            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


            panel.add(label);
            panel.add(Box.createVerticalStrut(10));
            panel.add(textField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(okButton);


            final JDialog dialog = new JDialog(frame, "Add Voter", true);
            dialog.setContentPane(panel);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);

            okButton.addActionListener(e1 -> {

                try {
                    String input = textField.getText();
                    dialog.dispose();

                    if(input.isEmpty())
                        throw new NoInputException();

                    if (voter_panel != null) { // In case of a valid input.
                        if (white)
                            voter_panel.getWhite_list().add_voter(input); // Add the new voter.
                        else
                            voter_panel.getBlack_list().add_voter(input);

                        gameController.increment_voter_count(white);
                    }
                } catch (NoInputException exception) { // In case of an invalid input.
                    JDialog dialog1 = new JDialog();
                    dialog1.setTitle("Invalid input");
                    dialog1.setSize(new Dimension(150, 140));
                    JLabel noMoves = new JLabel("<html><center><font size='3'>Invalid input!</font></center></html>");
                    noMoves.setBorder(new EmptyBorder(10, 0, 0, 0));
                    noMoves.setHorizontalAlignment(SwingConstants.CENTER);
                    noMoves.setPreferredSize(new Dimension(40, noMoves.getPreferredSize().height));
                    JButton closeButton = new JButton("<html><font size='3'>Ok</font></html>");

                    JPanel panel1 = new JPanel();
                    panel1.setLayout(new BorderLayout());

                    closeButton.setPreferredSize(new Dimension(50, 25));
                    closeButton.addActionListener(e11 -> {
                        dialog1.dispose();
                    });

                    JPanel closeButtonPanel = new JPanel();
                    closeButtonPanel.setBorder(new EmptyBorder(0, 10, 4, 10));
                    closeButtonPanel.add(closeButton);

                    panel1.add(noMoves, BorderLayout.CENTER);
                    panel1.add(closeButtonPanel, BorderLayout.SOUTH);

                    dialog1.setResizable(false);
                    dialog1.setLocationRelativeTo(null);
                    dialog1.add(panel1);
                    dialog1.setVisible(true);
                }
            });

            dialog.setVisible(true);
        }
        /**
         * Initializes the attributes based on the parameters.
         * @param gameController Sets the gameController attribute.
         * @param white A boolean value indicating which side the button is for (whether it adds white players or black players).*/
        Add_Voter_Button_Listener(GameController gameController, boolean white) {
            this.gameController = gameController;
            this.white = white;
        }
        /**
         * Sets the voter_panel attribute.
         * @param voter_panel The new value of the voter_panel attribute.*/
        public void add_voter_panel(Voter_List_Container voter_panel) {
            this.voter_panel = voter_panel;
        }
    }
}
