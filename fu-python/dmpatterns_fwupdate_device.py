import time, datetime
import sys
import threading

import iothub_client
from iothub_client import IoTHubClient, IoTHubClientError, IoTHubTransportProvider, IoTHubClientResult
from iothub_client import IoTHubError, DeviceMethodReturnValue

SEND_REPORTED_STATE_CONTEXT = 0
METHOD_CONTEXT = 0

MESSAGE_COUNT = 10

PROTOCOL = IoTHubTransportProvider.MQTT
CONNECTION_STRING = "{your device connection string here}"
CLIENT = IoTHubClient(CONNECTION_STRING, PROTOCOL)

def send_reported_state_callback(status_code, user_context):
    print ( "Reported state updated." )

def device_method_callback(method_name, payload, user_context):
    if method_name == "firmwareUpdate":
        print ( "Starting firmware update." )
        image_url = payload
        thr = threading.Thread(target=simulate_download_image, args=([payload]), kwargs={})
        thr.start()

        device_method_return_value = DeviceMethodReturnValue()
        device_method_return_value.response = "{ \"Response\": \"Firmware update started\" }"
        device_method_return_value.status = 200
        return device_method_return_value

def simulate_download_image(image_url):
    time.sleep(15)
    print ( "Downloading image from: " + image_url )

    reported_state = "{\"firmwareStatus\":\"downloading\", \"downloadComplete\":\"" + str(datetime.datetime.now()) + "\"}"
    CLIENT.send_reported_state(reported_state, len(reported_state), send_reported_state_callback, SEND_REPORTED_STATE_CONTEXT)
    time.sleep(15)

    simulate_apply_image(image_url)

def simulate_apply_image(image_url):
    print ( "Applying image from: " + image_url )

    reported_state = "{\"firmwareStatus\":\"applying\", \"startedApplyingImage\":\"" + str(datetime.datetime.now()) + "\"}"
    CLIENT.send_reported_state(reported_state, len(reported_state), send_reported_state_callback, SEND_REPORTED_STATE_CONTEXT)
    time.sleep(15)

    simulate_complete_image()

def simulate_complete_image():
    print ( "Image applied." )

    reported_state = "{\"firmwareStatus\":\"completed\", \"lastFirmwareUpdate\":\"" + str(datetime.datetime.now()) + "\"}"
    CLIENT.send_reported_state(reported_state, len(reported_state), send_reported_state_callback, SEND_REPORTED_STATE_CONTEXT)

def iothub_firmware_sample_run():
    try:
        CLIENT.set_device_method_callback(device_method_callback, METHOD_CONTEXT)

        reported_state = "{\"firmwareStatus\":\"standBy\", \"logTime\":\"" + str(datetime.datetime.now()) + "\"}"
        CLIENT.send_reported_state(reported_state, len(reported_state), send_reported_state_callback, SEND_REPORTED_STATE_CONTEXT)
        print ( "Device twins initialized." )
        print ( "IoTHubClient waiting for commands, press Ctrl-C to exit" )

        while True:
            status_counter = 0
            while status_counter <= MESSAGE_COUNT:
                time.sleep(10)
                status_counter += 1

    except IoTHubError as iothub_error:
        print ( "Unexpected error %s from IoTHub" % iothub_error )
        return
    except KeyboardInterrupt:
        print ( "IoTHubClient sample stopped" )

if __name__ == '__main__':
    print ( "Starting the IoT Hub Python firmware update sample..." )
    print ( "    Protocol %s" % PROTOCOL )
    print ( "    Connection string=%s" % CONNECTION_STRING )

    iothub_firmware_sample_run()