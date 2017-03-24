using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net.Http;
using System.IO;

namespace LibPrintClient
{
    public partial class PrinterSelect : Form
    {
        public PrinterSelect()
        {
            InitializeComponent();

            radioButton1.Text = Variables.parsed[3].Split(',')[0].Trim();
            radioButton2.Text = Variables.parsed[5].Split(',')[0].Trim();
            label1.Text = Variables.parsed[3].Split(',')[1].Trim();
            label2.Text = Variables.parsed[5].Split(',')[1].Trim();

            button1.Click += new EventHandler(this.SelectOK);
            button2.Click += new EventHandler(this.SelectCancel);

            if(Variables.parsed.Length == 4)
            {
                radioButton2.Hide();
                label2.Hide();
            }
        }

        async void SelectOK(Object sender, EventArgs e)
        {
            if(radioButton1.Checked == true)
            {
                Variables.printerName = Variables.parsed[3].Split(',')[0].Trim();
            }
            else if(radioButton2.Checked == true)
            {
                Variables.printerName = Variables.parsed[5].Split(',')[0].Trim();
            }

            byte[] fileByteArray = File.ReadAllBytes(Directory.GetFiles(@"c:\ProgramData\LibPrint\cache\")[0]);

            HttpClient httpClient = new HttpClient();
            MultipartFormDataContent form = new MultipartFormDataContent();

            form.Add(new StringContent("printPDF"), "request");
            form.Add(new StringContent(System.Security.Principal.WindowsIdentity.GetCurrent().Name), "username");
            form.Add(new StringContent(Environment.MachineName), "computer");
            form.Add(new StringContent(Variables.printerName), "printerName");
            form.Add(new StringContent("temp"), "secToken");
            form.Add(new ByteArrayContent(fileByteArray, 0, fileByteArray.Count()), "file", Directory.GetFiles(@"c:\ProgramData\LibPrint\cache\")[0]);
            HttpResponseMessage response = await httpClient.PostAsync(Variables.libprinturl, form);

            response.EnsureSuccessStatusCode();
            httpClient.Dispose();

            string result = response.Content.ReadAsStringAsync().Result;

            Variables.parsed = result.Split(new[] {':', '\n'});

            Console.WriteLine(result);

            if(Variables.parsed[1].Trim() == "OK")
            {
                ConfirmationWindow frm = new ConfirmationWindow();
                frm.Show();
            }
            else if(Variables.parsed[1].Trim() == "Error")
            {
                PrintError frm = new PrintError();
                frm.Show();
            }
            else
            {
                string error = "Response: Error\nError: Invalid response from server";
                Variables.parsed = error.Split(new[] { ':', '\n' });

                PrintError frm = new PrintError();
                frm.Show();
            }
        }

        void SelectCancel(Object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void PrinterSelect_Load(object sender, EventArgs e)
        {
            
        }
    }
}
