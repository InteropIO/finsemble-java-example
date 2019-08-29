# Java native interface for Finsemble

## Testing in the IDE

The finsemble-jar project must be built before it can be run. 

Pass the following parameters:
```
finsembleWindowName=FinsembleJar-11-126-Finsemble componentType=FinsembleJar uuid=uuid1545252286933_4444 left=316 top=89 width=800 height=600 iac=true serverAddress=ws://127.0.0.1:3376
```

## Testing application from command line

This can be run with the following command from the project root:
``` BASH
./target/FinsembleJavaExample.exe
```

## Configuration for testing 
Copy the _config.json_ included in the project to _src/components/java-example/java-example.json_. Update the application manifest to include:
``` JSON
    "finsemble": {
        "applicationRoot": "http://localhost:3375",
        "moduleRoot": "http://localhost:3375/finsemble",
        "servicesRoot": "http://localhost:3375/finsemble/services",
		"notificationURL": "http://localhost:3375/components/notification/notification.html",
		"avaExampleRoot": "<path to finsemble-java-example root>/target",
        "importConfig": [
			"$applicationRoot/configs/application/config.json",
            "$applicationRoot/components/native/java-example.json"
        ],
        "IAC": {
            "serverAddress" : "ws://127.0.0.1:3376"
        }
    }
```

## Configuration for sample testing

Copy the _FinsembleJavaExample.zip_ from _target_ to the _hosted_ folder in the seed project. Set up the configuration as described above, but edit _java-example.json_ to use an alias instead of a path:

``` json
                "windowType": "FinsembleNativeWindow",
                "//path": "$javaExampleRoot/FinsembleJavaExample.exe",
                "alias": "finsembleJavaExample",
                "url": "",
```

Add the Finsemble Java Example to `appAssets` in _configs/openfin/manifest-local.json_.

``` json
/* ... */
    "appAssets": [
        {
            "src": "https://assets.finsemble.com/assimilation/assimilation_3.10.0.1.zip",
            "version": "3.10.0",
            "alias": "assimilation",
            "target": "AssimilationMain.exe"
        },
        {
            "src": "http://localhost:3375/hosted/FinsembleJavaExample.zip",
            "version": "3.10.0",
            "alias": "finsembleJavaExample",
            "target": "FinsembleJavaExample.exe"
        }
    ],
/* ... */
```


## Enable logging
This project includes an example _logging.properties_ file to enable logging for the project. To use this file, add the following argument to the command line to launch the example:
```
-Djava.util.logging.config.file=/path/to/logging.properties
```

**NOTE:** You may want to update `java.util.logging.FileHandler.pattern` to point to a different location. The default is in the user's home directory.
