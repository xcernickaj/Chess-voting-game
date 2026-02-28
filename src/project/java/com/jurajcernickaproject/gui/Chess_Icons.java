package com.jurajcernickaproject.gui;

import com.jurajcernickaproject.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * An abstract class used for getting the proper chess images for each piece.*/
public abstract class Chess_Icons
{
    private static ImageIcon[] icons;
    /**
     * One time use function, which gets all the images based on the proper file path and saves them inside an array.*/
    public static void load_chess_icons()
    {
        String[] image_names = new String[12];
        image_names[0] = "BlackPawn.png";
        image_names[1] = "WhitePawn.png";
        image_names[2] = "BlackKnight.png";
        image_names[3] = "WhiteKnight.png";
        image_names[4] = "BlackBishop.png";
        image_names[5] = "WhiteBishop.png";
        image_names[6] = "BlackRook.png";
        image_names[7] = "WhiteRook.png";
        image_names[8] = "BlackQueen.png";
        image_names[9] = "WhiteQueen.png";
        image_names[10] = "BlackKing.png";
        image_names[11] = "WhiteKing.png";

        icons = new ImageIcon[12];

        try
        {
            for(int i = 0; i < 12; i++)
            {
                BufferedImage originalImage = ImageIO.read(new File(".\\src\\project\\java\\com\\jurajcernickaproject\\img\\" + image_names[i]));
                Image scaledImage = originalImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                icons[i] = new ImageIcon(scaledImage);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Returns the corresponding image for a piece inside a JLabel.
     * @param piece The piece which needs its corresponding image.
     * @return A JLabel containing the piece image.*/
    public static JLabel get_label(Piece piece)
    {
        switch(piece.type())
        {
            case 'R':
                if(piece.white())
                    return new JLabel(icons[7]);
                else
                    return new JLabel(icons[6]);
            case 'B':
                if(piece.white())
                    return new JLabel(icons[5]);
                else
                    return new JLabel(icons[4]);
            case 'Q':
                if(piece.white())
                    return new JLabel(icons[9]);
                else
                    return new JLabel(icons[8]);
            case 'N':
                if(piece.white())
                    return new JLabel(icons[3]);
                else
                    return new JLabel(icons[2]);
            case 'K':
                if(piece.white())
                    return new JLabel(icons[11]);
                else
                    return new JLabel(icons[10]);
            case 'P':
                if(piece.white())
                    return new JLabel(icons[1]);
                else
                    return new JLabel(icons[0]);
            default:
        }
        return null;
    }
}
