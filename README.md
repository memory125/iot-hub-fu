# iot-hub-fu
Azure IoT Hub firmware update.

## fu-java
>* Open cmd (Windows) or terminal (Linux & macOS) to create the project via maven. Please refer to the below command.
    <br/> For firmware update project.
    <br/> `mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=firmware-update -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`
    <br/> For simulated device project.
    <br/> `mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=simulated-device -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`
>* Sign in [Azure Portal](https://portal.azure.com) using your Azure account.
>* Create IoT Hub service. 
>* Add IoT device based on the IoT Hub created above.
>* Replace the connection string in App.java separately for simulated-device and firmware-update.
    <br/> `Note`
    <br/> `iotHubConnectionString is iot hub connection string. And deviceId is your device id.`
    <br/> `connString is your device connection string.`
>* Compile the project via below command.
    <br/> `mvn clean package -DskipTests`
>* Run this project via below command.
    <br/> `mvn exec:java -Dexec.mainClass="com.mycompany.app.App"`

## fu-node
>* Open cmd or powershell (Windows) or terminal (Linux & macOS) to create the project via npm. Please refer to the below command.
    <br/> `npm init`
    <br/> For manageddevice, install the relevant libs via below command.
    <br/> `npm install azure-iot-device azure-iot-device-mqtt --save`
    <br/> For triggerfwupdateondevice, install the relevant libs via below command.
    <br/> `npm install azure-iothub --save`
>* Sign in [Azure Portal](https://portal.azure.com) using your Azure account.
>* Create IoT Hub service. 
>* Add IoT device based on the IoT Hub created above.
>* Replace the connection string separately for manageddevice and triggerfwupdateondevice.
    <br/> `Note`
    <br/> `connectionString in service.js is iot hub connection string. And deviceToUpdate is your device id for firmware update.`
    <br/> `ConnectionString in device.js is your device connection string.`
>* Compile & run the project via below command.
    <br/> `node dmpatterns_fwupdate_device.js & node dmpatterns_fwupdate_service.js`

## fu-python
>* Open cmd or powershell (Windows) or terminal (Linux & macOS) to create the file dmpatterns_fwupdate_device.py & dmpatterns_getstarted_service.py.    
>* Please intall the relevant libs first via below commands.
    <br/> `pip install azure-iothub-service-client & pip install azure-iothub-device-client`
>* Sign in [Azure Portal](https://portal.azure.com) using your Azure account.
>* Create IoT Hub service. 
>* Add IoT device based on the IoT Hub created above.
>* Replace the connection string in above files.
    <br/> `Note`
    <br/> `CONNECTION_STRING in dmpatterns_fwupdate_device.py is device connection string.`
    <br/> `CONNECTION_STRING in dmpatterns_getstarted_service.py is your iot hub connection string. And DEVICE_ID is your device id.`
>* Compile & run the project via below command.
    <br/> `python dmpatterns_getstarted_device.py & python dmpatterns_getstarted_service.py`