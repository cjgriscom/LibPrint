using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net;
using System.IO;



namespace LibPrintClient
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        public static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            //check contents of cache folder
            if (Directory.GetFiles(@"c:\ProgramData\LibPrint\cache\").Length == 0)
            {
                string error = "Response: Error\nError: No document cached for printing";
                Variables.parsed = error.Split(new[] { ':', '\n' });

                Application.Run(new PrintError());
            }

            //Make getInformation request
            WebClient webClient = new WebClient();
            webClient.QueryString.Add("request", "getInformation");
            webClient.QueryString.Add("username", System.Security.Principal.WindowsIdentity.GetCurrent().Name);
            webClient.QueryString.Add("computer", Environment.MachineName);
            webClient.QueryString.Add("secToken", "temp");
            string result = webClient.DownloadString(Variables.libprinturl);
            Variables.parsed = result.Split(new[] { ':', '\n'});

            if (Variables.parsed[1].Trim() == "OK")
            {
                Application.Run(new PrinterSelect());
            }

            else if(Variables.parsed[1].Trim() == "Error")
            {
                Application.Run(new PrintError());
            }
        }
    }
}
