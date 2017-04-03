using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography;
using System.IO;

namespace LibPrintClient
{
    public static class Variables
    {
        public static string username = System.Security.Principal.WindowsIdentity.GetCurrent().Name.Split('\\')[1];
        public static string computer = Environment.MachineName;
        public static string[] parsed;
        public static string libprinturl = System.IO.File.ReadAllText(@"C:\ProgramData\LibPrint\Config\DomainCodeConfig.ini").Split(new[] { '=', '\n' })[3].Trim();
        public static string printerName;
        public static string domainCode = System.IO.File.ReadAllText(@"C:\ProgramData\LibPrint\Config\DomainCodeConfig.ini").Split(new[] { '=', '\n' })[1].Trim();

        public static string cacheFile()
        {
            string cacheFile = Directory.GetFiles(@"c:\ProgramData\LibPrint\cache\")[0];

            return cacheFile;
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
