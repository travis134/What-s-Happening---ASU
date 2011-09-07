from django.conf.urls.defaults import patterns, include, url
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    (r'^$', 'whaa.views.default'),
    (r'^admin/', include(admin.site.urls)),
    (r'^api/', include('api.urls')),
    (r'^maps/$', 'whaa.views.map'),
    (r'^events/(?P<latitude>(-?)(\d{0,2})(\.\d+)?),\s?(?P<longitude>(-?)(\d{0,3})(\.\d+)?)/$', 'whaa.views.events'),
)
