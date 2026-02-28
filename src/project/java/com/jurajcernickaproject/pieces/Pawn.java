package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.gamelogic.Chess_Position;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;
import com.jurajcernickaproject.move.Move_Type;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * A logical representation of a pawn.*/
public class Pawn extends Piece implements Serializable
{
    private boolean has_moved;
    private boolean en_passantable;
    /**
     * Activates the constructor of the superclass based on the parameters and default values for this subclass.
     * @param initial_coordinates A Coordinates object representing the coordinates of this piece.
     * @param is_white A boolean value indicating the color of this piece.
     * @param has_moved A boolean value indicating if this piece has moved before.
     * @param en_passantable A boolean value indicating if it is possible to en passant this piece.*/
    public Pawn(Coordinates initial_coordinates, boolean is_white, boolean has_moved, boolean en_passantable)
    {
        super(initial_coordinates, is_white, 'P');
        this.has_moved = has_moved;
        this.en_passantable = en_passantable;
    }
    /**
     * Checks if en passant is possible to be played in a certain direction. If it can be, it adds en passant as a new move to the list of possible moves for this piece.
     * @param current_position Current chess position. Important for determining which moves can be played.
     * @param direction A Coordinates object which represents a vector showing in which direction we want to check the en passant possibility.*/
    private void check_en_passant(Chess_Position current_position, Coordinates direction)
    {
        if(this.get_coordinates().get_x() != 4 && this.get_coordinates().get_x() != 3) // En passant can only be played if the pawn is in the middle of the board.
            return;

        Coordinates en_passant_check = new Coordinates(this.get_coordinates());
        en_passant_check.apply_vector(direction); // Apply vector on the pawns current coordinates.

        Piece captured_piece = current_position.get_tile(en_passant_check).get_piece();

        if(captured_piece instanceof Pawn && captured_piece.white() != this.white() && ((Pawn) captured_piece).en_passantable()) // If the piece in the specified direction next to the pawn is an en-passantable pawn of a certain color.
        {
            if(this.white()) // Get the right direction for the move based on this piece's color.
                en_passant_check.apply_vector(new Coordinates(-1, 0));
            else
                en_passant_check.apply_vector(new Coordinates(1, 0));

            this.add_move(new Move(en_passant_check, Move_Type.En_Passant));
        }
    }
    /**
     * Create coordinates based on certain parameters. A quality of life function which prevents code duplication.
     * @param direction_if_white A Coordinates object which will be applied like a vector to this piece's coordinates if this piece is white.
     * @param direction_if_black A Coordinates object which will be applied like a vector to this piece's coordinates if this piece is black.
     * @return A new Coordinates object, whose attributes are equal to this piece's coordinates with an applied vector.*/
    private Coordinates create_coordinates(Coordinates direction_if_white, Coordinates direction_if_black)
    {
        Coordinates new_coordinates = new Coordinates(this.get_coordinates());
        if(this.white())
            new_coordinates.apply_vector(direction_if_white);
        else
            new_coordinates.apply_vector(direction_if_black);

        return new_coordinates;
    }
    /**
     * Checks if capture is possible to be played in a certain direction. If it can be, it adds the capture as a new move to the list of possible moves for this piece.
     * @param current_position Current chess position. Important for determining which moves can be played.
     * @param coordinates Coordinates object which represents the coordinates of the captured piece.
     * @param outOfBoundsCondition A function reference, which references a function returning a boolean value specifying if the capture is out of bounds of the board.*/
    private void checkCapture(Chess_Position current_position, Coordinates coordinates, Predicate<Coordinates> outOfBoundsCondition)
    {
        if (outOfBoundsCondition.test(coordinates)) {
            Piece captured_piece = current_position.get_tile(coordinates).get_piece();
            if (captured_piece != null && captured_piece.white() != this.white())
                this.add_move(new Move(coordinates, Move_Type.Standard));
        }
    }
    /**
     * Checks if moving forward is possible to be played. If it can be, it adds it as a new move to the list of possible moves for this piece.
     * @param current_position Current chess position. Important for determining which moves can be played.*/
    private void check_move_forward(Chess_Position current_position)
    {
        Coordinates coordinates_move_forward = create_coordinates(new Coordinates(-1, 0), new Coordinates(1, 0));
        Piece captured_piece = current_position.get_tile(coordinates_move_forward).get_piece();
        if (captured_piece == null)
            this.add_move(new Move(coordinates_move_forward, Move_Type.Standard)); // Move forward one square is possible.

        if(!this.has_moved && captured_piece == null) // A pawn can move two squares if it hasn't moved before.
        {
            if (this.white())
                coordinates_move_forward.apply_vector(new Coordinates(-1, 0)); // Forward means specific directions based on whose color this piece is.
            else
                coordinates_move_forward.apply_vector(new Coordinates(1, 0));

            if(coordinates_move_forward.get_x() > 8 || coordinates_move_forward.get_x() < -1) // Can't move out of bounds of the board.
                return;

            captured_piece = current_position.get_tile(coordinates_move_forward).get_piece();
            if(captured_piece == null)
                this.add_move(new Move(coordinates_move_forward, Move_Type.Standard)); // Move forward two squares is possible
        }
    }
    /**
     * Overriden function from the superclass which calculates regular moves. For a pawn, a regular move is capturing the piece in front of it on a diagonal. The rules for
     * this kind of capture are very specific, and a unique implementation of this function is in order.*/
    @Override
    public void calculate_moves(Chess_Position current_position)
    {
        Coordinates leftCapture = create_coordinates(new Coordinates(-1, -1), new Coordinates(1, -1));
        Coordinates rightCapture = create_coordinates(new Coordinates(-1, 1), new Coordinates(1, 1));

        checkCapture(current_position, leftCapture, c -> c.get_y() > -1 && c.get_x() > -1 && c.get_x() < 8); // Passing a lambda reference.
        checkCapture(current_position, rightCapture, c -> c.get_y() < 8 && c.get_x() > -1 && c.get_x() < 8);
    }
    /**
     * Overriden function from the superclass which calculates special moves. In this case, it calculates if en passant of moving forward is possible and if they are, it adds them to the list of possible moves for this piece.
     * @param current_position The current chess position, important for determining which moves are possible.*/
    @Override
    public void calculate_special_moves(Chess_Position current_position)
    {
        check_move_forward(current_position);

        if(this.get_coordinates().get_y() > 0)
            check_en_passant(current_position, new Coordinates(0, -1));
        if(this.get_coordinates().get_y() < 7)
            check_en_passant(current_position, new Coordinates(0, 1));
    }
    /**
     * An overriden function for updating the piece's coordinates. A Pawn type object also updates the has_moved attribute and the en_passantable attribute.
     * @param new_coordinates A Coordinates object representing the new coordinates of this piece.*/
    @Override
    public void update(Coordinates new_coordinates)
    {
        this.en_passantable = false;
        int x_coordinate_difference = this.get_coordinates().get_x() - new_coordinates.get_x();
        if(x_coordinate_difference > 1 || x_coordinate_difference < -1) // If this pawn has moved by more than two tiles this turn.
            this.en_passantable = true; // It is possible to en passant this pawn.

        super.update(new_coordinates);
        this.has_moved = true;
    }
    /**
     * Returns a new instance of this type of object with the same attributes as this one.
     * @return A new Pawn object, which is effectively the same as this one.*/
    @Override
    public Piece clone()
    {
        return new Pawn(this.get_coordinates(), this.white(), this.has_moved, this.en_passantable);
    }
    /**
     * Returns a boolean value indicating if this pawn can be en passanted.
     * @return The attribute en_passantable.*/
    public boolean en_passantable()
    {
        return this.en_passantable;
    }
    /**
     * Sets this pawn to be impossible to en passant by changing the en_passantable attribute to false.*/
    public void un_passantable()
    {
        this.en_passantable = false;
    }
}
