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

            button1.Enabled = false;

            radioButton1.Text = Variables.parsed[3].Split(',')[0].Trim();
            label1.Text = Variables.parsed[3].Split(',')[1].Trim();

            Console.WriteLine(Variables.parsed.Length);

            if(Variables.parsed.Length == 7)
            {
                radioButton2.Text = Variables.parsed[5].Split(',')[0].Trim();
                label2.Text = Variables.parsed[5].Split(',')[1].Trim();
            }
            else
            {
                radioButton2.Text = "Printer Unavailable";
                label2.Text = "N/A";
                radioButton2.Enabled = false;
                label2.Enabled = false;
            }

            radioButton1.Click += new EventHandler(this.Choose);
            radioButton2.Click += new EventHandler(this.Choose);
            button1.Click += new EventHandler(this.SelectOK);
            button2.Click += new EventHandler(this.SelectCancel);
        }

        void Choose(Object sender, EventArgs e)
        {
            button1.Enabled = true;
            return;
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

            byte[] fileByteArray = File.ReadAllBytes(Variables.cacheFile());

            HttpClient httpClient = new HttpClient();
            MultipartFormDataContent form = new MultipartFormDataContent();

            form.Add(new StringContent("printPDF"), "request");
            form.Add(new StringContent(Variables.username), "username");
            form.Add(new StringContent(Variables.computer), "computer");
            form.Add(new StringContent(Variables.printerName), "printerName");
            form.Add(new StringContent(Variables.GenerateSecToken("temp", Variables.username, Variables.computer)), "secToken");
            form.Add(new ByteArrayContent(fileByteArray, 0, fileByteArray.Count()), "file", Variables.cacheFile());
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
            File.Delete(Variables.cacheFile());
            Application.Exit();
        }

        private void PrinterSelect_Load(object sender, EventArgs e)
        {
            
        }
    }
}
