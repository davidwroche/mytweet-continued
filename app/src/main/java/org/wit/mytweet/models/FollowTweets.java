package org.wit.mytweet.models;

/**
 * Created by Tosh on 12/21/2016.
 */
public class FollowTweets {
    public int id;
    public String message;
    public String firstName;
    public String lastName;

    public FollowTweets(){

    }

    public FollowTweets(int id, String message, String firstName, String lastName){
        this.id = id;
        this.message = message;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId(){
        return id;
    }

    public String getMessage(){
        return message;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }


    public void setId(int id){
        this.id = id;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }


    public void setLastName(String lastName){
        this.lastName = lastName;
    }

}
