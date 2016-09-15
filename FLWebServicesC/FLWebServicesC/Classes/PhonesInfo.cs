using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FLWebServicesC.Classes
{
    public class PhonesInfo
    {
         public string PhoneUID { get; set; }
        public string PhoneName { get; set; }
        public int BatteryLevel { get; set; }
        public double Latitude { get; set; }
        public double longitude { get; set; }
        public string DateRecord { get; set; }
       
        public PhonesInfo(string PhoneUID, string PhoneName, int BatteryLevel, double Latitude,
            double longitude, string DateRecord)
        {
            this.PhoneUID = PhoneUID;
            this.PhoneName = PhoneName;
            this.BatteryLevel = BatteryLevel;
            this.Latitude = Latitude;
            this.longitude = longitude;
            this.DateRecord = DateRecord;
        }
    }
}