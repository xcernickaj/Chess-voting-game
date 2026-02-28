package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.gamelogic.Chess_Position;
import com.jurajcernickaproject.gamelogic.Chess_Tile;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;
import com.jurajcernickaproject.move.Move_Type;

import java.io.Serializable;

/**
 * A logical representation of long range pieces. This includes pieces which can move any number of tiles in a certain direction as long as no pieces stand in the way.*/
public abstract class Long_Range_Piece extends Piece implements Serializable, MovementVectors
{
    /**
     * None of the long range pieces perform any moves which would be considered special moves, and thus the overriden function is empty.
     * @param current_position Current chess position. Important for determining which moves can be played. In this case, it is unused.*/
    @Override
    public void calculate_special_moves(Chess_Position current_position)
    {

    }
    /**
     * Initializes attributes based on the parameters.
     * @param initial_coordinates The Coordinates of this piece.
     * @param is_white A boolean value indicating the color of this piece.
     * @param diagonal A boolean value indicating if this piece can move diagonally.
     * @param vertical A boolean value indicating if this piece can move vertically.
     * @param type A char value indicating the type of this piece.*/
    Long_Range_Piece(Coordinates initial_coordinates, boolean is_white, boolean diagonal, boolean vertical, char type)
    {
        super(initial_coordinates, is_white, type);

        if(diagonal) {
            this.add_move_vector(getDiagonalVectors());
        }
        if(vertical) {
            this.add_move_vector(getVerticalVectors());
        }
    }
    /**
     * Calculates which moves can be played by this piece. It uses the piece's move vectors to determine this. Specifically designed for long range pieces.
     * @param position Current chess position. Important for determining which moves can be played.*/
    @Override
    public void calculate_moves(Chess_Position position)
    {
        for (Coordinates move_vector : this.get_move_vectors())
        {
            Coordinates current_coordinates = new Coordinates(this.get_coordinates());
            current_coordinates.apply_vector(move_vector); // Apply a move vector to this piece's coordinates to determine which tile it would land on if it moved in this direction.

            while (current_coordinates.get_x() < 8 && current_coordinates.get_x() > -1
                    && current_coordinates.get_y() < 8 && current_coordinates.get_y() > -1) { // As long as we are not out of bounds when moving in this direction.
                Chess_Tile current_tile = position.get_tile(current_coordinates);
                Piece current_piece = current_tile.get_piece();

                if (current_piece != null && current_piece.white() == this.white()) // A piece can't move on a tile occupied by a piece of the same color.
                    break;

                this.add_move(new Move(current_coordinates, Move_Type.Standard));

                if (current_piece != null && current_piece.white() != this.white()) // A long ranged piece cannot jump over enemy pieces.
                    break;

                current_coordinates.apply_vector(move_vector);
            }
        }
    }
}
