package org.wit.mytweet.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tosh on 10/1/2016.
 */
public class Tweet {

    public int id;
    public int Uid;
    public String _id;
    public String message;
    public String date;
    public String poster;
    public Tweet(){

    }

    public Tweet(int id, String message, String date, int Uid){
        this.id = id;
        this.Uid = Uid;
        this.message = message;
        this.date = date;
    }

    public Tweet(String message, String date, int Uid){
        this.message = message;
        this.date = date;
        this.Uid = Uid;
    }

    public Tweet(String message){
        this.message = message;
    }

    public Tweet(int id, String poster, String message, String _id){
        this.id = id;
        this.poster = poster;
        this.message = message;
        this._id = _id;
        //this.Uid = Uid;

    }

    //Setters

    public void setId(int id){
        this.id = id;
    }

    public void setUid(int Uid){
        this.Uid = Uid;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setPoster(String poster){
        this.poster = poster;
    }

    public void setTid(String _id){
        this._id = _id;
    }


    //Getters
    public String  getTID(){
        return _id;
    }

    public int getId(){
        return id;
    }

    public int getUid(){
        return Uid;
    }

    public String  getPoster(){
        return poster;
    }

    public String getMessage(){
        return message;
    }

    public String getDate(){
      return date;
   }

}
