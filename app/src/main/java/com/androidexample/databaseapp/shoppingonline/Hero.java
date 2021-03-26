package com.androidexample.databaseapp.shoppingonline;

/**
 * Created by Belal on 9/9/2017.
 */

class Hero {
    private int id;
    private String name, realname, rating;

    public Hero(int id, String name, String realname, String rating) {
        this.id = id;
        this.name = name;
        this.realname = realname;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
