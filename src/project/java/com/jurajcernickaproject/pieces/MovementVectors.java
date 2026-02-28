package com.jurajcernickaproject.pieces;

import com.jurajcernickaproject.move.Coordinates;

import java.util.LinkedList;

/**
 * An interface which serves as a generator of move vectors for different types of pieces. Pieces then use these move vectors to determine which tiles they can move on.*/
public interface MovementVectors {
    /**
     * Returns a list of all vertical move vectors. These vectors are for long range pieces, and so they are only one tile in length. Long range pieces will
     * keep applying the same vector over and over again until hitting a barrier, effectively travelling any number of tiles in the specified direction.
     * @return LinkedList of Coordinates objects, which represent the move vectors.*/
    default LinkedList<Coordinates> getVerticalVectors() {
        LinkedList<Coordinates> ret = new LinkedList<>();

        ret.add(new Coordinates(1, 0));
        ret.add(new Coordinates(-1, 0));
        ret.add(new Coordinates(0, 1));
        ret.add(new Coordinates(0, -1));

        return ret;
    }
    /**
     * Returns a list of all diagonal move vectors. These vectors are for long range pieces, and so they are only one tile in length. Long range pieces will
     * keep applying the same vector over and over again until hitting a barrier, effectively travelling any number of tiles in the specified direction.
     * @return LinkedList of Coordinates objects, which represent the move vectors.*/
    default LinkedList<Coordinates> getDiagonalVectors() {
        LinkedList<Coordinates> ret = new LinkedList<>();

        ret.add(new Coordinates(1, 1));
        ret.add(new Coordinates(1, -1));
        ret.add(new Coordinates(-1, -1));
        ret.add(new Coordinates(-1, 1));

        return ret;
    }
    /**
     * Returns a list of all king's move vectors.
     * @return LinkedList of Coordinates objects, which represent the move vectors.*/
    default LinkedList <Coordinates> getKingVectors() {
        LinkedList<Coordinates> ret = new LinkedList<>();

        ret.add(new Coordinates(1, 1));
        ret.add(new Coordinates(1, -1));
        ret.add(new Coordinates(-1, -1));
        ret.add(new Coordinates(-1, 1));

        ret.add(new Coordinates(1, 0));
        ret.add(new Coordinates(-1, 0));
        ret.add(new Coordinates(0, 1));
        ret.add(new Coordinates(0, -1));

        return ret;
    }
    /**
     * Returns a list of all knight's move vectors.
     * @return LinkedList of Coordinates objects, which represent the move vectors.*/
    default LinkedList <Coordinates> getKnightVectors() {
        LinkedList<Coordinates> ret = new LinkedList<>();

        int x = 2; // A mathematical way to calculate the different directions a knight can move.
        int y = 1;
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 2; j++)
            {
                for(int k = 0; k < 2; k++)
                {
                    ret.add(new Coordinates(x, y));
                    x *= -1;
                }
                y *= -1;
            }
            x = 1;
            y = 2;
        }

        return ret;
    }
}
