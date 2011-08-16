from django.db import models
from django.contrib.gis.db import models
from django.contrib.auth.models import User
from django.contrib.gis.geos import Point

class Event(models.Model):
    name = models.CharField(help_text="Enter the name of the event.", max_length=200)
    description = models.TextField(help_text="Enter a short description of the event.", null=True, blank=True)    
    location = models.ForeignKey('whaa.Location')
    organization = models.ForeignKey('whaa.Organization')
    start_timeframe = models.DateTimeField(help_text="Enter the starting date and time.")
    end_timeframe = models.DateTimeField(help_text="Enter the ending date and time.")
    class Meta:
        unique_together = ['location', 'name', 'organization']
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
