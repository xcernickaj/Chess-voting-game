package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.move.Coordinates;

import java.io.Serializable;

/**
 * A logical representation of a rook.*/
public class Rook extends Long_Range_Piece implements Serializable
{
    private boolean has_moved;
    /**
     * Activates the constructor of the superclass based on the parameters and default values for this subclass.
     * @param initial_coordinates A Coordinates object representing the coordinates of this piece.
     * @param is_white A boolean value indicating the color of this piece.
     * @param has_moved A boolean value indicating if this piece has moved before.*/
    public Rook(Coordinates initial_coordinates, boolean is_white, boolean has_moved)
    {
        super(initial_coordinates, is_white, false, true, 'R');
        this.has_moved = has_moved;
    }
    /**
     * An overriden function for updating the piece's coordinates. A Rook type object also updates the has_moved attribute.
     * @param new_coordinates A Coordinates object representing the new coordinates of this piece.*/
    @Override
    public void update(Coordinates new_coordinates)
    {
        super.update(new_coordinates);
        this.has_moved = true;
    }
    /**
     * Returns a new instance of this type of object with the same attributes as this one.
     * @return A new Rook object, which is effectively the same as this one.*/
    @Override
    public Piece clone()
    {
        return new Rook(this.get_coordinates(), this.white(), this.has_moved);
    }
    /**
     * Returns a boolean value indicating if this piece has moved before.
     * @return The attribute has_moved.*/
    public boolean has_moved()
    {
        return this.has_moved;
    }
}
