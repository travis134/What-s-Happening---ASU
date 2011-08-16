from django.conf.urls.defaults import *
from piston.resource import Resource
from api.handlers import EventsHandler

#Create a django-pistons resource, to which the url parameters can be passed
events_resource = Resource(EventsHandler)

urlpatterns = patterns('',
   #Match urls that begin with "near" and contain a GPS coordinate, pass coordinate as parameters: latitude, longitude
   url(r'^events/near/(?P<latitude>(-?)(\d{0,2})(\.\d+)?),\s?(?P<longitude>(-?)(\d{0,3})(\.\d+)?)/$', events_resource),

   #Match urls that begin with "all"
   url(r'^events/all/$', events_resource),
)
