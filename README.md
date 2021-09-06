# Water level indicator

A nodemcu tied to an ultrasonic sensor detects the distance to the water level at every ten seconds and uploads it to a firebase realtime db.

Android app, takes data from this db, and calculates current percentage based on min and max values it can have. Outlier values are discarded.

The app widget didn't really function as I wanted it to, so took a second nodemcu and created a water level indicator that takes data from firebase rtdb and displays it on an led matrix.

As an experiment in 3d printing and pcb printing, added eagle board schematic to print the water level indicator on pcb. Will end up removing the indicator and wire it up to relays to auto control the motor when water level is low.

### Update 2021-09-05

Bought a waterproof ultrasonic sensor, but the version supplied by the manufactorer seems to have some faults. One the minimum distance is too high and two it was giving random readings and not accurate ones. Which makes it unusable for this project. So ended up buying a new normal ultrasonic sensor and gave the waterproof one for return. But to learn previous lessons, figured would keep the sensor on top of the lid this time around so that corrosion doesn't play that big a role and it would be impossible for it to fall into water.

To that end, drilled a couple of holes in the lid of the water tank and stuck the sensor through it covering most of it with insulation tape. Also overhauled the microchip so that its easily replacable and works directly off of ac power doing its own coversion instead of relying on a phone charger.

Old vs new
![image](https://user-images.githubusercontent.com/9362269/132161640-8e9af23f-c296-4ebe-bd39-4643f38173a5.png)


Underside of lid
![image](https://user-images.githubusercontent.com/9362269/132161556-d9242ffb-65bf-441a-befc-98c5a41b7320.png)

Topside connected up
![image](https://user-images.githubusercontent.com/9362269/132161731-35425384-af4b-4f77-8e5a-77ded42fbf48.png)


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


