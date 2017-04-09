## homebrew-js-service-framework
Homebrew Javascript Service Framework for webOS

HP webOS 2.0 brought with it javascript system services. These services are run via Node.js and are amazingly powerful. So naturally, HP locked down third party services with a jailer. For homebrew however, full system root access is a must.

This homebrew js service framework remedies the situation. It adds a custom script to /var/usr/bin/run-homebrew-js-service

All a homebrew service needs to do different from standard service format is to edit the dbus file and change the script path to the new script.
