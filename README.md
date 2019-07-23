# 7D2D_RGW_Create_Map_Preview_Image_Tool ![build](https://travis-ci.org/ognivo777/7D2D_RGW_Create_Map_Preview_Image_Tool.svg?branch=master)
The simple tool for create informative map image with schematics roads, houses, caves and water towers. Relief information also applied to make map more imformative about the landscape.

## Example image
![Example](https://drive.google.com/uc?export=download&id=1PtXNDc0GGHoz0oQKDgNGOPJEP78-U22p)

## Prerequists
Java 8 or higher. You can download java [here](https://www.oracle.com/technetwork/java/javase/downloads/index.html).

## Usage
Download [latest release](https://github.com/ognivo777/7D2D_RGW_Create_Map_Preview_Image_Tool/releases/latest) and put 7dtd-rgw-map-image-builder.jar into `%USER_HOME%\AppData\Roaming\7DaysToDie\GeneratedWorlds\<world name>\`.
After that use double click on the jar or use command line for execute:
`java -jar 7dtd-rgw-map-image-builder.jar`

You will see program output in window:
![](https://drive.google.com/uc?export=download&id=1BEXWLqO5bD2IOOSQDARtBJ1yAI_iAV97)

And final result appear:
![](https://drive.google.com/uc?export=download&id=1mRBLDkTL-1--_5n_0MbyEFMLE2iM09Md)

After that review new created files. World map with all objects may be found in *8_mapWithObjects.png* image.

## Troubleshooting

* In some case you get error "TOO LITTLE" helpful add enviroment variable `_JAVA_OPTIONS=-Xmx256m` instead of specifing java vm options directly in command line

## Feedback
Support and discussion is available in thread on the game official forum: https://7daystodie.com/forums/showthread.php?120690-Random-generated-(RGW)-MAP-Preview-image-export-Tool
