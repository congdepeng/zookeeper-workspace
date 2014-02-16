package com.depeng.feature;

import com.depeng.feature.zk.ZkClientUtil;

import java.io.*;
import java.net.Socket;

/**
 * Created by depeng on 16/2/14.
 */
public class Client {
    public static void main(String[] args) throws Exception {

        String masterInfo = ZkClientUtil.getMasterInfo();
        System.out.println(masterInfo);

        String featureJobInfo = callMaster(masterInfo);

        String result = callFeatureJob(featureJobInfo);
        System.out.println(result);

    }

    private static String callMaster(String masterInfo) {
        String featureJobInfo = null;
        try {
            String[] hostPort = masterInfo.split(":");

            Socket socket = new Socket(hostPort[0], Integer.getInteger(hostPort[1]));
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = "request feature job";
            pw.write(info);
            pw.flush();
            socket.shutdownOutput();

            while (!((featureJobInfo = br.readLine()) == null)) {
                System.out.println("get feature job info from Master：" + featureJobInfo);
            }
            br.close();
            is.close();
            pw.close();
            os.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return featureJobInfo;

    }

    private static String callFeatureJob(String featureJobInfo) {
        String result = null;
        try {
            String[] hostPort = featureJobInfo.split(":");

            Socket socket = new Socket(hostPort[0], Integer.getInteger(hostPort[1]));
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = "request to extract feature";
            pw.write(info);
            pw.flush();
            socket.shutdownOutput();
            while (!((result = br.readLine()) == null)) {
                System.out.println("get  extract feature result from a feature job：" + result);
            }
            br.close();
            is.close();
            pw.close();
            os.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
