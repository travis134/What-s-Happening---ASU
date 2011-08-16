from piston.handler import BaseHandler
from piston.utils import rc
from datetime import datetime, timedelta
from django.contrib.gis.geos import *
from whaa.models import Event, Location, Building, Organization

#Return all events near specified latitude and longitude or return all events
class EventsHandler(BaseHandler):
    allowed_methods = ('GET',)
    model = Event
    fields = ('name', 'description', 'start_timeframe', 'end_timeframe', ('location', (('building', ('abbreviation', 'name', 'latitude', 'longitude')), 'room')), ('organization', ('name', 'description', 'website', ('owner', ('username', 'first_name', 'last_name', 'email')))))
 
    def read(self, request, latitude=None, longitude=None):

        #Get delay (maximum wait time before event) in minutes, default to 240 minutes
        delay = int(request.GET.get('delay', '240'));

        #Filter out passed events and events that don't meet the provided delay
        events = Event.objects.all().exclude(start_timeframe__lt=datetime.now()).exclude(start_timeframe__gte=(datetime.now()+timedelta(minutes=delay)));

        #If latitude and longitude are set, perform a nearby events search
        if latitude and longitude:
            #Make sure latitude and longitude are clamped to respective domains
            latitude = max(-90.0, min(90.0, float(latitude)))
            longitude = max(-180.0, min(180.0, float(longitude)))

            #Creata a point from the given latitude and longitude
            pnt = fromstr('POINT(%s %s)' % (longitude, latitude))

            #Get radius in meters to search within, clamp between 10 and 250 meters, default to 125 meters
            radius = float(request.GET.get('radius', '125'))
            radius = max(10.0, min(500.0, radius))

            #Query database for buildings within the provided radius
            buildings = Building.objects.filter(coordinate__distance_lte=(pnt, radius))

            #Make sure the collection of buildings isn't empty    
            if not buildings:
                return rc.NOT_FOUND
      
            #Query database for locations which are in the nearby buildings  
            locations = Location.objects.filter(building__in=buildings)

            #Make sure the collection of locations isn't empty
            if not locations:
                return rc.NOT_FOUND
    
            #Filter collection of events down to those which are in nearby locations
            events = events.filter(location__in=locations)

        #Make sure events isn't empty
        if not events:
            return rc.NOT_FOUND
        return events
