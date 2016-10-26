package com.telenav.sdk_sample.car.data;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

//import org.json.simple.parser.ParseException;

/**
 * Created by powen on 8/10/16.
 */
public class SocketClient
{
    protected static final int CONNECT_TIME_OUT = 15 * 1000;
    public String host = "192.168.1.101";
    public int port = 45002;
    OutputStream os;
    InputStream is;
    Socket socket;
    int mode;
    DataParser dp = new DataParser();

    public SocketClient(String h, int p, int m)
    {
        host = h;
        port = p;
        mode = m;
        Log.d("Client","onCreate()");
        Runnable conn = new Connect();
        new Thread(conn).start();
    }

    protected Socket openConnection()
    {

        socket = new Socket();
        SocketAddress address = new InetSocketAddress(host, port);

        try
        {
            socket.connect(address, CONNECT_TIME_OUT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.d("Client","connected on port "+socket.getLocalPort());
        return socket;
    }

    public void closeConnection()
    {
        try
        {
            if (socket != null)
            {
                socket.close();
                socket = null;
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        closeInputOutput();
    }

    protected void closeInputOutput()
    {
        try
        {
            if (os != null) os.close();
            if (is != null) is.close();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

    }

    class Connect implements Runnable{
        @Override
        public void run()
        {

            while (true)
            {
                try
                {
                    socket = openConnection();
                    os = socket.getOutputStream();
                    is = socket.getInputStream();
                    receiveMessage();
                    if(socket!=null&&socket.isConnected())
                        closeConnection();
                }
                catch (Throwable e)
                {
                    if(socket!=null&&socket.isConnected())
                        closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }


    private void receiveMessage()
    {
        if(socket==null||!socket.isConnected())
        {
            Log.d("Client","Socket port = "+port+" is not connected");
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str;
            while (socket != null && is != null&&(str = br.readLine())!=null)
            {

                if (str.length() > 0)
                {
                    processMessage(str);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message)
    {
        if(socket==null||!socket.isConnected()||socket.isClosed())
        {
            Log.d("Client","Socket port "+port+" not connected");
        }
        try
        {
            os = socket.getOutputStream();
            if (message != null&&!message.isEmpty())
            {
                if(message.charAt(message.length()-1)!= '\n')
                    os.write((message+"\n").getBytes());
                else
                    os.write((message).getBytes());

                Log.d("Client","SocketSender sending "+message+" at port = "+socket.getPort()+", "+socket.getLocalPort());
            }
            else
            {
                Log.d("Client","SocketSender has no data");
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private void processMessage(String str) throws JSONException {

        /*
        0 = perception
        1 = motion
        2 = Control
        3 = map
         */
        if (mode == 0)
            dp.parseJsonRequestString(str);
        if (mode == 1)
            dp.parseJsonMotionRequestString(str);
        if (mode == 2)
            dp.parseControlString(str);
        if (mode == 3)
        {
            try
            {
                //dp.parseAutopilotData(str);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        Log.d("Client","get message length = "+str.length()+" "+str);
        return;
    }

}