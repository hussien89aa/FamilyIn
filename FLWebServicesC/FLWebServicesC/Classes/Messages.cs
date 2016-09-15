using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Web;

namespace FLWebServicesC.Classes
{
    public class Messages
    {
        public void SendMessage(string Email,string body,string subject )
        {
            try
            {
                string smtpAddress = "mail.myphonelocations.alruabye.net";
                int portNumber = 25;
                bool enableSSL = false;

                string emailFrom = "postmaster@myphonelocations.alruabye.net";
                string password = "abc3551356A@#$";
                string emailTo = Email;

                using (MailMessage mail = new MailMessage())
                {
                    mail.From = new MailAddress(emailFrom);
                    mail.To.Add(emailTo);
                    mail.Subject = subject;
                    mail.Body = body;
                    mail.IsBodyHtml = true;
                    // Can set to false, if you are sending pure text.

                    //  mail.Attachments.Add(new Attachment("C:\\SomeFile.txt"));
                    //  mail.Attachments.Add(new Attachment("C:\\SomeZip.zip"));

                    using (SmtpClient smtp = new SmtpClient(smtpAddress, portNumber))
                    {
                        smtp.Credentials = new NetworkCredential(emailFrom, password);
                        smtp.EnableSsl = enableSSL;
                        smtp.Send(mail);
                    }
                }
            } catch (Exception ex){}
        }
    }
}