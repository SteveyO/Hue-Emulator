Hue-Emulator Changelog
============
v0.3 (23-January-2015)

New Features:
---
* Retrieving the lights now retrieves the full lights resource as per 1.3 bridges.

Bug Fixes:
---
* Retrieving lights resource doesn't retrieve a LightIdentifer: null and TransitionTime: null anymore, these have been removed.

v0.2 (01-December-2014)

New Features:
---
* UPNP Server. 1st attempt.  If you run the Emulator on Port 80 (if not blocked) some hue apps now work out of the box :-)
* Added Create User + Pushlink functionality (you must click the bridge icon to simulate pushlinking)
* Started Scenes implementation.  Get Scenes and Create Scenes functionality added + recall scenes.  Not 100% tested.
* Can now configure which JSON Strings to show in console (i.e. Requests, Responses, Full Config).
* Re-ordered some of the JSON to be more consistent with a hue bridge

Bug Fixes:
---
* Software version is now a string (not an integer).
* Clear console should now work.
* Added transition time (attribute only, not bulb transitions).