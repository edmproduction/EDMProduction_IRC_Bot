import requests, time, sys, pprint, datetime

p = pprint.PrettyPrinter() #for debugging

##### Config
import ConfigParser, os
config = ConfigParser.RawConfigParser()
config.readfp(open("threadbot.cfg"))
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

try:
    day =  config.get("threadbot", "debug_day")
except ConfigParser.NoOptionError, e:
    d = datetime.date.today()
    day = d.weekday()
sort_by_new = False

# 0 / Monday / Feedback thread
# 1 / Tuesday / How do I make this sound thread
# 2 / Wednesday / There are no stupid questions thread
# 3 / Thursday / Marketplace thread
if day == 0:
    title =  config.get("monday", "title") + ' (' + d.strftime("%B %d") + ')'
    text = config.get("monday", "text")
    print title
    print text
    thread_call = {'api_type': 'json', 'kind': 'self', 'sr':sr, 'uh': mh, \
      'title':  title, 'text': text }
    sort_by_new = True
elif day == 1:
    thread_call = {'api_type': 'json', 'kind': 'self', 'sr':sr, 'uh': mh, \
     'title': '"How do I make this sound?" Thread (' + d.strftime("%B %d") + ')', \
     'text': "Post all \"How do I make this sound?\" questions" + \
      " in this thread until the next one is created. Any threads made that " + \
      " should be a comment here will" + \
      " be removed.\nPlease include a timestamped link to your request." }
    sort_by_new = True
elif day == 2:
    thread_call = {'api_type': 'json', 'kind': 'self', 'sr':sr, 'uh': mh, \
     'title': '"No Stupid Questions" Thread (' + d.strftime("%B %d") + ")", \
     'text': "While you should search, read the Newbie FAQ, and " + \
      "definitely [RTFM](http://en.wikipedia.org/wiki/RTFM) when you have a question, some days " + \
      "you just [can't get rid of a bomb](http://cdn.uproxx.com/wp-content/uploads/2011/08/tumblr_lpnoa80qJS1qj4b9to2_r1_500.gif)." + \
      " Ask your ~~stupid~~ questions here." }
    sort_by_new = True
elif day == 3:
    thread_call = {'api_type': 'json', 'kind': 'self', 'sr':sr, 'uh': mh, \
     'title': 'edmp Marketplace Thread (' + d.strftime("%B %d") + ")", \
     'text': "This thread is where you may share or request services" + \
              " you have to offer to the edmproduction community. Post your " + \
              " programs and plugins, your mastering/teaching/coaching/artwork " + \
              "services, your website/tutorials, your preset/sample packs, your" + \
              " labels- anything but actual music itself.\n\n**Rules:**\n\n1. No " + \
              "posting music. No posting your soundcloud when you're looking for " + \
              "labels, no ghost production; nothing that constitutes you selling or " + \
              "sharing your own created tracks.\n\n2. Spam will not be tolerated. Repeated " + \
              "postings for the same product/service in the same thread will not be allowed, " + \
              "but you can wait until the following week to repost.\n\n3. Mark very clearly " + \
              "whether you're requesting or offering services, and if you're offering them, " + \
              "whether those services are paid or free.\n\nAs with the rest of the subreddit, " + \
              "final decisions over what constitutes an acceptable posting here will be at the " + \
              "sole discretion of the mods." }
    sort_by_new = False
else:
    sys.exit()

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

print "herro?"

#### Mod-Distinguish thread

dist_data = {'api_type': 'json', 'how':'yes', 'id':name, 'uh': mh}
r = s.post('http://www.reddit.com/api/distinguish', data=dist_data, cookies = cookie)
thread_r = r.json()['json']
if len(thread_r['errors']) > 0:
    p.pprint(thread_r)


#### Edit to include "sort by new" link
if sort_by_new:
    url = url + '?sort=new'
    body_text = "* [Please sort this thread by new!]("+url+")\n\n*" + thread_call['text']
    edit_data = {'api_type': 'json', 'text': body_text, 'thing_id':name, 'uh': mh}
    r = s.post('http://www.reddit.com/api/editusertext', data=edit_data, cookies = cookie)

print url
print "errors:"
p.pprint(r.json()['json']['errors'])

