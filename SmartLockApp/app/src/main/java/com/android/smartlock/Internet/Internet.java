package com.android.smartlock.Internet;

import android.util.Log;
import com.android.smartlock.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Internet {

    int RESPONSE_LEN = 2048;
    private String result;

    protected Internet(String... params) {
        String get = "";
        for (int i = 0; i < params.length; i += 2) {
            get += params[i] + "=" + params[i + 1] + "\n";
        }
        tcpSend(get);
    }

    private void tcpSend(String content) {
        final String header = "GET /smartlock-user-agent/ HTTP/1.1\n" +
                "Host: smartlock\n" +
                "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                "Content-Length: " + content.length() + "\n" +
                "Accept-Language: en-us,en;q=0.5\n\n";
        String msg = header + content + '\n';

        Log.d("TCP", Constants.getIPAdress() + " " + Constants.getPort() + " " + Constants.getTimeout() + " " + content);

        char[] buff = new char[RESPONSE_LEN];
        String modifiedSentence;
        Socket clientSocket;
        try {
            clientSocket = new Socket(Constants.getIPAdress(), Constants.getPort());
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes(msg);
            clientSocket.setSoTimeout(Constants.getTimeout());
            inFromServer.read(buff, 0, RESPONSE_LEN);
            modifiedSentence = String.valueOf(buff).trim();
            clientSocket.close();
            outToServer.close();
            inFromServer.close();
        } catch (Exception exc) {
            modifiedSentence = "-1";
        }
        this.result = modifiedSentence;
    }

    public String getResult() {
        return result;
    }
}
