# PG3D-Injector
- writing into android application lib.so with offset &amp; hex bytes <br />
- support root and non root devices

# Usages 
- Git Clone This Project.
- Build with Android Studio.
- Install the app-debug into your android devices.
- If you have root make sure you grant the RootAccess.
- If you're non root user then make sure you're using any virtual app with root that works.
- open the debug/injector
- Open the original game (Example: Open Pixel Gun 3D so it the Debug can target the PID).
- click Inject
- logs should say true, means working, if false then either root isn't granted or you broke something.
- done

This is one of my projects that I had been working during summer. The PG3D Injector. This does not have to be PG3D.
This project can work for any games instead of Pixel Gun 3D. 
It doesn't contain any c++, but java/kotlin. You can find everything inside MainActivity.kt

This is an alternative way to inject mods inside the game instead of implementing your mod menus, making the game having signature checks and causing some integrity checks.


NOTE: Yes, this project has root inside it. You will neeed root for your device to grant root access to the debug.apk

Everything is implemented, root, some examples of the mods in MainActivity.
Took the inspiration of Jbro129 PG3D Launcher.

# Preview

![](http://imgur.com/a/kWHaTZv)

# Code
-Main Codes and mods in [MainActivity.kt](https://github.com/SliceCast/External-PG3D-Injector/blob/master/app/src/main/java/com/slicecast/MainActivity.kt)

-Values XML for Injectors background color, etc [Values](https://github.com/SliceCast/External-PG3D-Injector/tree/master/app/src/main/res/values)

-Injectors Layouts and Background Customization [activity_main.xml](https://github.com/SliceCast/External-PG3D-Injector/blob/master/app/src/main/res/layout/activity_main.xml)



# Credits
- BryanGIG - [KMrite-Injector](https://github.com/BryanGIG/KMrite)
- jbro129 - [LibInjector](https://github.com/jbro129/LibInjector)
- topjohnwu - [libsu](https://github.com/topjohnwu/libsu)
