import os
import platform
import json

f = open('profiles.json')
data = json.load(f)

prefix = "" if platform.system() == "Windows" else "./"

for p in data['profiles']:
    os.system('%sgradlew build -Dprofile="%s"' % (prefix, p["minecraft-version"]))
