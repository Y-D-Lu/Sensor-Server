# Sensor-Server

The server part of Sensor OS casting, provides secondary display and control for Android devices connected on Wi-Fi P2P.
It also provides drag'n'drap operation similiar with the [Smartisan TNT Workstation](https://www.smartisan.com/tnt/overview).
It works on _Android_, _Windows_ and _MacOS_, theoretically.
The package "kastro" is powered by the [scrcpy](https://github.com/Genymobile/scrcpy).



## Environment

The Server(_Android_) part requires Nitrogen OS at API 28 (Android 9.0) with the customized Sensor OS pack, which enables controlling as well as processing drag'n'drop.
The Client(_Android_, _Windows_) part runs a Client APK or Win EXE.

## Features

### Magic Display

We create an illusory display at the beginning of the cast, then start our launcher on it.
It allows to display different apps on server and client.

### Wi-Fi P2P connection

Devices are connected by Wi-Fi P2P, which makes it possible to keep your own Wi-Fi connection, thus, the two devices can be connected to different APs, and without cutting out the existing connection, the two devices can be connected.

### Drag'n'drop

Drag'n'drop to share file to app, such as sending image to wechat friend, referring to [TNT intro on ytb](https://www.youtube.com/watch?v=i9bc2ko-a8k) or [TNT intro on bilibili](https://www.bilibili.com/video/av39075190) from 2:36 to 3:07.

## How to build
Refer to [BUILD](BUILD.md).

## Licence

    Copyright (C) 2019 Lu Yuedong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
