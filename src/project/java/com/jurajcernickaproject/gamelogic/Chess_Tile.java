package com.jurajcernickaproject.gamelogic;

import com.jurajcernickaproject.pieces.Piece;
import com.jurajcernickaproject.move.Coordinates;

import java.io.Serializable;

/**
 * Represents a single chess tile on the board from a logical perspective. Has certain coordinates, and can contain a piece. Also contains any functions manipulating chess tiles.*/
public class Chess_Tile implements Serializable
{
    private Piece piece;
    private final Coordinates coordinates;
    private boolean occupied;
    /**
     * Initializes attributes either to their default values or based on the parameter.
     * @param new_coordinates The coordinates of the newly created Chess_Tile.*/
    public Chess_Tile(Coordinates new_coordinates)
    {
        this.occupied = false;
        this.coordinates = new_coordinates;
        this.piece = null;
    }
    /**
     * Returns the coordinates of this chess tile.
     * @return Coordinates of this chess tile.*/
    public Coordinates get_coordinates()
    {
        return this.coordinates;
    }
    /**
     * Sets the piece attribute to a give piece based on the parameter.
     * @param piece Piece which this tile is now occupied by.*/
    public void set_piece(Piece piece)
    {
        this.piece = piece;
        this.occupied = piece != null;
    }
    /**
     * Returns the piece attribute.
     * @return Piece attribute.*/
    public Piece get_piece()
    {
        if(this.piece != null)
            return this.piece;
        else
            return null;
    }
    /**
     * Returns the occupied attribute, which is a boolean function indicating if a piece is occupying this square.
     * @return occupied attribute.*/
    public boolean occupied()
    {
        return this.occupied;
    }
    /**
     * Sets the occupied attribute as true. Called when a piece moves on a tile.*/
    public void set_occupied()
    {
        this.occupied = true;
    }
    /**
     * Sets the occupied attribute as false. Called when a piece leaves a tile.*/
    public void set_unoccupied()
    {
        this.occupied = false;
    }
    /**
     * A static function, which copies one matrix of chess tiles to another matrix of chess tiles, while cloning all the pieces occupying these tiles.
     * @param original_tiles Matrix of chess tiles which gets copied.
     * @return A new matrix of chess tiles which contains the same composition of pieces.*/
    public static Chess_Tile[][] copy_tiles(Chess_Tile[][] original_tiles)
    {
        Chess_Tile[][] new_tiles = new Chess_Tile[8][8];

        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Chess_Tile original_tile = original_tiles[i][j];
                new_tiles[i][j] = new Chess_Tile(original_tile.get_coordinates());
                if(original_tile.get_piece() != null)
                    new_tiles[i][j].set_piece(original_tile.get_piece().clone());
            }
        }

        return new_tiles;
    }
}
