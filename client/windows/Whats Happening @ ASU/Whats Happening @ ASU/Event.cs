using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Whats_Happening___ASU
{
    class Event
    {
        public string name { get; set; }
        public string description { get; set; }
        public string start_timeframe
        {
            get
            {
                return this.start_timeframe_datetime.ToString();
            }
            set
            {
                this.start_timeframe_datetime = DateTime.Parse(value);
            }
        }
        public string end_timeframe
        {
            get
            {
                return this.end_timeframe_datetime.ToString();
            }
            set
            {
                this.end_timeframe_datetime = DateTime.Parse(value);
            }
        }
        private DateTime start_timeframe_datetime;
        private DateTime end_timeframe_datetime;
        public Location location { get; set; }
        public Organization organization { get; set; }

        public override string ToString()
        {
            return this.name + ", " + this.location.ToString();
        }
    }
}
