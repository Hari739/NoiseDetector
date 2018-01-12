package com.hari.noisedetector;


import java.security.PublicKey;


/**
 * Created by Hari on 22-11-2017.
 * will use for map storing in firebase
 */

public class User {
    private String Name;
    private String loudness;
    private String timestamp;
    private String latitude;
    private String longitude;
    public  User(){

    }
    public User(String n, String l,String t,String lat,String lon){
        this.latitude = lat;
        this.longitude =lon;
        this.Name =n;
        this.loudness =l ;
        this.timestamp = t;
    }

    public void setName(String n){
        this.Name = n;
    }
    public void setLoudness(String l){
        this.loudness =l ;
    }
    public  void setTimestamp(String t){
        this.timestamp = t;
    }
    public void setLatitude(String lat){
        this.latitude = lat;
    }
    public void setLongitude(String lon){
        this.longitude = lon;
    }

   public String getName(){
        return  this.Name;
   }
   public String getLoudness(){
       return this.loudness;
   }
   public  String getTimestamp(){
       return  this.timestamp;
   }
   public String getLatitude(){
       return this.latitude;
   }
   public  String getLongitude(){
       return  this.longitude;
   }
}
