using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Whats_Happening___ASU
{
    class Organization
    {
        public string name { get; set; }
        public string description { get; set; }
        public Uri website { get; set; }
        public User owner { get; set; }
    }
}
