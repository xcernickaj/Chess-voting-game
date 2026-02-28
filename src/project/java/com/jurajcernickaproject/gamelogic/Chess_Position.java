package com.jurajcernickaproject.gamelogic;

import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;
import com.jurajcernickaproject.pieces.*;
import com.jurajcernickaproject.move.Move_Type;

import java.io.*;
import java.util.LinkedList;

/**
 * Represents a chess position. It contains different functions which manipulate the position on a logic level, as well as the information about the state of the current position
 * It also contains the serialised versions of previous as well as future chess positions, along with the serialization and deserialization functions.
 * A current chess state (which consists of a certain composition of pieces on the board, as well as which player's turn it is) exists in the form of a class declared inside this class*/
public class Chess_Position
{
    private final Position_State state;
    /**
     * Checks if a player is in check. Useful for determining which moves are legal (making sure that a side cannot make a move which puts them in check).
     * It is also useful when determining if castling is possible (you cannot castle when you are in check).
     * Uses RTTI with the keyword instanceof, for determining whether the attacked piece is of type King.
     * @return A boolean value which indicates if a check is on the board*/
    public boolean in_check()
    {
        for(Chess_Tile[] row : this.get_tiles())
        {
            for(Chess_Tile tile : row)
            {
                Piece enemy_piece = tile.get_piece();
                if (enemy_piece != null && enemy_piece.white() != state.whites_turn) // were checking if the current piece is of the right color, and if there is any piece at all on that square in the first place
                {
                    enemy_piece.calculate_moves(this); // calculate possible moves for this enemy piece
                    for(Move enemy_move : enemy_piece.get_moves())
                    {
                        Piece captured_piece = this.get_tile(enemy_move.get_coordinates()).get_piece();
                        if(captured_piece instanceof King) // we check if the enemy piece can capture a king
                            return true; // a side is in check
                    }
                }
            }
        }
        return false;
    }
    private final LinkedList<byte[]> previousStates;
    private final LinkedList<byte[]> futureStates;
    /**
     * Takes every piece which belongs to a certain side and updates its possible moves. Usually gets called after a move has been made and the board has been updated.
     * @return A boolean value which indicates that a side has at least one possible move. Important for determining when a game should end by checkmate or stalemate*/
    public boolean update_possible_moves()
    {
        boolean movable = false;

        for (Chess_Tile[] tile_row : state.tiles)
        {
            for (Chess_Tile tile : tile_row)
            {
                Piece piece = tile.get_piece();
                if(piece != null && piece.white() == state.whites_turn)
                    if(piece.set_possible_moves(this))
                        movable = true;
            }
        }
        return movable;
    }
    /**
     * Uses the parameters to create a new chess state. Also initializes other class variables.
     * @param board_setup A matrix of chess tiles representing the composition of pieces on the board.
     * @param whites_turn A boolean value indicating whose turn it is.*/
    public Chess_Position(Chess_Tile[][] board_setup, boolean whites_turn)
    {
        this.state = new Position_State(board_setup, whites_turn, 0);
        this.previousStates = new LinkedList<>();
        this.futureStates = new LinkedList<>();
    }
    /**
     * Returns a tile with the specified coordinates.
     * @return Chess_Tile with the specified coordinates.*/
    public Chess_Tile get_tile(Coordinates coordinates)
    {
        return state.tiles[coordinates.get_x()][coordinates.get_y()];
    }
    /**
     * Returns all chess tiles.
     * @return A matrix of Chess_Tile representing the composition of pieces.*/
    public Chess_Tile[][] get_tiles()
    {
        return state.tiles;
    }
    /**
     * Returns a boolean value indicating whose turn it is.
     * @return A boolean value indicating whose turn it is.*/
    public boolean get_turn()
    {
        return state.whites_turn;
    }
    /**
     * Toggles the value indicating whose turn it is from one side to another. Usually gets called after a move has been played on the board, and it's time for the other side to make the next move.*/
    public void swap_turn()
    {
        state.whites_turn = !state.whites_turn;
    }
    /**
     * Moves a piece with certain coordinates to another tile. Also involves removing the piece from its original coordinates, and removing the piece previously occupying the destination tile
     * @param initial_coordinates Initial coordinates of the moving piece.
     * @param move Move which the piece is supposed to make on the board.*/
    private void move_piece(Coordinates initial_coordinates, Move move)
    {
        Chess_Tile destination = get_tile(move.get_coordinates());
        Chess_Tile start = get_tile(initial_coordinates);

        destination.set_piece(start.get_piece());
        destination.set_occupied();
        start.set_piece(null); // Removes the piece from its original position
        start.set_unoccupied();

        destination.get_piece().update(move.get_coordinates()); // Updates the coordinates of the moving piece, to its new coordinates
    }
    /**
     * Returns a new piece based on the function parameter. Gets called when a side promotes a pawn, and needs a new piece of a certain type, based on which piece that side chose to promote the pawn into.
     * @param vote Contains the information of which piece the pawn is supposed to promote into, and thus which piece the function should return.
     * @return New piece of a certain type, based on the vote parameter.*/
    private Piece create_promotion_piece(Vote vote)
    {
        return switch (vote.promotion_piece())
        {
            case "queen" -> new Queen(vote.get_move().get_coordinates(), get_tile(vote.get_move().get_coordinates()).get_piece().white());
            case "knight" -> new Knight(vote.get_move().get_coordinates(), get_tile(vote.get_move().get_coordinates()).get_piece().white());
            case "bishop" -> new Bishop(vote.get_move().get_coordinates(), get_tile(vote.get_move().get_coordinates()).get_piece().white());
            case "rook" -> new Rook(vote.get_move().get_coordinates(), get_tile(vote.get_move().get_coordinates()).get_piece().white(), true);
            default -> null;
        };
    }
    /**
     * Takes a vote as parameter and performs the move inside said vote on the board. Gets called after all the voters of the playing side have voted. The most popular vote gets sent as the parameter.
     * Function has to take care of different types of moves, which is achieved using a switch statement.
     * After the move has been performed, all pawns on the board (except the one which has just moved) are marked as impossible to en passant.
     * @param vote The most popular vote. The move will be performed based on it.*/
    public void perform_move(Vote vote) {
        futureStates.clear(); // No future states exist anymore, because the chess position has been updated
        addStateToPreviousStates(new Position_State(state.tiles, state.whites_turn, state.movesTowardsDraw)); // Adds a copy of the current state (before the move has been made) among the previous states.

        switch (vote.get_move().type()) {
            case Move_Type.Standard:
                move_piece(vote.get_coordinates(), vote.get_move());

                if(vote.promotion_piece() != null)
                    this.get_tile(vote.get_move().get_coordinates()).set_piece(create_promotion_piece(vote)); // If the move consisted of a promotion, we promote the piece.

                break;

            case Move_Type.Castle:
                move_piece(vote.get_coordinates(), vote.get_move()); // Moves the king first
                Chess_Tile destination = get_tile(vote.get_move().get_coordinates());
                Coordinates move_coordinates = new Coordinates(vote.get_move().get_coordinates());

                if (destination.get_piece().white()) { // Logic which determines where the rook should move, based on the direction of the castling and the color of the moving side.
                    if (vote.get_move().get_coordinates().get_y() < vote.get_coordinates().get_y()) {
                        move_coordinates.apply_vector(new Coordinates(0, 1));
                        move_piece(new Coordinates(7, 0), new Move(move_coordinates, Move_Type.Standard));
                    } else {
                        move_coordinates.apply_vector(new Coordinates(0, -1));
                        move_piece(new Coordinates(7, 7), new Move(move_coordinates, Move_Type.Standard));
                    }
                } else {
                    if (vote.get_move().get_coordinates().get_y() < vote.get_coordinates().get_y()) {
                        move_coordinates.apply_vector(new Coordinates(0, 1));
                        move_piece(new Coordinates(0, 0), new Move(move_coordinates, Move_Type.Standard));
                    } else {
                        move_coordinates.apply_vector(new Coordinates(0, -1));
                        move_piece(new Coordinates(0, 7), new Move(move_coordinates, Move_Type.Standard));
                    }
                }

                break;

            case Move_Type.En_Passant:
                Coordinates captured_pawn_coordinates = new Coordinates(vote.get_move().get_coordinates());

                if (get_tile(vote.get_coordinates()).get_piece().white()) // We move the captured coordinates by one square to apply en passant properly.
                    captured_pawn_coordinates.apply_vector(new Coordinates(1, 0));
                else
                    captured_pawn_coordinates.apply_vector(new Coordinates(-1, 0));

                Chess_Tile captured_tile = get_tile(captured_pawn_coordinates);
                captured_tile.set_piece(null);
                captured_tile.set_unoccupied();

                move_piece(vote.get_coordinates(), vote.get_move());

                break;
        }

        for(Chess_Tile[] row : state.tiles) // All the pawns are no longer enpassantable, except the one which has moved this turn.
        {
            for(Chess_Tile tile : row)
            {
                Piece piece = tile.get_piece();
                if(piece != null && piece.type() == 'P' && (piece.get_coordinates().get_x() != vote.get_move().get_coordinates().get_x()
                || piece.get_coordinates().get_y() != vote.get_move().get_coordinates().get_y()))
                    ((Pawn)piece).un_passantable();

                if(piece != null)
                    piece.clear_moves();
            }
        }
    }
    /**
     * Returns a linked list with the previous board states. For making sure going back to previous board states is possible.
     * @return Returns a LinkedList of serialized previous board states.*/
    public LinkedList<byte[]> getPreviousStates() {
        return previousStates;
    }
    /**
     * Returns a linked list with the future board states. For making sure going back to the future board states is possible.
     * @return Returns a LinkedList of serialized future board states.*/
    public LinkedList<byte[]> getFutureStates() {
        return futureStates;
    }
    /**
     * Returns the chess position to its previous board state.
     * Involves deserializing the previous board state, serializing the current state and adding it to the future board states,
     * and removing the previous board state from the linked list (since it is now the current state).
     * Also updates the possible moves of the pieces, since the composition of pieces on the board has changed.*/
    public void goBackToPreviousState() {
        addStateToFutureStates(new Position_State(state.tiles, state.whites_turn, state.movesTowardsDraw));
        Position_State newState = deserializeState(previousStates.getFirst());
        if(newState != null) { // In case we're at the beginning of the list
            state.modifyState(newState);
            previousStates.removeFirst();
            update_possible_moves();
        }
    }
    /**
     * Returns the chess position to its next board state.
     * Involves deserializing the future board state, serializing the current state and adding it to the previous board states,
     * and removing the next board state from the linked list (since it is now the current state).
     * Also updates the possible moves of the pieces, since the composition of pieces on the board has changed.*/
    public void goToNextState() {
        addStateToPreviousStates(new Position_State(state.tiles, state.whites_turn, state.movesTowardsDraw));
        Position_State newState = deserializeState(futureStates.getFirst());
        if(newState != null) { // In case we're at the beginning of the list
            state.modifyState(newState);
            futureStates.removeFirst();
            update_possible_moves();
        }
    }
    /**
     * Serializes a position state and then adds it to the linked list of previous position states.
     * @param state Position state which gets serialized and added to the linked list of previous position states.*/
    public void addStateToPreviousStates(Position_State state) {
        byte[] stateByteArray = serializeState(state);
        previousStates.addFirst(stateByteArray);
    }
    /**
     * Serializes a position state and then adds it to the linked list of future position states.
     * @param state Position state which gets serialized and added to the linked list of future position states.*/
    public void addStateToFutureStates(Position_State state) {
        byte[] stateByteArray = serializeState(state);
        futureStates.addFirst(stateByteArray);
    }
    /**
     * Returns a serialized position state.
     * @param state Position state which gets serialized.
     * @return A byte array, which represents a serialized position state.*/
    private byte[] serializeState(Position_State state) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(state);
            out.close();
            return byteArrayOutputStream.toByteArray();
        } catch(IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
    /**
     * Returns a deserialized position state.
     * @param stateByteArray A byte array which represents a serialized position state.
     * @return A Position_State.*/
    private Position_State deserializeState(byte[] stateByteArray) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stateByteArray);
            ObjectInputStream in = new ObjectInputStream(byteArrayInputStream);
            Position_State state = (Position_State) in.readObject();
            in.close();
            return state;
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }
    /**
     * Returns the number of moves towards a draw. Important for determining the end of the game conditions (specifically the 50 move rule).
     * @return An integer, which indicates the number of moves which have been made without advancing a pawn or capturing a piece.*/
    public int getMovesTowardsDraw() {
        return state.movesTowardsDraw;
    }
    /**
     * Increments the number of moves towards a draw. Important for determining the end of the game conditions (specifically the 50 move rule).*/
    public void incrementMovesTowardsDraw() {
        state.movesTowardsDraw++;
    }
    /**
     * Resets the number of moves towards a draw. Important for determining the end of the game conditions (specifically the 50 move rule).*/
    public void resetMovesTowardsDraw() {
        state.movesTowardsDraw = 0;
    }
    /**
     * A nested class, which represents a given chess state. This includes the composition of pieces on the board, whose move it is, and number of moves towards a draw.
     * Different from the chess position mainly so the serialized chess states stored in linked lists representing previous and future board states only store the minimum
     * necessary amount of information, while everything else is store in Chess_Position.*/
    public static class Position_State implements Serializable {
        private final Chess_Tile[][] tiles;
        private boolean whites_turn;
        private int movesTowardsDraw;
        /**
         * Initializes the class variables, as well as copies the pieces from the board_setup parameter and pastes them inside the tiles parameter.
         * @param board_setup A matrix representing the composition of pieces on the board.
         * @param whites_turn A boolean value representing whose move it is.
         * @param movesTowardsDraw An integer value indicating how many moves towards a draw due to the 50 move rule.*/
        Position_State(Chess_Tile[][] board_setup, boolean whites_turn, int movesTowardsDraw) {
            this.tiles = new Chess_Tile[8][8];
            this.whites_turn = whites_turn;
            this.movesTowardsDraw = movesTowardsDraw;

            for (int i = 0; i < 8; i++) { // Clone the pieces from board_setup into tiles.
                for (int j = 0; j < 8; j++) {
                    this.tiles[i][j] = new Chess_Tile(new Coordinates(i, j));

                    Piece current_piece = board_setup[i][j].get_piece();
                    if(current_piece != null)
                        this.tiles[i][j].set_piece(current_piece.clone());
                }
            }
        }
        /**
         * Modifies the current state to be identical to the parameter newState.
         * @param newState A Position_State, based on which the inside parameters of this Position_State will change.*/
        public void modifyState(Position_State newState) {
            whites_turn = newState.whites_turn;
            movesTowardsDraw = newState.movesTowardsDraw;

            for(int i = 0; i < 8; i++) { // Clones the pieces from newState into tiles.
                for(int j = 0; j < 8; j++) {
                    Piece currentPiece = newState.tiles[i][j].get_piece();
                    if(currentPiece != null)
                        this.tiles[i][j].set_piece(currentPiece.clone());
                    else
                        this.tiles[i][j].set_piece(null);
                }
            }
        }
    }
}
