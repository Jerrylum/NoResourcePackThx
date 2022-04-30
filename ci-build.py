import os
import json

f = open('profiles.json')
data = json.load(f)

for p in data['profiles']:
    os.system('gradlew build -Dprofile="%s"' % (p["minecraft-version"]))
    # os.system('gradlew build ' + ' '.join(['-D%s="%s"' % (s, p[s]) for s in p]))
