## mplayer-webos

MPlayer is a movie player which runs on many systems. It plays most MPEG/VOB, FLV, AVI, Ogg/OGM, VIVO, ASF/WMA/WMV, QT/MOV/MP4, RealMedia, Matroska, NUT, NuppelVideo, FLI, YUV4MPEG, FILM, RoQ, PVA files, supported by many native, XAnim, and Win32 DLL codecs. You can watch VideoCD, SVCD, DVD, 3ivx, DivX 3/4/5, WMV and even H.264 movies.


Chomper from the Treo8 forums released a PDK-based MPlayer app for webOS. This is a separate unofficial continuation of that project. This project, however, is not PDK-based. Instead, it runs with a Mojo app frontend and a Node.js backend service (and a Java service, for backwards compatability with webOS 1.4.5 devices).

This offers several advantages. For one, cross-app launching can be used to let other developers open/stream videos in MPlayer. In addition, I've included the option to register MPlayer as a file handler, so MPlayer can be used as the system default player for extensions of your choice, allowing apps like Web, Email, and Internalz to use MPlayer without modification.

Supports webOS 1.4.5 and 2.x.

Licensed under GPL v2.

webOS Mojo app, Node.js service, and Java service by Jason Robitaille.
Mplayer binary built on Ubuntu 10.10 32bit Linux using the WebOS-Internals WIDK using the standard MPlayer source patched with a patch originally by Chomper and modified by WebOS-Internals for WIDK usage.

### Installation

Requires Homebrew JS Service Framework.

Install the release IPK for your device via [WebOS Quick Install](https://github.com/JayCanuck/webos-quick-install).

- armv7 is Pre/Pre+/Pre2
- armv6 is Pixi/Pixi+


### Change Log
v1.0.2 - March 25, 2011
- Now uses "-vf scale" rather than "-framedrop -fs -pp 10"

v1.0.1 - March 20, 2011
- Fixed a bug preventing Preference popup access

v1.0.0 - March 20, 2011
- Initial release