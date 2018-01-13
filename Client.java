import java.io.*;
import java.net.*;
import java.lang.*;

public class Client
{
  public static void main(String[] args)
  {
    String message, stdin , ip;// messageŪ�JServer�ݿ�X , stdin �g�X Client �ݿ�X
    int port;
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("�п�J�w�s��IP : ");
      ip = br.readLine();//Ū�J�ϥΪ̿�J��ip
      System.out.print("�п�J�n�s��port : ");
      port = Integer.parseInt(br.readLine());//Ū�J�ϥΪ̿�J��port
      Socket con = new Socket(ip,port);//�إ߳s�u
      if(con.isConnected())
      {
        System.out.println("Connect Success");
        System.out.println("��J�m�W");
        String name = br.readLine();
        //
        Thread thread = new Thread(new ClientTalkc(con));//�إߦh�����(���b�o)
        
        thread.start(); //�Ұ�
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());//�إ�DataOutputStream�N��Ƽg��Server
        while(true)
        {          
          //System.out.print("�п�J �G ");
          stdin = br.readLine();
          dos.writeUTF(name + " : " + stdin);//�N�ϥΪ̿�J���e�ǰe��Server
        }      
      }
      else
        System.out.println("Connect fails");
    }
    catch(Exception e)
    {
      System.out.println(e.toString());
    }
  }//end of main
}//end of class Client

class ClientTalkc implements Runnable //�r�J��LClient�ݿ�J����Ѥ��e
{
  DataInputStream dis;//�إ�DataInput Ū���ϥΪ̶ǰe���e
  Socket clientSocket;
  public ClientTalkc(Socket clientSocket) 
  {
    this.clientSocket = clientSocket;    
  }
  public void run() 
  {
    try 
    {
      dis = new DataInputStream(clientSocket.getInputStream());//�ŧi�@�ӱNserver�ݸ�Ƽg�X���ܼ�
      while (true) 
      {        
        String read = dis.readUTF();//Ū��Server�ݶǰe�L�Ӫ���T
        System.out.println(read);
	System.out.print("�п�J �G ");
      }
    }
    catch(IOException e) 
    {
      //System.out.println(e.toString());
    }
  }
}