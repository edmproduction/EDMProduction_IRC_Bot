import requests, time, sys, pprint, datetime
from optparse import OptionParser
import ConfigParser, os

p = pprint.PrettyPrinter() #for debugging

##### Config
parser = OptionParser()
parser.add_option("-c", "--config", dest="conf_path", type="string", help="config file path")
(options, args) = parser.parse_args()

config = ConfigParser.RawConfigParser()
config.readfp(open(options.conf_path)) #"threadbot.cfg"
sr = config.get("threadbot", "subreddit")
user = config.get("threadbot", "username")
pw = config.get("threadbot", "password")

###### Login
user_pass_dict = {'user': user,
              'passwd': pw,
              'api_type': 'json'}

s = requests.Session()
s.headers.update({'User-Agent' : 'edmproduction weekly threadbot by /u/fiyarburst'})
r = s.post('http://www.reddit.com/api/login', data=user_pass_dict)
login = r.json()['json']['data']
cookie = {'cookie': login['cookie']}
mh = login['modhash']

##### Check day, select appropriate thread
d = datetime.date.today()
try:
    day =  config.getint("threadbot", "debug_day")
except ConfigParser.NoOptionError, e:
    day = d.weekday()
sort_by_new = False

# 0 / Monday / Feedback thread
# 1 / Tuesday / How do I make this sound thread
# 2 / Wednesday / There are no stupid questions thread
# 3 / Thursday / Marketplace thread
dayname = "waffles"
if day == 0:
    dayname = "monday"
    sort_by_new = True
elif day == 1:
    dayname = "tuesday"
    sort_by_new = True
elif day == 2:
    dayname = "wednesday"
    sort_by_new = True
elif day == 3:
    dayname = "thursday"
    sort_by_new = False
else:
    sys.exit() # woo inelegance

try:
    title = config.get(dayname, "title") + ' (' + d.strftime("%B %d") + ')'
    text = config.get(dayname, "text")
except:
    sys.exit() #nothing found for today
text = "\n\n".join(text.split("\n"))
thread_call = {'api_type': 'json',
                 'kind': 'self', 
                 'sr':sr, 'uh': mh, 
                 'title':  title, 
                 'text': text }

#### Post thread, 'r' is the results; thread_r is a dict of r

r = s.post('http://www.reddit.com/api/submit', data=thread_call, cookies = cookie)
thread_r = r.json()['json']

if len(thread_r['errors']) > 0:
    print "fuckin captcha or something"
    iden = thread_r['captcha']
#    captcha = s.get('http://www.reddit.com/captcha/' + iden)
    import subprocess
    subprocess.call(['open', 'http://www.reddit.com/captcha/' + iden ])
    thread_call['captcha'] = input("Captcha (enclose in quotes):")
    thread_call['iden'] = iden
    r = s.post('http://www.reddit.com/api/submit', data=thread_call, cookies = cookie)
    thread_r = r.json()['json']['data']
    print r.json()
    if len(thread_r['errors']) > 0:
        p.pprint(thread_r)

thread_r = thread_r['data']
name = thread_r['name']
tid = thread_r['id']
url = thread_r['url'] 

#### Mod-Distinguish thread

dist_data = {'api_type': 'json', 'how':'yes', 'id':name, 'uh': mh}
r = s.post('http://www.reddit.com/api/distinguish', data=dist_data, cookies = cookie)
thread_r = r.json()['json']
if len(thread_r['errors']) > 0:
    p.pprint(thread_r)


#### Edit to include "sort by new" link
if sort_by_new:
    url = url + '?sort=new'
    body_text = "**[Please sort this thread by new!]("+url+")**\n\n " + thread_call['text']
    edit_data = {'api_type': 'json', 'text': body_text, 'thing_id':name, 'uh': mh}
    r = s.post('http://www.reddit.com/api/editusertext', data=edit_data, cookies = cookie)

print url
print "errors:"
p.pprint(r.json()['json']['errors'])




##### WOOOOOOOOOOOOOOO IM LISTENING TO PENDULUM AND DRINKING MOUNTAIN DEW



