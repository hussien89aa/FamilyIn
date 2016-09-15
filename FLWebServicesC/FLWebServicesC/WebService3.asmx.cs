using DBManager;
using FLWebServicesC.Classes;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Web;
using System.Web.Script.Serialization;
using System.Web.Script.Services;
using System.Web.Services;

namespace FLWebServicesC
{
    /* this new web service that could work with phone and json only
     * 
     * 
     * */
    /// <summary>
    /// Summary description for WebService3
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    [System.Web.Script.Services.ScriptService]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class WebService3 : System.Web.Services.WebService
    {

        //This method create new account  in the database 
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void OpenAccount(string EmailAdrress, string Password)
        {
            DBConnection DBop = new DBConnection();
            MesseageResponse messeageResponse = new MesseageResponse();
            JavaScriptSerializer ser = new JavaScriptSerializer();

            /// check if the email already taken
            DataTable dataTable = new DataTable();
            dataTable = DBop.NewConectionDLL.SelectDataSet("Admins", " * ", " EmailAdrress like '" + EmailAdrress + "'").Tables[0];
            if ((dataTable != null) && (dataTable.Rows.Count > 0))
            {

                messeageResponse.ErrorID = (int)ErrorNumbers.NotFound;
                messeageResponse.Message = "email address already taken, try with different email";

                HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                return;
            }

            /// save new account
            /// 
            ColoumnParam[] Coloumns = new ColoumnParam[2];
            Coloumns[0] = new ColoumnParam("Password", ColoumnType.varchar50, Password);
            Coloumns[1] = new ColoumnParam("EmailAdrress", ColoumnType.varchar50, EmailAdrress);
            if (DBop.NewConectionDLL.InsertRow("Admins", Coloumns))
            {
                dataTable = DBop.NewConectionDLL.SelectDataSet("Admins", " UserUID ", "EmailAdrress like '" + EmailAdrress + "'").Tables[0];


                /////send email to new account=========================================================

                Messages send = new Messages();
                string subject = "My Phone Location Account is created successfully";
                string body = "Dear User,";
                body = body + "\n, Thank you for using My phones locations APP , now you could track your phone from any computer on real time \n";
                send.SendMessage(EmailAdrress, body, subject);

                ///send user info

                messeageResponse.ErrorID = (int)ErrorNumbers.Found;
                messeageResponse.UserUID = Convert.ToString(dataTable.Rows[0]["UserUID"]);
                messeageResponse.Message = "Great, your accound is created successfully";
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                return;
            }
            else
            {
                messeageResponse.ErrorID = (int)ErrorNumbers.NotFound;
                messeageResponse.Message = "Error , cannot add this account";
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                return;
            }


        }


        //This method new user login
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void UserLogin(string EmailAdrress, string Password, string PhoneMac, string PhoneName)
        {
            DBConnection DBop = new DBConnection();
            MesseageResponseUser messeageResponse = new MesseageResponseUser();
            JavaScriptSerializer ser = new JavaScriptSerializer();

            /// check if the account is valid
            DataTable dataTable = new DataTable();
            dataTable = DBop.NewConectionDLL.SelectDataSet("Admins", " UserID,UserUID ", " EmailAdrress = '" + EmailAdrress + "' and Password = '" + Password + "'").Tables[0];
            if ((dataTable == null) || (dataTable.Rows.Count == 0))
            {
                messeageResponse.ErrorID = (int)ErrorNumbers.NotFound;
                messeageResponse.Message = "Username or password is not correct";
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                return;
            }

            int UserID = (int)dataTable.Rows[0]["UserID"];
            messeageResponse.UserUID = Convert.ToString(dataTable.Rows[0]["UserUID"]);
            // check if the user already resgitered with that account
            dataTable = DBop.NewConectionDLL.SelectDataSet("AdminPhones", " PhoneUID ", "UserID=" + UserID + " and PhoneMac = '" + PhoneMac + "'").Tables[0];
            if ((dataTable != null) && (dataTable.Rows.Count > 0))
            {
                messeageResponse.PhoneUID = Convert.ToString(dataTable.Rows[0]["PhoneUID"]);
                messeageResponse.ErrorID = (int)ErrorNumbers.Found;
                messeageResponse.Message = "you Already registered,  you will login now";
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                return;
            }

            // register new user
            ColoumnParam[] Coloumns = new ColoumnParam[3];
            Coloumns[0] = new ColoumnParam("UserID", ColoumnType.Int, UserID);
            Coloumns[1] = new ColoumnParam("PhoneMac", ColoumnType.varchar50, PhoneMac);
            Coloumns[2] = new ColoumnParam("PhoneName", ColoumnType.varchar50, PhoneName);
            if (DBop.NewConectionDLL.InsertRow("AdminPhones", Coloumns))
            {

                dataTable = DBop.NewConectionDLL.SelectDataSet("AdminPhones", " PhoneUID ", "UserID=" + UserID + " and PhoneMac = '" + PhoneMac + "'").Tables[0];
                if ((dataTable != null) && (dataTable.Rows.Count > 0))
                {
                    messeageResponse.PhoneUID = Convert.ToString(dataTable.Rows[0]["PhoneUID"]);
                    messeageResponse.ErrorID = (int)ErrorNumbers.Found;
                    messeageResponse.Message = "Great, you registered successfully";
                    HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                    return;
                }

            }
            else
            {
                messeageResponse.ErrorID = (int)ErrorNumbers.NotFound;
                messeageResponse.Message = "Error , cannot add this account";
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponse));
                return;
            }

