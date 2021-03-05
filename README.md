# Water level indicator

A nodemcu tied to an ultrasonic sensor detects the distance to the water level at every ten seconds and uploads it to a firebase realtime db.

Android app, takes data from this db, and calculates current percentage based on min and max values it can have. Outlier values are discarded.


##screenshots

![main.jpg](https://raw.githubusercontent.com/moonblade/water-level-indicator/master/screenshots/main.jpg)

![settings.jpg](https://raw.githubusercontent.com/moonblade/water-level-indicator/master/screenshots/main.jpg)
