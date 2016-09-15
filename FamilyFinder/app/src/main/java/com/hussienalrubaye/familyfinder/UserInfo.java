package com.hussienalrubaye.familyfinder;

/**
 * Created by hussienalrubaye on 9/14/16.
 */

public class UserInfo {
    public String PhoneUID;
    public String PhoneName;
    public int BatteryLevel;
    public double Latitude ;
    public double Longitude ;
    public String DateRecord ;

    public UserInfo(String PhoneUID, String PhoneName,
                    int BatteryLevel, double Latitude,
                      double Longitude, String DateRecord)
    {
        this.PhoneUID = PhoneUID;
        this.PhoneName = PhoneName;
        this.BatteryLevel = BatteryLevel;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.DateRecord = DateRecord;
    }
}
