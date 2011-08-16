from django.contrib import admin
from django.contrib.gis import admin
from django import forms
from whaa.models import Event, Location, Building, Organization

class EventAdminForm(forms.ModelForm):
    def clean(self):
        cleaned_data = super(EventAdminForm, self).clean()
        q = Event.objects.filter(start_timeframe__lte=cleaned_data.get("end_timeframe"))
        q = q.filter(end_timeframe__gte=cleaned_data.get("start_timeframe"))
        q = q.filter(location__exact=cleaned_data.get("location"))
        if q.count() > 0:
            if cleaned_data.get("name") != q[0].name or cleaned_data.get("location") != q[0].location or cleaned_data.get("organization") != q[0].organization:
                raise forms.ValidationError("Error, meeting time conflicts with an existing meeting: %s, from %s to %s" % (q[0].name,q[0].start_timeframe,q[0].end_timeframe))
        return cleaned_data

class EventAdmin(admin.ModelAdmin):
    list_display = ['name', 'location', 'organization', 'start_timeframe', 'end_timeframe']
    search_fields = ['name', 'description', 'location', 'organization']
    ordering = ['name']
    form = EventAdminForm

class LocationAdmin(admin.ModelAdmin):
    list_display = ['building', 'room']
    search_fields = ['building', 'room']
    ordering = ['building']

class BuildingAdmin(admin.OSMGeoAdmin):
    list_display = ['abbreviation', 'name', 'latitude', 'longitude']
    search_fields = ['abbreviation', 'name']
    ordering = ['abbreviation']

class OrganizationAdmin(admin.ModelAdmin):
    list_display = ['name']
    search_fields = ['name', 'description']
    ordering = ['name']

admin.site.register(Event, EventAdmin)
admin.site.register(Location, LocationAdmin)
admin.site.register(Building, BuildingAdmin)
admin.site.register(Organization, OrganizationAdmin)
