# Java native interface for Finsemble

## Testing in the IDE

The finsemble-jar project must be built before it can be run. 

**NOTE:** The project was created using (IntelliJ IDEA)[https://www.jetbrains.com/idea/download], but can be built with other IDEs with some conversion.

Pass the following parameters:
```
foo bar baz finsembleWindowName=FinsembleJar-11-126-Finsemble componentType=FinsembleJar uuid=uuid1545252286933_4444 left=316 top=89 width=800 height=600 openfinVersion=8.56.28.36 openfinSocketPort=9696
```

## Testing application from command line

This can be run with the following command from the project root:
``` BASH
java -jar finsemble/out/artifacts/finsemble_jar/finsemble.jar
```

## Configuration for testing 
Copy the _config.json_ included in the project to _configs/application/javaExample.json_. Update the application manifest to include:
``` JSON
    "finsemble": {
        "applicationRoot": "http://localhost:3375",
        "moduleRoot": "http://localhost:3375/finsemble",
        "servicesRoot": "http://localhost:3375/finsemble/services",
		"notificationURL": "http://localhost:3375/components/notification/notification.html",
		"finsembleJavaExampleRoot": "<Path to finsemble-jar root>/finsemble/out/artifacts/finsemble_jar_bundled",
        "importConfig": [
			"$applicationRoot/configs/application/config.json",
            "$applicationRoot/configs/application/java-example
            .json"
        ],
        "IAC": {
            "serverAddress" : "wss://127.0.0.1:3376"
        }
    }
```

## Enable logging
This project includes an example _logging.properties_ file to enable logging for the project. To use this file, add the following argument to the command line to launch the example:
```
-Djava.util.logging.config.file=/path/to/logging.properties
```

**NOTE:** You may want to update `java.util.logging.FileHandler.pattern` to point to a different location. The default is in the user's home directory.
