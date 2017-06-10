package org.wit.mytweet.models;

/**
 * Created by Tosh on 12/19/2016.
 */
public class Social {

    public int id;
    public String _id;
    public String following;
    public String follower;
    public String folFname;
    public String folLname;

    public Social(){

    }

    public Social(int id, String _id, String following, String follower){
        this.id = id;
        this._id =  _id;
        this.following = following;
        this.follower = follower;

    }

    public Social(String _id, String following, String follower){
        this._id =  _id;
        this.following = following;
        this.follower = follower;

    }


    public Social(String _id, String following, String follower, String folFname, String folLname){
        this._id =  _id;
        this.following = following;
        this.follower = follower;
        this.folFname = folFname;
        this.folLname = folLname;

    }

    public int getID(){
        return id;
    }

    public String get_ID(){
        return _id;
    }

    public String getFollowing(){
        return following;
    }

    public String getFollower(){
        return follower;
    }

    public String getFolFname(){
        return folFname;
    }

    public String getFolLname(){
        return folLname;
    }

    public void setId(int id){
        this.id = id;

    }

    public void set_id(String _id){
        this._id = _id;
    }

    public void setFollower(String follower){
        this.follower = follower;

    }

    public void setFollowing(String following){
        this.following = following;

    }

    public void setFolFname(String folFname){
        this.folFname = folFname;

    }

    public void setFolLname(String folLname){
        this.folLname = folLname;

    }
}
