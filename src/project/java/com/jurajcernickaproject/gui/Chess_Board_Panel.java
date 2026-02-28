package com.jurajcernickaproject.gui;

import com.jurajcernickaproject.gamelogic.Chess_Position;
import com.jurajcernickaproject.gamelogic.Chess_Tile;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;
import com.jurajcernickaproject.pieces.Piece;
import com.jurajcernickaproject.gamelogic.GameController;
import com.jurajcernickaproject.gamelogic.Vote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * A GUI object which contains the chess board itself. Made up of Tile panels. Also includes any functions which manipulate with the chess board from a user interface perspective.*/
public class Chess_Board_Panel extends JPanel
{
    private final Tile_Panel[][] squares;
    /**
     * Sets the color of all tile panels to default. Gets called after a move, or whenever we want to stop highlighting tiles.*/
    private void dehighlight_tiles()
    {
        for (Tile_Panel[] row : squares)
            for (Tile_Panel current : row)
                    current.set_default(false);
    }
    /**
     * Sets the locked attribute to true.*/
    public void lock()
    {
        this.locked = true;
    }
    /**
     * Sets the locked attribute to false.*/
    public void unlock()
    {
        this.locked = false;
    }
    /**
     * Makes a move in the current position and updates the visual representation of the board.
     * @param vote The vote which represents the move played on the board.*/
    public void update_board(Vote vote)
    {
        position.perform_move(vote);
        position.swap_turn();

        dehighlight_tiles();

        selected_piece_coordinates = null;
        locked = false;
    }
    /**
     * Returns the current position.
     * @return The attribute position.*/
    public Chess_Position get_position()
    {
        return this.position;
    }
    /**
     * Loads the piece images on their corresponding tiles, as well as removing the previous ones.
     * Gets called at the very beginning of the game and after every update of the position.*/
    public void load_piece_images()
    {
        for(Tile_Panel[] row : this.squares)
        {
            for(Tile_Panel panel : row)
            {
                Piece piece = panel.get_tile().get_piece();
                if (piece == null && panel.getComponentCount() == 1) // If the tile contains an image of a chess piece but no chess piece from a logic perspective.
                {
                    panel.remove(0);

                    panel.revalidate();
                    panel.repaint();
                }
                else if (piece != null) // If there is a piece on that tile, we need to replace the image on that tile with the one which is up-to-date.
                {
                    if (panel.getComponentCount() == 1)
                        panel.remove(0);

                    JLabel piece_label = Chess_Icons.get_label(piece);
                    if(piece_label != null)
                        panel.add(piece_label, BorderLayout.CENTER);

                    panel.revalidate();
                    panel.repaint();
                }
            }
        }
    }
    /**
     * Loads the pieces images, and dehighlights the tiles. Gets called after the board changes when going to a previous or future position using the corner screen buttons.*/
    public void resetTiles() {
        load_piece_images();
        selected_piece_coordinates = null;
        dehighlight_tiles();
    }
    private boolean locked;
    private Coordinates selected_piece_coordinates;
    private static final int SIZE = 8;
    private final Chess_Position position;
    private GameController gameController;
    /**
     * Sets the gameController attribute equal to the parameter. Gets called only once, after the gameController has been created shortly after the program has been started.
     * @param gameController The new value for the attribute gameController.*/
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    /**
     * Initializes attributes either to their default values or based on the parameters. Also creates a two-dimensional matrix of tile panels, and initializes each tile panel.
     * @param pieces A two-dimensional matrix of chess tiles. A two-dimensional matrix of tile panels will be initialized based on this parameter.
     * @param whites_turn A boolean value which indicates whose move is it.*/
    public Chess_Board_Panel(Chess_Tile[][] pieces, boolean whites_turn)
    {
        this.position = new Chess_Position(pieces, whites_turn);
        this.position.update_possible_moves();
        this.selected_piece_coordinates = null;
        this.locked = false;

        setLayout(null);
        this.squares = new Tile_Panel[SIZE][SIZE];

        int square_size = Tile_Panel.get_preferred_size_int();

        boolean white_square = false;

        for (int row = 0; row < SIZE; row++)
        {
            for (int col = 0; col < SIZE; col++)
            {
                this.squares[row][col] = new Tile_Panel(white_square, position.get_tile(new Coordinates(row, col)));
                this.squares[row][col].setBounds(col * square_size, row * square_size, square_size, square_size);
                white_square = !white_square;

                Passive_Tile_Listener listener;
                listener = new Active_Tile_Listener(squares[row][col]);

                squares[row][col].addMouseListener(listener);

                add(this.squares[row][col]);
            }
            white_square = !white_square;
        }
        setPreferredSize(new Dimension(SIZE * square_size, SIZE * square_size));

        load_piece_images();
    }

