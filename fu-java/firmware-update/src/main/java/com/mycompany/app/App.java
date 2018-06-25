package com.mycompany.app;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static final String iotHubConnectionString = "{your iot hub connection string here}";
    public static final String deviceId = "{your device id here}";

    private static final String methodName = "firmwareUpdate";
    private static final Long responseTimeout = TimeUnit.SECONDS.toSeconds(30);
    private static final Long connectTimeout = TimeUnit.SECONDS.toSeconds(5);

    public static void ShowReportedProperties() 
    {
        try 
        {
            DeviceTwin deviceTwins = DeviceTwin.createFromConnectionString(iotHubConnectionString);
            DeviceTwinDevice twinDevice = new DeviceTwinDevice(deviceId);

            Boolean firmwareUpdated = false;
            Integer timeoutCycle = 0;

            while (!firmwareUpdated)
            {
                if (timeoutCycle > 5)
                {
                    System.out.println("Operation timed out");
                    break;
                }

                Thread.sleep(1000);

                deviceTwins.getTwin(twinDevice);

                String reportedProperties = twinDevice.reportedPropertiesToString();

                if(reportedProperties.contains("status=waiting"))
                {
                    System.out.println("Waiting on device...");
                }
                else if(reportedProperties.contains("status=downloadComplete"))
                {
                    System.out.println("Download complete, applying firmware...");
                }
                else if (reportedProperties.contains("status=applyComplete"))
                {
                    System.out.println("Firmware applied");
                    System.out.println("Get reported properties from device twin");
                    System.out.println(twinDevice.reportedPropertiesToString());
                    firmwareUpdated = true;
                }
                else
                {
                    timeoutCycle++;
                }
            }
        } catch (Exception ex) {
                System.out.println("Exception reading reported properties: " + ex.getMessage());
        }
    }

    public static void main( String[] args ) throws IOException
    {
        DeviceMethod methodClient = DeviceMethod.createFromConnectionString(iotHubConnectionString);

        try
        {
            String payload = "https://someurl";

            System.out.println("Invoked firmware update on device.");
            System.out.println("Sent URL: " + payload);

            MethodResult result = methodClient.invoke(deviceId, methodName, responseTimeout, connectTimeout, payload);

            if(result == null)
            {
                throw new IOException("Invoke direct method reboot returns null");
            }

            System.out.println("Status for device:   " + result.getStatus());
            System.out.println("Message from device: " + result.getPayload());
        }
        catch (IotHubException e)
        {
            System.out.println(e.getMessage());
        }
       
        ShowReportedProperties();

        System.out.println("Press ENTER to exit.");
        System.in.read();
        System.out.println("Shutting down sample...");
    }
}
