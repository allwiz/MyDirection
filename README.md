# MyDirection

**Developement version**
<br><a id="raw-url" href="https://github.com/allwiz/MyDirection/blob/main/bin/net.allwiz.mydirection-v1.0.2-release.apk">My Direction APK</a>

**Screenshots**
<br>My Direction Android application. Manage your places and easily use Google Maps.
![alt text](https://github.com/allwiz/MyDirection/blob/main/doc/mydirection.app.screenshots.png "My Direction")

**Build Guide**
<br>You need to use your own api key to search the places. If you get it, please replace '@string/place_api_key' with the api key
1. Get an API Key
https://developers.google.com/places/android-sdk/get-api-key
2. Make a fingerprint
SHA-1 fingerprint: Right pane in Android Studio > Gradle > My Direction(or your project) > Tasks > android > signingReport
3. Enable Billing
We need to enable billing in order to search the place.
https://console.cloud.google.com/project/_/billing/enable

