# Water level indicator

A nodemcu tied to an ultrasonic sensor detects the distance to the water level at every ten seconds and uploads it to a firebase realtime db.

Android app, takes data from this db, and calculates current percentage based on min and max values it can have. Outlier values are discarded.


##screenshots

![main](https://user-images.githubusercontent.com/9362269/110064173-9b5a0f80-7d92-11eb-9c23-230dae3abe87.jpg)

![settings](https://user-images.githubusercontent.com/9362269/110064203-ad3bb280-7d92-11eb-8fe8-b40cc28b06be.jpg)

