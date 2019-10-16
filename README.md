# disni-plugin-tester
A standalone tester for Storm messaging plugin for DiSNI.

## What is it for?
While writing DiSNI based messaging plugin for Apache Storm, I've found it hard to debug.

This is an approximate environment for messaging plugin, and a plugin may change little to run inside this separate environment.

Packages are under `cn.edu.ustc.acsa.test`. 

## Usage
(Be sure you've got [2.1-modified](https://github.com/libreliu/disni/tree/2.1-modified) version of DiSNI first!)

First, run:
```
mvn clean install
```

Then run the server: 
```
cd target
LD_LIBRARY_PATH=<your_path_to_installed_disni_library>:${LD_LIBRARY_PATH}  java -cp .:../  -jar DiSNITest-1.0-SNAPSHOT-jar-with-dependencies.jar server
```

...and the client:
```
cd target
LD_LIBRARY_PATH=<your_path_to_installed_disni_library>:${LD_LIBRARY_PATH}  java -cp .:../  -jar DiSNITest-1.0-SNAPSHOT-jar-with-dependencies.jar client host port
```

> Several configurations are hard-coded into the source code. Be sure to read before you run. (Eg. Port number 14514)

> Make sure there are `log4j.properties` in reach at classpaths (Or to write log4j conf. codes on your own)

## License
Parts of the code are from Apache Storm, so I've decided to use **Apache License v2.0** to all of these.

## Thanks
ACSA Lab @ University of Science and Technology of China
