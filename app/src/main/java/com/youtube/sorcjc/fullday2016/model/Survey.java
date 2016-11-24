package com.youtube.sorcjc.fullday2016.model;

import java.io.Serializable;

/**
 * Created by pc on 21/11/2016.
 */

public class Survey implements Serializable {
    private int id;
    private String description;
    private String turn;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
