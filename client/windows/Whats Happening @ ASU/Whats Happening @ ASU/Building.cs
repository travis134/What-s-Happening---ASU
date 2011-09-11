using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Whats_Happening___ASU
{
    class Building
    {
        public string abbreviation { get; set; }
        public string name { get; set; }
        public float latitude { get; set; }
        public float longitude { get; set; }

        public override string ToString()
        {
            return this.abbreviation;
        }
    }
}
