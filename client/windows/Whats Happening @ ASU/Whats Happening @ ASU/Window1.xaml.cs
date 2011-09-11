using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Web.Script.Serialization;
/// <ADP> Include the ADP SDK namespace;
using com.intel.adp;
using System.Net;
/// </ADP>
/// 
namespace Whats_Happening___ASU
{
    /// <summary>
    /// Interaction logic for Window1.xaml
    /// </summary>
    public partial class Window1 : Window
    {
        public Window1()
        {
            InitializeComponent();

            using(WebClient wc = new WebClient())
            {
                //Download JSON formatted events
                string json = wc.DownloadString("http://slyduck.com/api/events/near/33.417866954449565,-111.93152221691895/?radius=250&delay=1440");
                
                //Deserialize JSON to list of events
                JavaScriptSerializer ser = new JavaScriptSerializer();
                List<Event> events = ser.Deserialize<List<Event>>(json);
                
            }
            
            


            //Authorization code for Intel AppUp(TM) software
            com.intel.adp.AdpApplication app;
            try
            {
                //TODO: Right click and select Get Application GUID to replace the debug GUID before submitting it
                //app = new com.intel.adp.AdpApplication(new com.intel.adp.AdpApplicationId(0xBFB0F884, 0x06204AAD, 0xA69F588F, 0x8A71F97E));
                app = new com.intel.adp.AdpApplication(new com.intel.adp.AdpApplicationId(0x11111111, 0x11111111, 0x11111111, 0x11111111));
            }
            catch (com.intel.adp.AdpException e)
            {
                if (e is AdpErrorException)
                {
                    //TODO: Add your logic to handle errors during initialization
                    MessageBox.Show(e.Message, "Error");
                    System.Environment.Exit(1);
                }
                else if (e is AdpWarningException)
                {
                    //TODO: Add your logic to handle warnings 
                    MessageBox.Show(e.Message, "Warning");
                }
            }




        }
    }
}
