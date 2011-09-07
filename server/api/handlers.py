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
        delay = int(request.GET.get('delay', '240'));
        radius = float(request.GET.get('radius', '125'))
        return Event.objects.near(latitude, longitude, radius, delay)
