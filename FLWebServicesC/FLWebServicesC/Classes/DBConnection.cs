using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using DBManager;
using FLWebServicesC.Classes;
namespace FLWebServicesC.Classes
{
    public class DBConnection
    {
        
         
        public DBOpeartion NewConectionDLL ;
        public DBConnection()
        {
            GlobalVar var = new GlobalVar();
            NewConectionDLL = new DBOpeartion(var.connectionString);

        }
 
    }
}