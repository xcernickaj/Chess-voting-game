package com.jurajcernickaproject.move;

/**
 * A representation of a move. Extends the class Coordinates. Contains the coordinates which a moving piece will move into as well as the move type.*/
public class Move extends Coordinates
{
    private final Move_Type type;
    /**
     * Initializes the attributes based on parameters.
     * @param initial_coordinates A Coordinates object representing the coordinates the moving piece will move into.
     * @param move_type An enum type representing the type of move.*/
    public Move(Coordinates initial_coordinates, Move_Type move_type)
    {
        super(initial_coordinates);
        this.type = move_type;
    }
    /**
     * Returns the coordinates the moving piece will move into when performing this move.
     * @return new Coordinates object containing the coordinates the moving piece will move on.*/
    public Coordinates get_coordinates()
    {
        return new Coordinates(super.get_x(), super.get_y());
    }
    /**
     * Returns the type of this move.
     * @return Move_Type enum attribute type.*/
    public Move_Type type()
    {
        return this.type;
    }
}
