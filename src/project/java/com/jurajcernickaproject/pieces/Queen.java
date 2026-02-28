package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.move.Coordinates;

import java.io.Serializable;

/**
 * A logical representation of a queen.*/
public class Queen extends Long_Range_Piece implements Serializable
{
    /**
     * Activates the constructor of the superclass based on the parameters and default values for this subclass.
     * @param initial_coordinates A Coordinates object representing the coordinates of this piece.
     * @param is_white A boolean value indicating the color of this piece.*/
    public Queen(Coordinates initial_coordinates, boolean is_white)
    {
        super(initial_coordinates, is_white, true, true, 'Q');
    }
    /**
     * Returns a new instance of this type of object with the same attributes as this one.
     * @return A new Queen object, which is effectively the same as this one.*/
    @Override
    public Piece clone()
    {
        return new Queen(this.get_coordinates(), this.white());
    }
}
