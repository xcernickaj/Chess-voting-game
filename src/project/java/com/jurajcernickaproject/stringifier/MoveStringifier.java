package com.jurajcernickaproject.stringifier;

import com.jurajcernickaproject.gamelogic.Chess_Position;
import com.jurajcernickaproject.pieces.Piece;
import com.jurajcernickaproject.move.Coordinates;
import com.jurajcernickaproject.gamelogic.Vote;

import java.util.HashMap;
import java.util.Map;

public abstract class MoveStringifier {
    private static Chess_Position current_position;
    public static String stringifyVote(Vote vote) {
        String output = "";

        Piece moving_piece = current_position.get_tile(vote.get_coordinates()).get_piece();
        Piece captured_piece = current_position.get_tile(vote.get_move().get_coordinates()).get_piece();

        Map<Integer, Character> coordinate_to_letter = new HashMap<>();
        coordinate_to_letter.put(7, 'h');
        coordinate_to_letter.put(6, 'g');
        coordinate_to_letter.put(5, 'f');
        coordinate_to_letter.put(4, 'e');
        coordinate_to_letter.put(3, 'd');
        coordinate_to_letter.put(2, 'c');
        coordinate_to_letter.put(1, 'b');
        coordinate_to_letter.put(0, 'a');

        if(moving_piece.type() != 'P') {
            if(moving_piece.type() == 'K') {
                if(moving_piece.get_coordinates().get_y() < vote.get_move().get_coordinates().get_y() - 1) {
                    output += "O-O";
                    return output;
                } else if(moving_piece.get_coordinates().get_y() > vote.get_move().get_coordinates().get_y() + 1) {
                    output += "O-O-O";
                    return output;
                }
            }
            output += moving_piece.type();
            if(captured_piece != null)
                output += 'x';
        } else if (moving_piece.get_coordinates().get_y() != vote.get_move().get_coordinates().get_y()) {
            output += coordinate_to_letter.get(moving_piece.get_coordinates().get_y());
            output += 'x';
        }

        Coordinates coordinates = vote.get_move().get_coordinates();
        output += coordinate_to_letter.get(coordinates.get_y());
        output += 8 - coordinates.get_x();

        if(vote.promotion_piece() != null) {
            output += '=';
            switch(vote.promotion_piece()) {
                case "queen": output += 'Q'; break;
                case "knight": output += 'N'; break;
                case "bishop": output += 'B'; break;
                case "rook": output += 'R'; break;
            }
        }

        return output;
    }
    public static void set_position(Chess_Position position) {
        current_position = position;
    }
}
