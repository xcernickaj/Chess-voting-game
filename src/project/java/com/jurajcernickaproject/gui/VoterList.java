package com.jurajcernickaproject.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
/**
 * A GUI element representing the list of voters on a given side.*/
public class VoterList extends JScrollPane implements ActionListener {

    private final JList<String> voter_list;
    private boolean locked;
    private final LinkedList<String> voters;
    private final DefaultListModel<String> list_model;
    /**
     * Initializes the attributes to their default values. Also sets certain GUI options.*/
    public VoterList() {

        voters = new LinkedList<>();
        list_model = new DefaultListModel<>();
        voter_list = new JList<>(list_model);
        voter_list.setFont(new Font("Arial", Font.PLAIN, 14));

        setViewportView(voter_list);
    }
    /**
     * Deletes the item in the list which is currently selected. Gets called whenever a selected player votes. It's important to remove the players which have already voted from the list
     * to make sure no player votes several times.*/
    public void deleteSelectedLine() {
        int selectedIndex = voter_list.getSelectedIndex();
        if (selectedIndex != -1) { // If a selected name exists.
            DefaultListModel<String> model = (DefaultListModel<String>) voter_list.getModel();
            model.remove(selectedIndex);
        }
    }
    /**
     * Toggles this list to get locked or unlocked. A locked list doesn't show its names so only the side whose turn it is right now has its names visible.*/
    public void toggle_lock() {
        if(locked)
            unlock();
        else
            lock();
    }
    /**
     * Adds a new voting player to the list.
     * @param voter_name A string value representing the name of the player.*/
    public void add_voter(String voter_name) {
        voters.add(voter_name);
    }
    /**
     * Toggles this list to get locked. A locked list doesn't show its names so only the side whose turn it is right now has its names visible.*/
    public void lock() {
        locked = true;

        list_model.clear();
    }
    /**
     * Toggles this list to get unlocked. A locked list doesn't show its names so only the side whose turn it is right now has its names visible. Also generates
     * the names on the list.*/
    public void unlock() {
        locked = false;

        for(String name : voters)
            list_model.addElement(name);

        setViewportView(voter_list);
    }
    /**
     * An empty unused function which has to be declared here because the class implements an ActionListener.
     * @param e MouseEvent is not used, but is the parameter in the overriden function.*/
    @Override
    public void actionPerformed(ActionEvent e) {

    }
    /**
     * Returns an integer value indicating the selected line.
     * @return The selected_index attribute of the voter list.*/
    public int getSelectedLine() {
        return voter_list.getSelectedIndex();
    }
}