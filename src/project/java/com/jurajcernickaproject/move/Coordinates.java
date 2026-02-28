package com.jurajcernickaproject.move;

import java.io.Serializable;
/**
 * A representation of coordinates. Used all throughout the project for logic calculations and proper functionality of the chess game. Often used like vectors.*/
public class Coordinates implements Serializable
{
    private int x_coordinate;
    private int y_coordinate;
    /**
     * Initializes the attributes based on integer parameters.
     * @param x_coordinate An integer value representing the x coordinate.
     * @param y_coordinate An integer value representing the y coordinate.*/
    public Coordinates(int x_coordinate, int y_coordinate)
    {
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }
    /**
     * Initializes the attributes based on a Coordinates object parameter.
     * @param coordinates A Coordinates object whose attributes are getting copied to this one.*/
    public Coordinates(Coordinates coordinates)
    {
        this.x_coordinate = coordinates.get_x();
        this.y_coordinate = coordinates.get_y();
    }
    /**
     * Returns an integer value representing the x coordinate.
     * @return The x_coordinate attribute.*/
    public int get_x()
    {
        return this.x_coordinate;
    }
    /**
     * Returns an integer value representing the x coordinate.
     * @return The y_coordinate attribute.*/
    public int get_y()
    {
        return this.y_coordinate;
    }
    /**
     * Sets the attributes to be equal to those of the Coordinates object parameter.
     * @param new_coordinates A Coordinates object whose attributes are getting copied to this one.*/
    public void set_coordinates(Coordinates new_coordinates)
    {
        this.x_coordinate = new_coordinates.get_x();
        this.y_coordinate = new_coordinates.get_y();
    }
    /**
     * Takes a Coordinates object as the parameter and treats it like a vector to change the attributes of this object.
     * @param vector A Coordinates object which gets treated like a vector and applied to this object.*/
    public void apply_vector(Coordinates vector)
    {
        this.x_coordinate += vector.get_x();
        this.y_coordinate += vector.get_y();
    }
}
