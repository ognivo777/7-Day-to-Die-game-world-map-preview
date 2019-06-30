# 7D2D_RGW_Create_Map_Preview_Image_Tool
The simple tool for create informative map image with schematics roads, houses, caves and water. Relief information also applied to make map more imformative about the landscape.

## Usage
Download latest release an put 7dtd-rgw-map-image-builder.jar into `%USER_HOME%\AppData\Roaming\7DaysToDie\GeneratedWorlds\<world name>\`.
After that double clicking the jar or use command line execution:
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
Resulting map image: '4_mapWithObjects.png'.
```

After that review new created files. World map with all objects may be found in *4_mapWithObjects.png* image.
![Example](https://drive.google.com/uc?export=download&id=1hcLq6xomVYWAzoHsWW_E8ydCaemAvEF_)
