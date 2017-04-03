﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO;

namespace LibPrintClient
{
    public partial class PrintError : Form
    {
        public PrintError()
        {
            InitializeComponent();

            label1.Text = "Print error: " + Variables.parsed[3].Trim();

            button1.Click += new EventHandler(this.SelectOK);
        }

        void SelectOK(Object sender, EventArgs e)
        {
            if(Directory.GetFiles(@"c:\ProgramData\LibPrint\cache\").Length != 0)
            {
                File.Delete(Variables.cacheFile());
            }
            Application.Exit();
        }
    }
}
