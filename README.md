# obd2 reader for RASPBIAN

## Instruction
### Prepare your system 
1. Install rxtx libs for java
[http://rxtx.qbang.org/](http://rxtx.qbang.org/)
```sh
sudo apt-get install librxtx-java
```
2. Instal bluetooth tools
```sh
sudo apt-get install bluetooth bluez blueman
sudo reboot
```
3. Connect your OBD2 adapter with Bluez
4. Scan connected devices and get your device address
```sh
hcitool scan
```
5. Bind your device to rxtx 
```sh
rfcomm bind 0 34:D2:12:02:82:72
```
### Build and run app
1. Build jar with Gradle 
```sh
gradlew fatJar
```

2. Run jar with java lib path (which contains rxtx libs)
```sh
java -Djava.library.path=/usr/lib/jni -jar obdpi-all-1.0.jar
```

3. If running successful you will be asked for rxtx port to connect. 
Before that the program will prompt all available ports to use.

Example:
```sh
[main] INFO Main - Available ports:
[main] INFO Main - --> /dev/rfcomm0
[main] INFO Main - Enter binded com port:
/dev/rfcomm0
[main] INFO Main - Port /dev/rfcomm0 was chosen
[main] INFO Main - Starting service...
                          Reset OBDELM327v1.3aOBDGPSLogger
                           Echo Off    ATE0OK
                           Echo Off        OK
                      Line Feed Off        OK
                            Timeout        OK
               Select Protocol AUTO        OK
                      Vehicle Speed    37km/h
                         Engine RPM   5238RPM

```
