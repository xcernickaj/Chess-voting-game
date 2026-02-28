package com.jurajcernickaproject.gamelogic;

import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.move.Move;

/**
 * Represents a vote. There will only ever be 5 different votes each round which the admin chooses, but each one of them will gain weight whenever a player chooses it.
 * This way any duplicate votes will be reduced to an incremented integer, but the downside is a bit less intuitive code, as the votes might work differently than would be expected.
 * Implements Comparable, so we can easily find the vote with the largest weight.*/
public class Vote implements Comparable<Vote> {
    private final Coordinates piece_coordinates;
    private final Move move;
    private final String promotion_piece;
    int weight;
    /**
     * Initializes attributes either to their default values or based on the parameters.
     * @param piece_coordinates The coordinates of the moving piece.
     * @param move The move the piece will execute.
     * @param promotion_piece Which piece the moving piece will get promoted to after it moves, usually null.*/
    public Vote(Coordinates piece_coordinates, Move move, String promotion_piece)
    {
        this.piece_coordinates = piece_coordinates;
        this.move = move;
        this.promotion_piece = promotion_piece;
        this.weight = 1;
    }
    /**
     * Returns the coordinates of the moving piece.
     * @return The attribute piece_coordinates.*/
    public Coordinates get_coordinates()
    {
        return this.piece_coordinates;
    }
    /**
     * Returns the move which the vote is advocating for.
     * @return The attribute move.*/
    public Move get_move()
    {
        return this.move;
    }
    /**
     * Returns the name of the type of piece the moving piece would promote into.
     * @return String attribute promotion_piece.*/
    public String promotion_piece()
    {
        return promotion_piece;
    }
    /**
     * Returns the weight of this vote (meaning how many players voted for this move).
     * @return An integer attribute weight.*/
    public int get_weight()
    {
        return this.weight;
    }
    /**
     * Increments the integer attribute weight. Gets called when a new player voted for this exact move.*/
    public void increment_weight()
    {
        this.weight++;
    }
    /**
     * Compares the attributes of this vote with another vote. Gets called when determining which vote's weight to increment after a player has voted.
     * @param vote The vote which will be compared to this vote.
     * @return A boolean value indicating if the two votes have the same attributes and are effectively equal.*/
    public boolean compare(Vote vote)
    {
        return this.piece_coordinates.get_x() == vote.get_coordinates().get_x()
                && this.piece_coordinates.get_y() == vote.get_coordinates().get_y()
                && this.move.get_coordinates().get_x() == vote.get_move().get_coordinates().get_x() &&
                this.move.get_coordinates().get_y() == vote.get_move().get_coordinates().get_y()
                && this.move.type() == vote.move.type()
                && (this.promotion_piece == null || this.promotion_piece.equals(vote.promotion_piece));
    }
    /**
     * In order to find the vote with the largest weight.
     * @param other The vote we are comparing with this one.
     * @return An integer indicating which vote has the larger weight.*/
    @Override
    public int compareTo(Vote other)
    {
        return Integer.compare(other.get_weight(), this.weight);
    }
}
