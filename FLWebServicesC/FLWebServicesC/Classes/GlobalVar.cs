using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Configuration;

namespace FLWebServicesC.Classes
{
    public class GlobalVar
    {
          public string connectionString;

        
    public GlobalVar()
    {

        connectionString = WebConfigurationManager.ConnectionStrings["FLCS"].ConnectionString;
   
    }
    }
}