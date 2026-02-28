package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.gamelogic.Chess_Position;
import com.jurajcernickaproject.gamelogic.Chess_Tile;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;
import com.jurajcernickaproject.gamelogic.Vote;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * The abstract logical representation of a chess piece. It is the root of the most important hierarchy in the project. Contains all the universal methods and attributes for chess pieces. */
public abstract class Piece implements Serializable
{
    private final char type;
    private final LinkedList<Coordinates> move_vectors;
    private Coordinates coordinates;
    private final boolean is_white;
    private LinkedList<Move> moves;
    /**
     * Every chess piece has to implement a method for calculating the moves for that piece.
     * @param current_position Current chess position. Important for determining the possible moves for a piece.*/
    abstract public void calculate_moves(Chess_Position current_position);
    /**
     * Every chess piece has to implement a method for calculating the special moves for that piece. If a piece has no special moves, it implements an empty method.
     * @param current_position Current chess position. Important for determining the possible moves for a piece.*/
    abstract public void calculate_special_moves(Chess_Position current_position);
    /**
     * The root of the prototype design pattern. Implementations create a clone of that particular piece.*/
    abstract public Piece clone();
    /**
     * Initializes the attributes to default values and based on the parameters.
     * @param initial_coordinates A Coordinates object representing the coordinates of this piece.
     * @param is_white A boolean value indicating the color of this piece.
     * @param new_type A char value indicating the type of this piece.*/
    Piece(Coordinates initial_coordinates, boolean is_white, char new_type)
    {
        this.is_white = is_white;
        this.moves = new LinkedList<>();
        this.coordinates = new Coordinates(initial_coordinates.get_x(), initial_coordinates.get_y());
        this.move_vectors = new LinkedList<>();
        this.type = new_type;
    }
    /**
     * Returns a boolean value indicating the color of this piece.
     * @return The attribute is_white.*/
    public boolean white()
    {
        return this.is_white;
    }
    /**
     * Returns a Coordinate object representing the coordinates of this piece.
     * @return The coordinates attribute.*/
    public Coordinates get_coordinates()
    {
        return this.coordinates;
    }
    /**
     * Returns a LinkedList of moves representing the possible moves this piece can make this turn.
     * @return The moves attribute.*/
    public LinkedList<Move> get_moves()
    {
        return this.moves;
    }
    /**
     * Add a new move to the list of possible moves this piece can make this turn.
     * @param new_move A new move which is added to the list of moves this piece can make.*/
    public void add_move(Move new_move)
    {
        this.moves.add(new_move);
    }
    /**
     * The moves attribute is reset to a new empty LinkedList. Gets called after the position state has changed, and moves which have previously been
     * possible might not be possible anymore.*/
    public void clear_moves()
    {
        this.moves = new LinkedList<>();
    }
    /**
     * Returns a boolean value indicating if a certain move puts king in check. Creates an alternative position where that move has been played, and then
     * checks the movements of all the enemy pieces. If any of them can capture the king in this alternative position, returns true, otherwise false.
     * @param position The current chess position, important for determining if the king is in check.
     * @param move The move whose credibility we want to check.
     * @return A boolean value indicating if a certain move puts the king in check.*/
    public boolean puts_king_in_check(Chess_Position position, Move move)
    {
        Chess_Position alternative_position = new Chess_Position(Chess_Tile.copy_tiles(position.get_tiles()), position.get_turn());
        alternative_position.perform_move(new Vote(this.get_coordinates(), move, null));

        return alternative_position.in_check();
    }
    /**
     * Sets the possible moves for this piece based on the current position.
     * @param position Current chess position. Important for determining which moves are possible for this piece.
     * @return A boolean value indicating if at least one move is possible. Important for checking end game conditions (specifically checkmate and stalemate).*/
    public boolean set_possible_moves(Chess_Position position)
    {
        this.calculate_moves(position);
        this.calculate_special_moves(position);

        LinkedList<Move> possible_moves = this.get_moves();
        possible_moves.removeIf(possible_move -> puts_king_in_check(position, possible_move));

        return !possible_moves.isEmpty();
    }
    /**
     * Updates the coordinates of this piece to the new coordinates. Gets called after a piece has moved.
     * @param new_coordinates Coordinates object, which is the new value for the coordinates attribute.*/
    public void update(Coordinates new_coordinates)
    {
        this.coordinates = new_coordinates;
    }
    /**
     * Adds a move vector to the list of move vectors for this piece. Move vectors define how a piece can move.
     * @param move_vectors LinkedList of Coordinates, which are treated like vectors. All of them get added to the move_vectors attribute.*/
    public void add_move_vector(LinkedList<Coordinates> move_vectors)
    {
        this.move_vectors.addAll(move_vectors);
    }
    /**
     * Returns the LinkedList of move vectors. Move vectors define how a piece can move.
     * @return The attribute move_vectors.*/
    public LinkedList<Coordinates> get_move_vectors()
    {
        return this.move_vectors;
    }
    /**
     * Returns the type of the piece in the form of a char. Each piece type has a corresponding symbol.
     * @return The type attribute.*/
    public char type()
    {
        return this.type;
    }
}
