package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.gamelogic.Chess_Position;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;
import com.jurajcernickaproject.move.Move_Type;

import java.io.Serializable;

/**
 * A logical representation of a king.*/
public class King extends Short_Range_Piece implements Serializable
{
    private boolean has_moved;
    /**
     * Activates the constructor of the superclass based on the parameters and default values for this subclass.
     * @param initial_coordinates A Coordinates object representing the coordinates of this piece.
     * @param is_white A boolean value indicating the color of this piece.
     * @param has_moved A boolean value indicating if this piece has moved before.*/
    public King(Coordinates initial_coordinates, boolean is_white, boolean has_moved)
    {
        super(initial_coordinates, is_white, false, 'K');
        this.has_moved = has_moved;
    }
    /**
     * An overriden function for updating the piece's coordinates. A King type object also updates the has_moved attribute.
     * @param new_coordinates A Coordinates object representing the new coordinates of this piece.*/
    @Override
    public void update(Coordinates new_coordinates) {
        super.update(new_coordinates);
        this.has_moved = true;
    }
    /**
     * Returns a new instance of this type of object with the same attributes as this one.
     * @return A new King object, which is effectively the same as this one.*/
    @Override
    public Piece clone()
    {
        return new King(this.get_coordinates(), this.white(), this.has_moved);
    }
    /**
     * Determines if the king can perform castle in a specific direction.
     * @param position The current chess position. Needed to determine the state of the board and thus if the king can safely castle.
     * @param corner_piece The piece in the corner of the board. Needs to be a rook which hasn't moved yet.
     * @param left_side A boolean value indicating which side castle is the function supposed to check.
     * @return A boolean value indicating if the king can castle in this direction.*/
    private boolean castle_possible(Chess_Position position, Piece corner_piece, boolean left_side)
    {
        if(!(corner_piece instanceof Rook) || ((Rook) corner_piece).has_moved())
            return false;

        if(position.in_check()) // Can't castle in check
            return false;

        Coordinates direction = new Coordinates(0, -1);
        if(left_side)
            direction.set_coordinates(new Coordinates(0, 1));

        int i = 0;
        Coordinates current_position = new Coordinates(this.get_coordinates());
        current_position.apply_vector(direction);
        while(current_position.get_y() != corner_piece.get_coordinates().get_y()) // Goes through all tiles between the king and the rook in the corner. All of these tiles have to be empty.
        {
            if (position.get_tile(current_position).occupied()) // Checks if the tile in the way is empty.
                return false;

            if(i < 2 && puts_king_in_check(position, new Move(current_position, Move_Type.Standard))) // No pieces can be attacking on the tiles through which the king is supposed to move when castling. So we create a new position where the king is on this tile and check if it is in check.
                return false;

            current_position.apply_vector(direction);
            i++;
        }

        return true;
    }
    /**
     * Adds castling as a possible move for this piece.
     * @param left_side A boolean value indicating the direction of this castling move.*/
    private void add_castle_move(boolean left_side)
    {
        Coordinates move_coordinates;
        if(left_side)
            move_coordinates = new Coordinates(this.get_coordinates().get_x(), this.get_coordinates().get_y() + 2); // Adds the move on the tile 2 tiles to the left of this piece.
        else
            move_coordinates = new Coordinates(this.get_coordinates().get_x(), this.get_coordinates().get_y() - 2); // Adds the move on the tile 2 tiles to the right of this piece.

        super.add_move(new Move(move_coordinates, Move_Type.Castle));
    }
    /**
     * Overriden function from the superclass which calculates special moves. In this case, it calculates if castling is possible and if it is, it adds it to the list of possible moves.
     * @param position The current chess position, important for determining which moves are possible.*/
    @Override
    public void calculate_special_moves(Chess_Position position)
    {
        if(!this.has_moved) // The King couldn't have moved for castling to be possible.
        {
            int first_row = 0;
            if(super.white())
                first_row = 7; // Determining where the first row for this side is.

            Piece corner_piece = position.get_tile(new Coordinates(first_row, 0)).get_piece();
            if(castle_possible(position, corner_piece, false)) // Tries if right side castling is possible.
                add_castle_move(false);

            corner_piece = position.get_tile(new Coordinates(first_row, 7)).get_piece();
            if(castle_possible(position, corner_piece, true)) // Tries if left side castling is possible.
                add_castle_move(true);
        }
    }
}
