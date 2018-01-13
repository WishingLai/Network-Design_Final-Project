import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.Date;

public class test
{
	
  public static void main(String[] args)
  {
    int count = 0;
    ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
    try
    {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      int port;
      System.out.print("�п�Jport : ");
      port = Integer.parseInt(in.readLine());
      System.out.println("Wating for connection");
      
      ServerSocket serverSocket = new ServerSocket(port);//�}�l��ťport�s�u�ШD�C�}PS

try (ServerSocket server = new ServerSocket(port)) {
     while (true) {  
       try {
         Socket connection = server.accept();
         Thread task = new ProxyThread(connection);//
         task.start();
       } catch (IOException ex) {}
     } 
    } catch (IOException ex) {
      System.err.println("Couldn't start server");
    }
      

      while (true) 
      {
        Socket clientSocket = serverSocket.accept();
        if(clientSocket.isConnected())
        {
          try
          {
            count ++ ;
            System.out.println("�ثe��" + count + "��Client");
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            clientOutputStreams.add(dos);
            dos.writeUTF("�z�O��" + count + "��Client");
            //
            Thread thread = new Thread(new ClientTalk(clientSocket , clientOutputStreams));//�إߤ@�Ӧh�����(���b�o)
            thread.start(); //�ҰʸӦh�����
          }
          catch (Exception e) 
          {
            count --;
            System.out.println(e.toString() + "count = " + count);
          }
        }
      }
    }
    catch (Exception e) 
    {
      //System.out.println(e.toString());
	System.out.println("DDD");
    }
    
 
  }

 
class ClientTalk implements Runnable 
{
  DataInputStream dis;//�ŧi�@��Ū��Client�ǰe�L�Ӫ��r�ꪫ��
  
  ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
  protected int count ;
  protected Socket clientSocket;
  public ClientTalk(Socket clientSocket , ArrayList<DataOutputStream> clientOutputStreams )
  {
    this.clientOutputStreams = clientOutputStreams;
    this.clientSocket = clientSocket;
    try
    {
      dis = new DataInputStream(clientSocket.getInputStream());//�ŧi�@�ӱNserver�ݸ�Ƽg�X���ܼ�
    }
    catch(Exception e)
    {
      //System.out.println(e.toString());
	System.out.println("CCC");
    }    
  }
  public void run() //�h������Arun
  {
    try 
    {
      while (true) 
      {        
        String read = dis.readUTF();
        System.out.println(read);
        broadCast(read);
      }
    }
    catch(IOException e) 
    {
      //System.out.println(e.toString());
	count--;
	System.out.println("AAA" + count + "AAA");
    }
  }
  public void broadCast(String message)//�s���\��A�NClient�r�J����ƶǰe���Ҧ�Client��
    {
        DataOutputStream writer = null;
        Iterator<DataOutputStream> it = clientOutputStreams.iterator();
        while( it.hasNext() )
        {
            try
            {
                writer = it.next();      
                writer.writeUTF(message);//�N��Ƽg�X
                writer.flush();//�M�Ÿ�Ʀ�y�C
            }
            catch (Exception e)
            {
                //System.out.println(e.toString());
		//System.out.println("BBB");
                clientOutputStreams.remove(writer);
            }
        }
    }

private class ProxyThread extends Thread {
    
    private Socket connection;
    
    ProxyThread(Socket connection) {
      this.connection = connection;
    }
    
	public synchronized String readLine(InputStream in) throws IOException {
        StringBuffer sb = new StringBuffer(80); // Default size
        int c;

        while((c = in.read()) != '\n') {
           if(c != '\r') sb.append((char) c);
        }
        return sb.toString();
    }
	
	public static final int TIMEOUT = 45000;
    public void CopyTo(InputStream in, OutputStream out) {
      int b;

      try {	// Lab2:p4file2scr()
        while((b = in.read()) >= 0) {
  	out.write(b);
        }
      } catch(Exception ex) {
        System.err.println(ex.getMessage());
      }
    }


	public void CopyLine(InputStream in, OutputStream out) {
      try {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        for (String line; (line = reader.readLine()) != null;) {
	  writer.write(line + "\r\n");
          writer.flush();
        }
      } catch(Exception ex) {
        System.err.println(ex.getMessage());
        return;
      }
    }

public class RemoteThread extends Thread {
      private Socket socket = null;
      private InputStream childIn;
      private OutputStream childOut;

      public RemoteThread(InputStream in, OutputStream out) {
        childIn = in;
        childOut = out;
      }

      @Override
      public void run() {
        CopyTo(childIn, childOut);
      }
    }

	public void printConnections(Socket local, Socket remote) {
      String    p =     local.getInetAddress().getHostAddress()
                + ":" + local.getPort()
                + "-" + local.getLocalAddress().getHostAddress()
                + ":" + local.getLocalPort()
                + "-" + remote.getLocalAddress().getHostAddress()
                + ":" + remote.getLocalPort()
                + "-" + remote.getInetAddress().getHostAddress()
                + ":" + remote.getPort();
      System.out.println(p);
    }


    @Override
    public void run() {

	try {
		InputStream in = connection.getInputStream();
		OutputStream out = connection.getOutputStream();
		String line = readLine(in);
		String[] pieces = line.split(" ");
		String remoteHost = pieces[0];
		int remotePort;
		

		if((remotePort = Integer.parseInt(pieces[1])) <= 0) remotePort = -1;
/*
		String s = "Connecting to :" + remoteSite + " : " + remotePort + "\n";
		out.write(s.getBytes());
		System.out.print("remoteHost = " + remoteSite + "\n");
		System.out.print("remotePort = " + remotePort + "\n");
		out.flush();
		
*/

	Socket socket = null;
        try {
          socket = new Socket(remoteHost, remotePort);
          socket.setSoTimeout(TIMEOUT);
	  printConnections(connection, socket);
          OutputStream out2 = socket.getOutputStream();
          InputStream in2 = socket.getInputStream();
          // Create Thread
          Thread t = new RemoteThread(in, out2);
          t.start();
          CopyTo(in2, out);
        } catch (IOException ex) {
          System.err.println(ex);
        } finally {
          try {
	    socket.close();
          } catch (IOException ex) {
            // ignore
          }
          // System.exit(0);
	}
      }catch (IOException ex) {
        System.err.println(ex);
      } finally {
        try {
          connection.close();
        } catch (IOException e) {
          // ignore;
        }
      }
    }
  }
}
}