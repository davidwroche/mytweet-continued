package org.wit.mytweet.models;

/**
 * Created by Tosh on 9/26/2016.
 */

public class User
{
    public int id;
    public String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    public User(){

    }

    public User(int id, String _id,String firstName, String lastName, String email, String password)
    {
        this.id = id;
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public void set_Id(String _id){
    this._id = _id;

}
    public User(String _id, String firstName, String lastName, String email, String password){
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public void setId(int id){
        this.id = id;

    }

    //Setter

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    //Getters

    public String get_Id(){
        return _id;
    }

    public int getId(){
        return id;
    }

    public String getFirstName(){
        return firstName;
    }


    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getLastName(){
        return lastName;
    }


}