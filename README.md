# 7D2D_RGW_Create_Map_Preview_Image_Tool ![build](https://travis-ci.org/ognivo777/7D2D_RGW_Create_Map_Preview_Image_Tool.svg?branch=master)
The simple tool for create informative map image with schematics roads, houses, caves and water towers. Relief information also applied to make map more imformative about the landscape.

## Prerequists
Java 8 or higher. You can download java [here](https://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Usage
Download [latest release](https://github.com/ognivo777/7D2D_RGW_Create_Map_Preview_Image_Tool/releases/latest) and put 7dtd-rgw-map-image-builder.jar into `%USER_HOME%\AppData\Roaming\7DaysToDie\GeneratedWorlds\<world name>\`.
After that use double click on the jar or use command line for execute:
`java -jar 7dtd-rgw-map-image-builder.jar`

You will see program output:
```
fileLength = 134217728
Detected mapSize: 8192
Resulting image side size will be: 2048px
File load:
|----------------|
|----------------|
Done.
Time to solve stats: 348ms
mean = 12396
rms = 12400
min = 5856
max = 38656
D2 = 1426.0
startHist = 5270
k = 1.0
waterLine = 9976
after gamma waterLine = 4120
File saving time:  = 3s
Roads loaded
All work done!
Resulting map image: '6_mapWithObjects.png'.

```

After that review new created files. World map with all objects may be found in *9_mapWithObjects.png* image.

## Example image
![Example](https://drive.google.com/uc?export=download&id=1DdlB3AaNib8JxmPC0ydiPzvTLugQ4Mxx)
