package com.jurajcernickaproject.gamelogic;

import com.jurajcernickaproject.gui.Chess_Board_Panel;
import com.jurajcernickaproject.stringifier.MoveStringifier;
import com.jurajcernickaproject.gui.Voter_List_Container;

import javax.swing.*;
import java.util.LinkedList;

/**
 * Takes care of the voting mechanism, as well as deciding when a game should end.*/
public class GameController {
    private final LinkedList<Vote> votes;
    private int white_voter_count;
    private int black_voter_count;
    private final Vote[] move_pool;
    private int array_size;
    private int vote_count;
    private final Chess_Board_Panel panel;
    private Voter_List_Container voter_container;
    /**
     * Initializes attributes either to their default values or based on the parameter.
     * @param panel The chess board panel, which represents the chess board from a user interface perspective.*/
    public GameController(Chess_Board_Panel panel)
    {
        this.votes = new LinkedList<>();
        this.vote_count = 0;
        this.panel = panel;

        move_pool = new Vote[5]; // Maximum number of different moves to choose from is 5.
        array_size = 0;

        MoveStringifier.set_position(panel.get_position());
    }
    /**
     * A function deciding if the game can start based on the number of voters on each side (each side needs at least one).
     * @return A boolean value indicating if the game can start.*/
    public boolean game_can_start() {
        return white_voter_count > 0 && black_voter_count > 0;
    }
    /**
     * A function which sets the voter_container parameter.
     * @param voter_container A GUI element which contains the lists of voters on each side.*/
    public void add_voter_container(Voter_List_Container voter_container) {
        this.voter_container = voter_container;
    }
    /**
     * Increment the number of voters on a certain side. The parameter decides which side.
     * @param white A boolean value indicating which side's number of voters should get incremented.*/
    public void increment_voter_count(boolean white) {
        if(white)
            white_voter_count++;
        else
            black_voter_count++;
    }
    /**
     * Finds and returns the most popular vote.
     * @return Most popular vote.*/
    private Vote get_largest_vote()
    {
        Vote largest_vote = votes.getFirst();
        for(Vote vote : this.votes)
            if(vote.get_weight() > largest_vote.get_weight())
                largest_vote = vote;

        return largest_vote;
    }
    /**
     * Either adds a new vote to the list of votes, or increments the weight of an existing vote. Gets called after a player votes for a certain move.
     * @param new_vote A vote made by a player.*/
    private void update_votes(Vote new_vote)
    {
        vote_count++;

        for(Vote vote : this.votes) {
            if (vote.compare(new_vote)) {
                vote.increment_weight();
                return;
            }
        }
        votes.add(new_vote);
    }
    /**
     * Creates the window which informs the players the game has ended.
     * @param label JLabel which gets shown in the window. Changes depending on how the game ended.*/
    private void create_game_ended_window(JLabel label)
    {
        JFrame frame = new JFrame();
        frame.setTitle("Game ended");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(label);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 80);
        frame.setVisible(true);
    }
    /**
     * Effectively ends the game, as well as generates a window informing the player how it ended.
     * @param ending_message A string which will get show in the final window. Informs how the game ended.*/
    private void end_game(String ending_message)
    {
        JLabel label = new JLabel(ending_message);
        create_game_ended_window(label);
        panel.lock();

        voter_container.getWhite_list().lock();
        voter_container.getBlack_list().lock();
    }
    /**
     * Updates the board and checks for any game ending conditions. Also ends the game in case they are met.*/
    private void update_boards()
    {
        Vote vote = get_largest_vote();

        if(panel.get_position().get_tile(vote.get_move().get_coordinates()).get_piece() == null && panel.get_position().get_tile(vote.get_coordinates()).get_piece().type() != 'P') // Conditions for the 50 move rule.
            panel.get_position().incrementMovesTowardsDraw();
        else
            panel.get_position().resetMovesTowardsDraw();

        panel.update_board(vote);

        boolean no_moves = !panel.get_position().update_possible_moves(); // If the player doesn't have any legal moves, the game ends.
        panel.load_piece_images();

        if(no_moves)
        {
            if(panel.get_position().in_check()) // Checkmate.
            {
                if(panel.get_position().get_turn())
                    end_game("Black won!");
                else
                    end_game("White won!");
            }

            else
            {
                end_game("Stalemate!"); // Stalemate.
            }
        }

        if(panel.get_position().getMovesTowardsDraw() == 100) // 50 Move rule.
        {
            end_game("Draw due to the 50 move rule!");
        }
    }
    /**
     * Resets the votes attribute. Gets called after a move has been played, and the linked list containing current votes needs to reset.*/
    private void reset_votes()
    {
        this.votes.clear();
        vote_count = 0;
    }
    /**
     * Adds a vote the list of votes. Gets called after a player has voted. Also checks if the number of players who have voted is equal to the number of players on that side.
     * If it is, then the given move is played.
     * @param new_vote The voting player's choice.
     * @param is_white_player A boolean value indicating if the voting player is white.*/
    public void add_vote(Vote new_vote, boolean is_white_player)
    {
        update_votes(new_vote);

        if((is_white_player && vote_count == white_voter_count) || (!is_white_player && vote_count == black_voter_count))
        {
            update_boards();
            reset_votes();
            reset_move_pool();
            voter_container.toggle_lock();
        }
    }
    /**
     * Adds a given vote to the list of possible votes which the players can choose that round. Gets called after an admin adds a new move.
     * @param vote The new added vote possibility.*/
    public void add_to_move_pool(Vote vote) {

        for(int i = 0; i < array_size; i++) // If such a vote has already been added, nothing happens. To avoid duplicate votes.
        {
            if(move_pool[i].get_coordinates().get_x() == vote.get_coordinates().get_x() &&
                    move_pool[i].get_coordinates().get_y() == vote.get_coordinates().get_y() &&
            move_pool[i].get_move().get_coordinates().get_x() == vote.get_move().get_coordinates().get_x() &&
            move_pool[i].get_move().get_coordinates().get_y() == vote.get_move().get_coordinates().get_y()) {
                return;
            }
        }

        move_pool[array_size] = vote;
        array_size++;

        if(array_size == 5) // 5 votes is the maximum number of votes. After this number, the chess board becomes locked.
            panel.lock();
    }
    /**
     * Resets the attribute of array_size back to zero.*/
    public void reset_move_pool() {
        array_size = 0;
    }
    /**
     * Returns the array_size attribute.
     * @return An integer, which is equal to the number of possible votable moves the admin has chosen.*/
    public int get_array_size() {
        return array_size;
    }
    /**
     * Returns the move_pool attribute.
     * @return An array which contains the player's votes.*/
    public Vote[] get_move_pool() {
        return move_pool;
    }

}
