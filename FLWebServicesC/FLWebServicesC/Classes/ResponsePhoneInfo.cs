using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FLWebServicesC.Classes
{
    public class ResponsePhoneInfo
    {
        public PhonesInfo[] UsersPhonesInfo { get; set; }
        public int ErrorID { get; set; } // give id if there is error
    }
}