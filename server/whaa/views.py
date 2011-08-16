from django.http import HttpResponse

def whaa_default(request):
    html = "<html><body>Under construction.</body></html>"
    return HttpResponse(html)
