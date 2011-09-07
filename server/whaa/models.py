from django.db import models
from django.contrib.auth.models import User
from datetime import datetime, timedelta
from django.contrib.gis.db import models
from django.contrib.gis.geos import *

class EventManager(models.Manager):
    def near(self, latitude=None, longitude=None, radius=None, delay=None):
        #Get delay (maximum wait time before event) in minutes, default to 240 minutes
        delay = max(30.0, min(2880.0, float(delay)))

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
            radius = max(10.0, min(500.0, radius))

            #Query database for buildings within the provided radius
            buildings = Building.objects.filter(coordinate__distance_lte=(pnt, radius))

            #Make sure the collection of buildings isn't empty
            if not buildings:
                return None

            #Query database for locations which are in the nearby buildings
            locations = Location.objects.filter(building__in=buildings)

            #Make sure the collection of locations isn't empty
            if not locations:
                return None

            #Filter collection of events down to those which are in nearby locations
            events = events.filter(location__in=locations)

        #Make sure events isn't empty
        if not events:
            return None
        return events

class Event(models.Model):
    objects = EventManager()
    name = models.CharField(help_text="Enter the name of the event.", max_length=200)
    description = models.TextField(help_text="Enter a short description of the event.", null=True, blank=True)    
    location = models.ForeignKey('whaa.Location')
    organization = models.ForeignKey('whaa.Organization')
    start_timeframe = models.DateTimeField(help_text="Enter the starting date and time.")
    end_timeframe = models.DateTimeField(help_text="Enter the ending date and time.")
    class Meta:
        unique_together = ['location', 'name', 'organization', 'start_timeframe', 'end_timeframe']
    def __unicode__(self):
        return u'%s' % (self.name)

class Location(models.Model):
    building = models.ForeignKey('whaa.Building')
    room = models.CharField(help_text="Enter a room number.", max_length=12)
    class Meta:
        unique_together = ('building', 'room')
    def __unicode__(self):
        return u'%s - %s' % (self.building, self.room)
    def natural_key(self):
        return [self.building.natural_key(), self.room]
    natural_key.dependencies = ['whaa.Building']

class Building(models.Model):
    abbreviation = models.CharField(help_text="Enter the abbreviation of the building's name", max_length=10, unique=True)
    name = models.CharField(help_text="Enter the name of the building.", max_length=200, unique=True)
    coordinate = models.PointField(blank=True, null=True)
    objects = models.GeoManager()
    def latitude(self):
        return self.coordinate.y;
    def longitude(self):
        return self.coordinate.x;
    def __unicode__(self):
        return u'%s - %s' % (self.abbreviation, self.name)
    def natural_key(self):
        return [self.abbreviation, self.name, self.latitude, self.longitude]

class OrganizationManager(models.Manager):
    def get_by_natural_key(self, name):
        return self.get(name=name) 

class Organization(models.Model):
    objects = OrganizationManager()
    name = models.CharField(help_text="Enter your organization's name.", max_length=200, unique=True)
    owner = models.ForeignKey(User)
    description = models.TextField(help_text="Please enter a short descrption of your organization.", null=True, blank=True)
    website = models.URLField(help_text="Enter your organization's website address.", verify_exists=True, max_length=600, null=True, blank=True)
    def __unicode__(self):
        return u'%s' % (self.name)
    def natural_key(self):
        return [self.name, self.description, self.website]
