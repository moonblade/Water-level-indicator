# Water level indicator

A nodemcu tied to an ultrasonic sensor detects the distance to the water level at every ten seconds and uploads it to a firebase realtime db.

Android app, takes data from this db, and calculates current percentage based on min and max values it can have. Outlier values are discarded.

The app widget didn't really function as I wanted it to, so took a second nodemcu and created a water level indicator that takes data from firebase rtdb and displays it on an led matrix.

As an experiment in 3d printing and pcb printing, added eagle board schematic to print the water level indicator on pcb. Will end up removing the indicator and wire it up to relays to auto control the motor when water level is low.

### Update 2021-09-01 

The water level sensor was connected to the lid of hte water tank by threading it through the lid. The thread broke and the full box took a dip in the water and fried the electronics.
Till then it was getting a ton of water droplets on it due to evaporation as well. So the board looks really gnarly due to that.

For the updated version, will be using waterproof ultrasonic sensor and the ic will be kept seperate and not inside the lid anymore.

![image](https://user-images.githubusercontent.com/9362269/132033304-b6c784d4-07c3-428f-9286-d6dd2637c560.png)

## Arduino code

### WaterLevelIndicator

Take data from firebase rtdb and display it on an 8x8 LED matrix based on value of printMode.

![IMG_20210311_173010](https://user-images.githubusercontent.com/9362269/110785062-7ed93e00-8290-11eb-8830-0068260a9fc1.jpg)

![IMG_20210311_172954](https://user-images.githubusercontent.com/9362269/110785068-83055b80-8290-11eb-8b11-7795970e5736.jpg)

Installed in kitchen
![image](https://user-images.githubusercontent.com/9362269/120057833-f9318700-c063-11eb-82da-114e88e869ca.png)


### WaterLevelSensor

Get distance to water level and upload it to firebase rtdb.

Installed on the underside of the water tank lid. Had to do some rewiring of the electrical connections in the roof to provide uninterupted power bypassing a switch.
![image](https://user-images.githubusercontent.com/9362269/120058157-968dba80-c066-11eb-8d4d-26f66873411d.png)

## App Screenshots

![main](https://user-images.githubusercontent.com/9362269/110064173-9b5a0f80-7d92-11eb-9c23-230dae3abe87.jpg)

![Screenshot_20210311-172932](https://user-images.githubusercontent.com/9362269/110785179-a4664780-8290-11eb-9458-9b5070759e48.jpg)


