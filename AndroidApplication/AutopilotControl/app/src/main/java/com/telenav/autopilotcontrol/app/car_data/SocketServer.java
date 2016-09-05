package com.telenav.autopilotcontrol.app.car_data;

/**
 * Created by powen on 7/25/16.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by powen on 8/9/16.
 */
public class SocketServer
{
    private static final int BUF_SIZE = 64*1024;
    private static final char ACK_FLAG = (char)6;
    private static byte[] buf = new byte[BUF_SIZE];
    private static byte[] buf2 = new byte[BUF_SIZE];
    private static final int JOGL_PORT = 45001,JOGL_PORT1 = 45011, JOGL_PORT2 = 45021, JOGL_PORT3 = 45031,MAP_PORT = 45002,PERCEPTION_PORT = 45003, CONTROL_PORT = 45004, MOTION_PORT = 45005;
    private static final int JOGL_MODE = 1,JOGL_MODE1 = 11,JOGL_MODE2 = 21,JOGL_MODE3 = 31,MAP_MODE = 2, PERCEPTION_MODE = 3, CONTROL_MODE = 4, MOTION_MODE = 5;

    private enum Clients{
        JoglClient(JOGL_PORT,JOGL_MODE),
        MapClient(MAP_PORT,MAP_MODE),
        PerceptionClient(PERCEPTION_PORT,PERCEPTION_MODE),
        ControlClient(CONTROL_PORT,CONTROL_MODE),
        MotionClient(MOTION_PORT,MOTION_MODE),
        JoglClient1(JOGL_PORT1,JOGL_MODE1),
        JoglClient2(JOGL_PORT2,JOGL_MODE2),
        JoglClient3(JOGL_PORT3,JOGL_MODE3);

        Clients(int p, int m)
        {
            port = p;
            mode = m;
        }
        public Thread thr;
        public int mode;
        public int port;
        public Socket socket;
        public ServerSocket serverSocket;
        public InputStream is;
        public OutputStream os;
    };

    public void receiveMessage(Clients c)
    {
        if(c.socket==null||!c.socket.isConnected())
        {
            Log.d("Server","Socket not connected on port "+c.socket.getPort());
            return;
        }
        try
        {
            String str ;
            if(c.mode == JOGL_MODE)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.is));
                while(c.socket != null && c.is != null&&(str = br.readLine())!=null)
                {
                    Log.d("Server", "got message from tablet = "+str);
                    sendMessage(str, Clients.ControlClient);
                }
            }
            else if(c.mode == JOGL_MODE1)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.is));
                while(c.socket != null && c.is != null&&(br.readLine())!=null)
                {
                    //TODO - dummy - do not delete this
                }
            }
            else if(c.mode == JOGL_MODE2)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.is));
                while(c.socket != null && c.is != null&&( br.readLine())!=null)
                {
                    //TODO - dummy - do not delete this
                }
            }
            else if(c.mode == JOGL_MODE3)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.is));
                while(c.socket != null && c.is != null&&(br.readLine())!=null)
                {
                    //TODO - dummy - do not delete this
                }
            }
            else if(c.mode == PERCEPTION_MODE)
            {
                int numBytes = -1;
                while(c.socket != null && c.is != null&&(numBytes = c.is.read(buf,0,BUF_SIZE-1))!=-1)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ACK_FLAG);
                    sb.append(String.valueOf(numBytes));
                    sendMessage(sb.toString(),c); // ACK message
                    sendMessage(buf,numBytes, Clients.JoglClient);
                }
            }
            else if(c.mode == MOTION_MODE)
            {
                int numBytes = -1;
                while(c.socket != null && c.is != null&&(numBytes = c.is.read(buf2,0,BUF_SIZE-1))!=-1)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ACK_FLAG);
                    sb.append(String.valueOf(numBytes));
                    sendMessage(sb.toString(),c); // ACK message
                    sendMessage(buf2,numBytes, Clients.JoglClient1);
                }
            }
            else if(c.mode == CONTROL_MODE)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.is));
                while(c.socket != null && c.is != null&&(str = br.readLine())!=null)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ACK_FLAG);
                    sb.append(String.valueOf(str.length()+1));
                    sendMessage(sb.toString(),c); // ACK message
                    sendMessage(str, Clients.JoglClient2);
                }
            }
            else if(c.mode == MAP_MODE)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.is));
                while(c.socket != null && c.is != null&&(str = br.readLine())!=null)
                {
                    Log.d("Server", "got message from tablet = "+str);
                    sendMessage(str, Clients.JoglClient3);
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] message, int len,Clients c)
    {

        if(c.socket==null||!c.socket.isConnected()||c.socket.isClosed())
        {
            Log.d("Server","Socket port "+c.port+" not connected");
            return;
        }

        try {
            if (message != null)
            {
                if(len != -1)
                    c.os.write(message,0 ,len);
                else
                    c.os.write(message);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, Clients c)
    {
        if (message != null && !message.isEmpty())
        {
            if(message.charAt(message.length()-1)!= '\n')
                sendMessage((message+"\n").getBytes(), -1, c);
            else
                sendMessage((message).getBytes(), -1, c);
        }
        else
        {
            System.out.println("Server: SocketSender has no data");
        }
    }

    public SocketServer()
    {
        for(Clients c: Clients.values())
        {
            Runnable connection = new Connect(c);
            c.thr = new Thread(connection);
            c.thr.start();
        }
    }

    class Connect implements Runnable
    {
        private Clients client;

        public Connect(Clients c)
        {
            this.client = c;
        }

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    client.serverSocket = new ServerSocket(); // <-- create an unbound socket first
                    client.serverSocket.setReuseAddress(true);

                    client.serverSocket.bind(new InetSocketAddress(client.port));
                    client.socket = client.serverSocket.accept();
                    Log.d("Server","Listening for incoming client on port "+client.serverSocket.getLocalPort());
                    Log.d("Server","port "+client.serverSocket.getLocalPort()+" is connected with client "+client.socket.getPort());

                    client.is = client.socket.getInputStream();
                    client.os = client.socket.getOutputStream();
                    receiveMessage(client);
                    if(client!=null)
                        closeConnection(client);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    if(client!=null)
                        closeConnection(client);
                }
            }
        }
    }

    public void closeAll()
    {
        for(Clients c: Clients.values())
        {
            closeConnection(c);
        }
    }

    protected void closeConnection(Clients c)
    {
        try
        {
            if(c.serverSocket!=null && !c.serverSocket.isClosed())
            {
                c.serverSocket.close();
            }
            if (c.socket != null&& !c.socket.isClosed())
            {
                Log.d("Server","Socket port "+c.port+" closed");
                c.socket.close();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        closeInputOutput(c.os,c.is);

    }

    protected void closeInputOutput(OutputStream os, InputStream is)
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

}