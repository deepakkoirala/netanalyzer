package com.thapasujan5.netanalzyerpro.Tools;

import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Suzan on 12/17/2015.
 */
public class SubnetMask {
    public static String getSubnetMask() {
        String subnet = "";
        // This works both in tethering and when connected to an Access Point
        try {


            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback())
                    continue; // Don't want to broadcast to the loopback interface

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();

                    // InetAddress ip = interfaceAddress.getAddress();
                    //interfaceAddress.getNetworkPrefixLength();
                    //is another way to express subnet mask

//                    InetAddress bc = interfaceAddress.getBroadcast();
//                    subnet = bc.getHostName();


                    // Android seems smart enough to set to null broadcast to
                    //  the external mobile network. It makes sense since Android
                    //  silently drop UDP broadcasts involving external mobile network.
                    if (broadcast == null)
                        continue;

                    // Use the broadcast
                    subnet = broadcast.getHostAddress();
                    Log.d("subnet", broadcast.getHostAddress());
                }
                Log.d("subnet", subnet+" before successful return");
                return subnet;
            }
        } catch (Exception e) {
            Log.d("SubnetMask", "SubnetError");
            e.printStackTrace();
        }
        return subnet;
    }
}
