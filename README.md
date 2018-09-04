# sticky-note
simple Java-based todo memo, synchronized with www.protectedtext.com.

![Popup Window](screenshot.png)

Features
-------------------
* Lives in Tray
* global keyboard shortcut
* steals focus when pop-ups
* does not terminate when press "close" button
* instant secure AES256 synchronization with ProtectedText
* Android App from ProtectedText project
* cross-platform, based on Java



Build
-------------------
run build.cmd

Install
-------------------
copy target\sticky-notes-0.0.1-SNAPSHOT-jar-with-dependencies.jar to somewhere.  
Place config.json alongside with the jar with your credentials for www.protectedtext.com:

{"pass":"YourPass","site":"/yourSite"}

Do not put http://www.protectedtext.com in "site" param.

**To run properly the app requires Java support for AES256 else you'll get "Illegal Key size" exception**.
**To enable this support install "Unlimited JCE Policy" files into Java installation**.

Run
-------------------
To run execute run.cmd

To open popup window presl Alt-Shift-1.  
Every time it is opened text is updated from site, when it get closed, new text is pushed to site.

To hide window press Esc.

There is also an icon in System Tray.

Develop
-------------------
Any ideas and contributions are welcome.


BUGS
-------------------
a few.