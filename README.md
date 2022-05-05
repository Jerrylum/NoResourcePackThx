# No resource pack! Thank you.

No resource pack! Thank you. is a mod that allows you to deny resource pack requested by the server.

# Intro

I believe the player should have the final say on what the game looks like. Sometimes, you don't want to install the resource pack provided by the server. Yet, certain servers don't let you join the game unless you install their resource pack.

Downloading unknown zip files to your computer is not a good idea. This could lead to a potential exploit where a malicious file inside a downloaded server resource pack could lead to an RCE exploit, allowing them to gain root access to your computer.

In addition, it was also a vulnerability discovered in Minehut, a popular server network. Minehut protects its players by masking their IP addresses. However, the server owner could bypass the IP-spoof protection if you accept a resource pack hosted on a custom server.  


## How Does It Work?

When you join the server, the server sends the client the link to download the resource pack. If you click "accept", the client tells the server you accept it and installs it. Otherwise, the client tells the server you reject it and the server will kick you from the game.

This mod modifies the packets sent to the server. The client always tells the server that the player accepted the resource pack even if the player didn't. The player will then receive a download link in the chat. They can scan the resource pack or install it manually afterward.

This mod can also work with Replay Mod, which modifies how the game handles resource packs.
