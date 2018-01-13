import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.Date;

public class Server
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
      
      ServerSocket serverSocket = new ServerSocket(port);//�}�l��ťport�s�u�ШD�C
      
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
}