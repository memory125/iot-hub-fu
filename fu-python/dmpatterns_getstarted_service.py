import sys
import time

import iothub_service_client
from iothub_service_client import IoTHubDeviceTwin, IoTHubDeviceMethod, IoTHubError

CONNECTION_STRING = "{your iot hub connection string here}"
DEVICE_ID = "{your device id here}"

METHOD_NAME = "firmwareUpdate"
METHOD_PAYLOAD = "{\"fwPackageUri\":\"test.com\"}"
TIMEOUT = 60
MESSAGE_COUNT = 5

def iothub_firmware_sample_run():
    try:
        iothub_twin_method = IoTHubDeviceTwin(CONNECTION_STRING)

        print ( "" )
        print ( "Direct Method called." )
        iothub_device_method = IoTHubDeviceMethod(CONNECTION_STRING)

        response = iothub_device_method.invoke(DEVICE_ID, METHOD_NAME, METHOD_PAYLOAD, TIMEOUT)
        print ( response.payload )

        print ( "" )
        print ( "Device Twin queried, press Ctrl-C to exit" )
        while True:
            twin_info = iothub_twin_method.get_twin(DEVICE_ID)

            if "\"firmwareStatus\":\"standBy\"" in twin_info:
                print ( "Waiting on device..." )
            elif "\"firmwareStatus\":\"downloading\"" in twin_info:
                print ( "Downloading firmware..." )
            elif "\"firmwareStatus\":\"applying\"" in twin_info:
                print ( "Download complete, applying firmware..." )
            elif "\"firmwareStatus\":\"completed\"" in twin_info:
                print ( "Firmware applied" )
                print ( "" )
                print ( "Get reported properties from device twin:" )
                print ( twin_info )
                break
            else:
                print ( "Unknown status" )

            status_counter = 0
            while status_counter <= MESSAGE_COUNT:
                time.sleep(1)
                status_counter += 1

    except IoTHubError as iothub_error:
        print ( "" )
        print ( "Unexpected error {0}" % iothub_error )
        return
    except KeyboardInterrupt:
        print ( "" )
        print ( "IoTHubService sample stopped" )

if __name__ == '__main__':
    print ( "Starting the IoT Hub firmware update Python sample..." )
    print ( "    Connection string = {0}".format(CONNECTION_STRING) )
    print ( "    Device ID         = {0}".format(DEVICE_ID) )

    iothub_firmware_sample_run()