            // check if the user already saved


        }



        //This method new user login
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void UserTracking(string PhoneUID, string Latitude, string longitude, int BatteryLevel)
        {
            DBConnection DBop = new DBConnection();

            JavaScriptSerializer ser = new JavaScriptSerializer();
            ColoumnParam[] Coloumns = new ColoumnParam[4];
            Coloumns[0] = new ColoumnParam("Latitude", ColoumnType.Float, Latitude);
            Coloumns[1] = new ColoumnParam("longitude", ColoumnType.Float, longitude);
            Coloumns[2] = new ColoumnParam("BatteryLevel", ColoumnType.Int, BatteryLevel);
            Coloumns[3] = new ColoumnParam("PhoneUID", ColoumnType.uniqueidenifer, PhoneUID);
            if (DBop.NewConectionDLL.InsertRow("Tracking", Coloumns))
            {

                var jsonData = new
                {
                    IsDeliver = "Y"
                };
                HttpContext.Current.Response.Write(ser.Serialize(jsonData));


            }
            else
            {
                var jsonData = new
                {
                    IsDeliver = "N"
                };
                HttpContext.Current.Response.Write(ser.Serialize(jsonData));
            }



        }


        //Return phones related to one admin  
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void UsersPhoneLocations(string UserUID)
        {
            DBConnection DBop = new DBConnection();
            ResponsePhoneInfo messeageResponsePhoneInfo = new ResponsePhoneInfo();
            DataTable dataTable = new DataTable();
            JavaScriptSerializer ser = new JavaScriptSerializer();


            dataTable = DBop.NewConectionDLL.SelectDataSet("Admins", " UserID ", " UserUID = '" + UserUID + "'").Tables[0];
            if ((dataTable == null) || (dataTable.Rows.Count == 0))
            {
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.NotFound;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;
            }

            int UserID = (int)dataTable.Rows[0]["UserID"];

            ColProcedureParam[] Coloumns1 = new ColProcedureParam[1];
            Coloumns1[0] = new ColProcedureParam("UserID", Convert.ToString(UserID));
            dataTable = DBop.NewConectionDLL.SelectDataSetProcedureTable("GetUpdateLocation", Coloumns1).Tables[0];
            if ((dataTable != null) && (dataTable.Rows.Count > 0))
            {
                PhonesInfo[] phonelist = new PhonesInfo[dataTable.Rows.Count];
                for (int i = 0; i < dataTable.Rows.Count; i++)
                {
                    phonelist[i] = new PhonesInfo(
                       Convert.ToString(dataTable.Rows[i]["PhoneUID"]),
                         Convert.ToString(dataTable.Rows[i]["PhoneName"]),
                        (int)dataTable.Rows[i]["BatteryLevel"],
                       Convert.ToDouble(dataTable.Rows[i]["Latitude"]),
                       Convert.ToDouble(dataTable.Rows[i]["longitude"]),
                      Convert.ToString(dataTable.Rows[i]["DateRecord"]));


                }
                messeageResponsePhoneInfo.UsersPhonesInfo = phonelist;
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.Found;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;

            }
            else
            {
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.NotFound;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;
            }


        }


        //Forget My Password
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void ForgetMyPassword(string Email)
        {
            DBConnection DBop = new DBConnection();
            JavaScriptSerializer ser = new JavaScriptSerializer();

            /// check if this account avilable
            DataTable dataTable = new DataTable();
            dataTable = DBop.NewConectionDLL.SelectDataSet("Admins", "Password", "EmailAdrress like '" + Email + "'").Tables[0];
            if ((dataTable != null) && (dataTable.Rows.Count > 0))
            {

                Messages send = new Messages();
                string subject = "My Phone Location Password Requesting";
                string body = "Dear ";
                body = body + "\n, Recently you requested your password , your password is ( \n" + (string)dataTable.Rows[0]["Password"] + ")";
                send.SendMessage(Email, body, subject);
                {



                }
            }


            var jsonData = new
            {
                IsDeliver = "if your email is valid, you will get your password shortly by email"
            };
            HttpContext.Current.Response.Write(ser.Serialize(jsonData));
        }

        //get one user ast location update
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void LastUserLocation(string PhoneUID)
        {
            DBConnection DBop = new DBConnection();
            ResponsePhoneInfo messeageResponsePhoneInfo = new ResponsePhoneInfo();
            DataTable dataTable = new DataTable();
            JavaScriptSerializer ser = new JavaScriptSerializer();

            dataTable = DBop.NewConectionDLL.SelectDataSet("Tracking", "*", "PhoneUID = '" + PhoneUID + "' and  [DateRecord] =(select max ([DateRecord]) from [UserUpdateLocation] where PhoneUID ='" + PhoneUID + "')" ).Tables[0];
            if ((dataTable != null) && (dataTable.Rows.Count > 0))
            {
                PhonesInfo[] phonelist = new PhonesInfo[dataTable.Rows.Count];
                for (int i = 0; i < dataTable.Rows.Count; i++)
                {
                    phonelist[i] = new PhonesInfo(null, null,
                        (int)dataTable.Rows[i]["BatteryLevel"],
                       Convert.ToDouble(dataTable.Rows[i]["Latitude"]),
                       Convert.ToDouble(dataTable.Rows[i]["longitude"]),
                      Convert.ToString(dataTable.Rows[i]["DateRecord"]));
                }
                messeageResponsePhoneInfo.UsersPhonesInfo = phonelist;
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.Found;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;

            }
            else
            {
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.NotFound;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;
            }


        }

        //get one user ast location update
        [WebMethod]
        [ScriptMethod(ResponseFormat = ResponseFormat.Json, UseHttpGet = true)]
        public void UserLocationHistory(string PhoneUID,int RecordNumners)
        {
            DBConnection DBop = new DBConnection();
            ResponsePhoneInfo messeageResponsePhoneInfo = new ResponsePhoneInfo();
            DataTable dataTable = new DataTable();
            JavaScriptSerializer ser = new JavaScriptSerializer();

            dataTable = DBop.NewConectionDLL.SelectDataSet("Tracking","top "+ RecordNumners+ " *", "PhoneUID = '" + PhoneUID + "'", " DateRecord desc").Tables[0];
            if ((dataTable != null) && (dataTable.Rows.Count > 0))
            {
                PhonesInfo[] phonelist = new PhonesInfo[dataTable.Rows.Count];
                for (int i = 0; i < dataTable.Rows.Count; i++)
                {
                    phonelist[i] = new PhonesInfo(null, null,
                        (int)dataTable.Rows[i]["BatteryLevel"],
                       Convert.ToDouble(dataTable.Rows[i]["Latitude"]),
                       Convert.ToDouble(dataTable.Rows[i]["longitude"]),
                      Convert.ToString(dataTable.Rows[i]["DateRecord"]));
                }
                messeageResponsePhoneInfo.UsersPhonesInfo = phonelist;
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.Found;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;

            }
            else
            {
                messeageResponsePhoneInfo.ErrorID = (int)ErrorNumbers.NotFound;
                HttpContext.Current.Response.Write(ser.Serialize(messeageResponsePhoneInfo));
                return;
            }


        }


    }


}