    /**
     * A nested class representing an ActionListener for a tile panel. Decides what should happen in case the mouse cursor interacts with the board.*/
    private class Passive_Tile_Listener extends MouseAdapter
    {
        /**
         * When a tile gets clicked while the board is locked. Dehighlights all the highlighted tiles and sets the selected_piece_coordinates attribute to null.
         * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void mouseClicked(MouseEvent e)
        {
            dehighlight_tiles();
            selected_piece_coordinates = null;
        }
        Tile_Panel panel;
        /**
         * Initializes the attribute panel based on the parameter.
         * @param tile_panel The Tile_Panel, which will become the value of the attribute panel.*/
        Passive_Tile_Listener(Tile_Panel tile_panel)
        {
            this.panel = tile_panel;
        }
        /**
         * Decides what happens in case the cursor enters a tile panel. Responsible for changing colors of hovered over tile panels.
         * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void mouseEntered(MouseEvent e)
        {
            if(!panel.selected() && !panel.highlighted() && !panel.chosen())
                if(!panel.selected() && !panel.highlighted())
                    panel.setBackground(panel.hovered_color());
        }
        /**
         * Decides what happens in case the cursor leaves a tile panel. Responsible for changing colors of no longer hovered over tile panels.
         * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void mouseExited(MouseEvent e)
        {
            if(!panel.selected() && !panel.highlighted() && !panel.chosen())
                panel.setBackground(panel.default_color());
        }
    }
    /**
     * A nested class which represents an ActionListener, which decides what should happen in case the cursor enters a tile panel.
     * Unlike its parent class, it specifies active actions, specifically clicking on squares.*/
    public class Active_Tile_Listener extends Passive_Tile_Listener
    {
        /**
         * Determines what should happen after a cursor clicks on a tile panel. Contains the necessary logic to highlight a piece and the movable tile panels, dehighlight them,
         * and call functions necessary for performing moves.
         * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if(locked) // If the board is locked, clicking doesn't do anything.
                return;

            if(panel.highlighted()) // If the clicked tile panel is movable, and a move has to be performed.
            {
                if(selected_piece_coordinates != null && position.get_tile(selected_piece_coordinates).get_piece().type() == 'P'
                        && (panel.get_tile().get_coordinates().get_x() == 0 || panel.get_tile().get_coordinates().get_x() == 7)) { // If the moving piece is a pawn which reached the final rank, and a promotion panel has to be created
                    new Promotion_Panel(this);
                    if(promotion_piece == null) { // If no piece was has been chosen as the promotion piece.
                        dehighlight_tiles();
                        selected_piece_coordinates = null;
                        return;
                    }
                }

                Vote vote = new Vote(selected_piece_coordinates, panel.get_move(), promotion_piece);
                dehighlight_tiles();
                panel.set_chosen();
                squares[selected_piece_coordinates.get_x()][selected_piece_coordinates.get_y()].set_chosen();

                gameController.add_to_move_pool(vote);
                set_promotion_piece(null);
                selected_piece_coordinates = null;
            }
            else
            {
                set_squares_to_default();

                Piece piece = panel.get_tile().get_piece();
                if (piece != null && piece.white() == position.get_turn() &&
                        (selected_piece_coordinates == null ||
                                selected_piece_coordinates.get_x() != piece.get_coordinates().get_x() ||
                                selected_piece_coordinates.get_y() != piece.get_coordinates().get_y())) { // If the clicked piece isn't the one which is already highlighted.

                    highlight_movable_squares(piece);
                    selected_piece_coordinates = new Coordinates(panel.get_tile().get_coordinates());
                }
                else
                    selected_piece_coordinates = null;
            }
        }
        private String promotion_piece;
        /**
         * Sets the value of the attribute promotion_piece. Necessary for storing the information about which piece to promote to after a move is played.
         * @param name A String value which gets assigned to the attribute promotion_piece.*/
        public void set_promotion_piece(String name)
        {
            this.promotion_piece = name;
        }
        /**
         * Initializes the attribute panel based on the parameter.
         * @param tile_panel The Tile_Panel, which will become the value of the attribute panel.*/
        public Active_Tile_Listener(Tile_Panel tile_panel) {
            super(tile_panel);
            this.promotion_piece = null;
        }
        /**
         * Sets all the highlighted tile panels back to their default color.*/
        private void set_squares_to_default()
        {
            if(selected_piece_coordinates != null) {
                Tile_Panel selected_panel = squares[selected_piece_coordinates.get_x()][selected_piece_coordinates.get_y()];
                Piece selected_piece = selected_panel.get_tile().get_piece();

                selected_panel.set_default(true);

                for (Move move : selected_piece.get_moves()) {
                    Tile_Panel current_panel = squares[move.get_coordinates().get_x()][move.get_coordinates().get_y()];
                    current_panel.set_default(true);
                }
            }
        }
        /**
         * Changes the color of all movable tile panels to their highlighted version. A movable tile panel is each panel the highlighted piece can move to.
         * @param piece The piece which is currently highlighted. All the tile panels it can move to are getting highlighted.*/
        private void highlight_movable_squares(Piece piece)
        {
            panel.set_selected();

            if (panel.selected()) {
                panel.setBackground(new Color(228, 229, 172));
                LinkedList<Move> moves = piece.get_moves();

                for (Move move : moves) {
                    Tile_Panel current = squares[move.get_coordinates().get_x()][move.get_coordinates().get_y()];
                    current.set_highlighted();
                    current.set_move(move);
                }
            }
        }
    }
}
