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
Detected mapSize: 8192
Resulting image side size will be: 2048px
File load:
|----------------|
|----------------|
Done.
Time to solve stats: 253ms
mean = 45586
rms = 45593
min = 0
max = 65520
D2 = 1644.0
startHist = 37373
k = 2.0
File saving time:  = 6s
Roads loaded
All work done!
Resulting map image: '5_mapWithObjects.png'.
```

After that review new created files. World map with all objects may be found in *5_mapWithObjects.png* image.

## Example image
![Example](https://db3pap001files.storage.live.com/y4mqkEaicnjzHH00idbfPXhfIuQE-ToGzUHYzg5xnSbkJY-aM9Dw9XDIdUGWWbWqLYSxzOBdNBgs6YoIPvG14BMw11JFX5B6MYOoFdI6SL_WSnHqso2OfbYmvfZ4k5MFWki9i2N-T_c6wQqHdNYKnW1P9QN1i34buk3_fWpsEqDH9Rmdgt7ZzhaWh-ED6lq5gQ0bFPj74bhBVV-l4U5HGcPuQ/5_mapWithObjects.png?psid=1&width=817&height=817&cropMode=center)
