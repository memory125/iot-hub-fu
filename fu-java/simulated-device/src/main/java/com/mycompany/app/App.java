package com.mycompany.app;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_NOT_DEFINED = 404;

    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private static String connString = "{your device connection string here}";
    private static DeviceClient client;

    private static String downloadURL = "unknown";
    
    protected static class DirectMethodStatusCallback implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context)
        {
            System.out.println("IoT Hub responded to device method operation with status " + status.name());
        }
    }

    protected static class DirectMethodCallback implements com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback
    {
        @Override
        public DeviceMethodData call(String methodName, Object methodData, Object context)
        {
            DeviceMethodData deviceMethodData;
            switch (methodName)
            {
                case "firmwareUpdate" :
                {
                    System.out.println("Response to method '" + methodName + "' sent successfully");

                    downloadURL = new String((byte[])methodData);

                    int status = METHOD_SUCCESS;
                    System.out.println("Starting firmware update");
                    deviceMethodData = new DeviceMethodData(status, "Started firmware update");
                    FirmwareUpdateThread firmwareUpdateThread = new FirmwareUpdateThread();
                    Thread t = new Thread(firmwareUpdateThread);
                    t.start();
                    break;
                }
                default:
                {
                    int status = METHOD_NOT_DEFINED;
                    deviceMethodData = new DeviceMethodData(status, "Not defined direct method " + methodName);
                }
            }
            return deviceMethodData;
        }
    }

    protected static class DeviceTwinStatusCallback implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context)
        {
            System.out.println("IoT Hub responded to device twin operation with status " + status.name());
        }
    }
    
    protected static class PropertyCallback implements PropertyCallBack<String, String>
    {
        public void PropertyCall(String propertyKey, String propertyValue, Object context)
        {
            System.out.println("PropertyKey:     " + propertyKey);
            System.out.println("PropertyKvalue:  " + propertyKey);
        }
    }

    protected static class FirmwareUpdateThread implements Runnable {
        public void run() {
          try {      
            HashMap initialUpdate = new HashMap();
            Property sentProperty = new Property("firmwareUpdate", initialUpdate);
            Set<Property> sentPackage = new HashSet<Property>();
      
            System.out.println("Downloading from " + downloadURL);
      
            initialUpdate.put("status", "waiting");
            initialUpdate.put("fwPackageUri", downloadURL);
            initialUpdate.put("startedWaitingTime", LocalDateTime.now().toString());   
            sentPackage.add(sentProperty);
      
            client.sendReportedProperties(sentPackage);
      
            Thread.sleep(5000);
      
            System.out.println("Download complete");
      
            HashMap downloadUpdate = new HashMap();
            downloadUpdate.put("status","downloadComplete");
            downloadUpdate.put("downloadCompleteTime", LocalDateTime.now().toString());
            downloadUpdate.put("startedApplyingImage", LocalDateTime.now().toString());
            sentProperty.setValue(downloadUpdate);
      
            client.sendReportedProperties(sentPackage);
      
            Thread.sleep(5000);
      
            System.out.println("Apply complete");
      
            HashMap applyUpdate = new HashMap();
            applyUpdate.put("status","applyComplete");
            applyUpdate.put("lastFirmwareUpdate", LocalDateTime.now().toString());
            sentProperty.setValue(applyUpdate);
      
            client.sendReportedProperties(sentPackage);
      
            Thread.sleep(5000);
      
            HashMap resetUpdate = new HashMap();
            applyUpdate.put("status","reset");
            sentProperty.setValue(resetUpdate);
      
            client.sendReportedProperties(sentPackage);
          }
          catch (Exception ex) {
            System.out.println("Exception in reboot thread: " + ex.getMessage());
          }
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        client = new DeviceClient(connString, protocol);

        try
        {
          client.open();
          client.subscribeToDeviceMethod(new DirectMethodCallback(), null, new DirectMethodStatusCallback(), null);
          client.startDeviceTwin(new DeviceTwinStatusCallback(), null, new PropertyCallback(), null);
          System.out.println("Client connected to IoT Hub.  Waiting for firmwareUpdate direct method.");
        }
        catch (Exception e)
        {
          System.out.println("On exception, shutting down \n" + " Cause: " + e.getCause() + " \n" +  e.getMessage());
          client.close();
          System.out.println("Shutting down...");
        }
        
        System.out.println("Press any key to exit...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        client.close();
        System.out.println("Shutting down...");
    }
}
