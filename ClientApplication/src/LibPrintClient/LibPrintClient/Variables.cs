using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography;
using System.IO;
using System.Windows.Forms;

namespace LibPrintClient
{
    public static class Variables
    {
        public static IniFile getINI()  {
            System.IO.Directory.CreateDirectory(baseDir());
            System.IO.Directory.CreateDirectory(configDir());
            System.IO.Directory.CreateDirectory(cacheDir());

            if (!System.IO.File.Exists(configFile()))
            {
                IniFile configuration = new IniFile(configFile());

                Program.initGUI();

                string host = Prompt.ShowDialog("Enter the host and port for this LibPrint configuration (i.e. 'libprint.ddns.org:8080').", "LibPrint");
                string code = Prompt.ShowDialog("Enter the domain code for this LibPrint configuration.", "LibPrint");

                configuration.IniWriteValue("config", "LibPrintHostAndPort", host);
                configuration.IniWriteValue("config", "DomainCode", code);

                MessageBox.Show("LibPrint Client is now configured. Edit " + configFile() + " if further changes are needed.");

                Environment.Exit(0);

                return configuration;
            } else
            {
                return new IniFile(configFile());
            }
        }
        public static string username = System.Security.Principal.WindowsIdentity.GetCurrent().Name.Split('\\')[1];

        public static string computer = Environment.MachineName;
        public static string[] parsed;

        public static IniFile configuration = getINI();

        public static string libprinturl = "http://" + configuration.IniReadValue("config", "LibPrintHostAndPort").Trim() + "/LibPrint/RequestHandler";
        public static string printerName;
        public static string domainCode = configuration.IniReadValue("config", "DomainCode").Trim();

        public static string cacheFile()
        {
            string cacheFile = Directory.GetFiles(cacheDir())[0];

            return cacheFile;
        }

        public static string cacheDir()
        {
            return Environment.ExpandEnvironmentVariables(@"%ProgramData%\LibPrint\cache\");
        }

        public static string baseDir()
        {
            return Environment.ExpandEnvironmentVariables(@"%ProgramData%\LibPrint\");
        }

        public static string configDir()
        {
            return Environment.ExpandEnvironmentVariables(@"%ProgramData%\LibPrint\Config\");
        }

        public static string configFile()
        {
            return configDir() + "DomainCodeConfig.ini";
        }

        public static string GenerateSecToken(string domainCode, string username, string computer)
        {
            return GetBase64EncodedSHA256Hash(domainCode + ':' + username + ':' + computer);
        }

        static string GetBase64EncodedSHA256Hash(string plaintext)
        {
            using(SHA256 hash = SHA256Managed.Create())
            {
                Byte[] result = hash.ComputeHash(Encoding.UTF8.GetBytes(plaintext));
                return Convert.ToBase64String(result).TrimEnd(new[] {'='}).Replace('+', '-').Replace('/', '_');
            }
        }
    }
}
