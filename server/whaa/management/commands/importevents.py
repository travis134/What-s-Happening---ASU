from django.core.management.base import BaseCommand, CommandError
from whaa.models import Event, Location, Building, Organization
from django.contrib.auth.models import User
from datetime import datetime
import csv, re, string

pattern = re.compile('[^A-Za-z0-9\@\.\+\-\_]*')

class Command(BaseCommand):
    args = '<filename>'
    help = 'Imports events from the specified csv file'

    def handle(self, *args, **options):
        if not args:
             raise CommandError('Please enter a valid filename')
        filename = args[0]
        if filename:
            try:
                f = open(filename, 'rb')
                eventReader = csv.reader(f)
                map = {}
                fields = eventReader.next()
                for field in fields:
                    map[field] = fields.index(field)
                for row in eventReader:
                    l = self.storeLocation(row[map['Building']], row[map['Room']])
                    u = self.storeUser(pattern.sub('',row[map['Contact/Instructor']]).lower())
                    o = self.storeOrganization(row[map['Course/Customer']], u)
                    e = self.storeEvent(row[map['Title']], row[map['Start Date']], row[map['End Date']], row[map['Start Time']], row[map['End Time']], l, o) 
                f.close()
            except IOError as (errno, strerror):
                raise CommandError('I/O error: %s' % strerror)

    def storeLocation(self, building, room):
        try:
            b = Building.objects.get(abbreviation=building)
            try:
                l = Location.objects.get(building=b, room=room)
                return l
            except Location.DoesNotExist:
                self.stdout.write('Location %s - %s does not exist, creating...\n' % (building, room))
                l = Location(building=b, room=room)
                l.save()
                return l
        except Building.DoesNotExist:
            raise CommandError('Building %s does not exist' % building)

    def storeUser(self, name):
        try:
            u = User.objects.get(username=name)
            return u
        except User.DoesNotExist:
            self.stdout.write('User %s does not exist, creating...\n' % name)
            u = User.objects.create_user(name,'tsein@asu.edu')
            return u

    def storeOrganization(self, name, owner):
        try:
            o = Organization.objects.get(name=name)
            return o
        except Organization.DoesNotExist:
            self.stdout.write('Organization %s does not exist, creating...\n' % name)
            o = Organization(name=name, owner=owner)
            o.save()
            return o

    def storeEvent(self, title, start_date, end_date, start_time, end_time, location, organization):
        start_timeframe = datetime.strptime('%s %s' % (start_date, start_time), '%m/%d/%Y %I:%M %p')
        end_timeframe = datetime.strptime('%s %s' % (end_date, end_time), '%m/%d/%Y %I:%M %p')
        try:
            e = Event.objects.get(name=title, start_timeframe=start_timeframe, end_timeframe=end_timeframe, location=location, organization=organization)
            return e
        except Event.DoesNotExist:
            self.stdout.write('Event %s does not exist, creating...\n' % title)
            e = Event(name=title, start_timeframe=start_timeframe, end_timeframe=end_timeframe, location=location, organization=organization)
            e.save()
            return e
