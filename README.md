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
![](https://drive.google.com/uc?export=download&id=1rcVPmVu8QBkg7eFYtEv22MKxtt60dj7-)
If you want to use inverse zoom specify `-m` command line argument.

After that review new created files. World map with all objects may be found in *8_mapWithObjects.png* image.

## Troubleshooting

* In some case you get error "TOO LITTLE". In this case would be helpful add enviroment variable `_JAVA_OPTIONS=-Xmx256m` instead of specifing java vm options directly in command line

## Feedback
Support and discussion is available in thread on the game official forum: [Random generated (RGW) MAP Preview image export Tool](https://bit.ly/3Jc6Jle) 

Also you can join Discord server https://discord.gg/c8Pt5B2 and ask me there:)
