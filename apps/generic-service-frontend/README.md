## generic-service-frontend

Homebrew webOS services are becoming more and more common. They power many popular homebrew applications like Internalz, Preware, LED Torch, MyTether, CPUScaler, AutoCorrect Edit, etc.

However, one frustrating thing is that, in most cases, there's nothing in the launcher after the install. In fact, there's no way to know if the service is installed and running like it should. This means users are left wondering if the install completed and leaves them without an easy way to uninstall the service

So I'd like to help fill that void and give homebrew service developers an easy way to provide immediate feedback to the user. So I've created this opensource Generic Service Frontend application.

And for developers interested, here's the best part. The application is organized in such a way that to configure it for your service, you only need to edit the appinfo.json file and provide your own icon app.

That's it! Literally takes only a few seconds to setup.

### Changelog
v1.1 - December 17, 2010
- App now stays visible in launcher, so you can use Palm-style prerm scripts to support in-launcher uninstallation.
- Supports a custom description paragraph of text.
- Optional support for a donation link
- Added support for full changelog display
