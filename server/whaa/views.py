from django.http import HttpResponse
from django.shortcuts import render_to_response
from datetime import datetime, timedelta
from django.contrib.gis.geos import *
from whaa.models import Event, Location, Building, Organization

def default(request):
    html = "<html><body>Under construction.</body></html>"
    return HttpResponse(html)

def events(request, latitude=None, longitude=None):
    radius = float(request.GET.get('radius', '125'))
    delay = int(request.GET.get('delay', '240'));
    events = Event.objects.near(latitude, longitude, radius, delay)
    if not events:
        return HttpResponse("<html><body>No events found.</body></html>") 
    return render_to_response('events.html', locals(), mimetype="text/html")    

def map(request):
    return render_to_response('map.html')
