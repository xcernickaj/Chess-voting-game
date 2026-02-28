package com.jurajcernickaproject.gui;

import com.jurajcernickaproject.gamelogic.Chess_Tile;
import com.jurajcernickaproject.move.Move;

import javax.swing.*;
import java.awt.*;
/**
 * A GUI element representing a single tile on a chess board. Includes functions which change the color of the tile when it gets highlighted or hovered over with a cursor.*/
public class Tile_Panel extends JPanel
{
    private static final int SQUARE_SIZE = 85;
    private Move move;
    private boolean highlighted;
    private boolean selected;
    private final Chess_Tile tile;
    private final boolean white;
    private boolean chosen;
    /**
     * Returns the boolean value which represents wherever this tile is chosen or not.
     * @return The attribute chosen.*/
    public boolean chosen()
    {
        return this.chosen;
    }
    /**
     * Sets this tile to be chosen. A chosen tile has a green border around it.*/
    public void set_chosen()
    {
        this.chosen = true;
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
    }
    /**
     * Returns the default color of this specific tile.
     * @return A Color object which is either white or black, based on the color of the tile.*/
    public Color default_color()
    {
        if(this.white)
            return new Color(245, 246, 247);
        else
            return new Color(180, 181, 179);
    }
    /**
     * Returns the hovered over color of this specific tile.
     * @return A Color object which is either a shade of white or a shade of black, based on the color of the tile.*/
    public Color hovered_color()
    {
        if(this.white)
            return new Color(233, 234, 224);
        else
            return new Color(168, 167, 145);
    }
    /**
     * Returns the highlighted color of this specific tile.
     * @return A Color object which is either light yellow or dark yellow, based on the color of the tile.*/
    public Color highlighted_color()
    {
        if(this.white)
            return new Color(245, 246, 234);
        else
            return new Color(180, 181, 157);
    }
    /**
     * Returns a boolean value indicating if this tile is selected.
     * @return The selected attribute.*/
    public boolean selected()
    {
        return this.selected;
    }
    /**
     * Returns a Chess_Tile object which this object implementation is a visual representation of.
     * @return The tile attribute.*/
    public Chess_Tile get_tile()
    {
        return this.tile;
    }
    /**
     * Returns a boolean value indicating if this tile is white.
     * @return The white attribute.*/
    public boolean white()
    {
        return this.white;
    }
    /**
     * Sets this tile's color to its default color.
     * @param keepBorder A boolean value indicating if the tile should keep its green border.*/
    public void set_default(boolean keepBorder)
    {
        this.highlighted = false;
        this.selected = false;
        this.chosen = false;
        this.setBackground(this.default_color());
        if(!keepBorder)
            this.setBorder(null);
    }
    /**
     * Sets this tile's color to its highlighted color.*/
    public void set_highlighted()
    {
        this.highlighted = true;
        this.selected = false;
        this.setBackground(this.highlighted_color());
    }
    /**
     * Sets this tile's color to its selected color.*/
    public void set_selected()
    {
        this.highlighted = false;
        this.selected = true;
        this.setBackground(new Color(228, 229, 172));
    }
    /**
     * Returns a boolean value indicating if this tile is highlighted.
     * @return The highlighted attribute.*/
    public boolean highlighted()
    {
        return this.highlighted;
    }
    /**
     * Initializes the attributes to their default values or based on parameters. Also sets the layout.
     * @param isWhite A boolean value indicating the color of the tile.
     * @param current_tile A Chess_Tile object which is the logic representation of this tile.*/
    public Tile_Panel(boolean isWhite, Chess_Tile current_tile)
    {
        Color color = isWhite ? new Color(245, 246, 247) : new Color(180, 181, 179);
        setBackground(color);
        this.white = isWhite;
        this.highlighted = false;
        this.selected = false;
        this.tile = current_tile;
        setLayout(new BorderLayout());
    }
    /**
     * Returns a Dimension object representing the preferred size of a tile.
     * @return A new Dimension object whose parameters is the SQUARE_SIZE attribute.*/
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(SQUARE_SIZE, SQUARE_SIZE);
    }
    /**
     * Returns an integer value indicating the preferred width or height of a tile.
     * @return The SQUARE_SIZE attribute.*/
    public static int get_preferred_size_int()
    {
        return SQUARE_SIZE;
    }
    /**
     * Sets the move attribute based on the parameter. If the highlighted piece can move on a tile, this tile will have a Move object as its attribute. When this tile is
     * clicked, the move attribute of the tile is used to determine which move has been chosen by the player.
     * @param new_move A Move object, which is the new value for the move attribute.*/
    public void set_move(Move new_move)
    {
        this.move = new_move;
    }
    /**
     * Returns a Move object. If the highlighted piece can move on a tile, this tile will have a Move object as its attribute. When this tile is
     * clicked, the move attribute of the tile is used to determine which move has been chosen by the player.*/
    public Move get_move()
    {
        return this.move;
    }
}
