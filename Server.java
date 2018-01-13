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
      System.out.print("請輸入port : ");
      port = Integer.parseInt(in.readLine());
      System.out.println("Wating for connection");
      
      ServerSocket serverSocket = new ServerSocket(port);//開始監聽port連線請求。
      
      while (true) 
      {
        Socket clientSocket = serverSocket.accept();
        if(clientSocket.isConnected())
        {
          try
          {
            count ++ ;
            System.out.println("目前有" + count + "位Client");
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            clientOutputStreams.add(dos);
            dos.writeUTF("您是第" + count + "位Client");
            //
            Thread thread = new Thread(new ClientTalk(clientSocket , clientOutputStreams));//建立一個多執行緒(錯在這)
            thread.start(); //啟動該多執行緒
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
  DataInputStream dis;//宣告一個讀取Client傳送過來的字串物件
  
  ArrayList<DataOutputStream> clientOutputStreams = new ArrayList<DataOutputStream>();
  protected int count ;
  protected Socket clientSocket;
  public ClientTalk(Socket clientSocket , ArrayList<DataOutputStream> clientOutputStreams )
  {
    this.clientOutputStreams = clientOutputStreams;
    this.clientSocket = clientSocket;
    try
    {
      dis = new DataInputStream(clientSocket.getInputStream());//宣告一個將server端資料寫出的變數
    }
    catch(Exception e)
    {
      //System.out.println(e.toString());
	System.out.println("CCC");
    }    
  }
  public void run() //多執行緒，run
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
  public void broadCast(String message)//廣播功能，將Client毒入的資料傳送給所有Client端
    {
        DataOutputStream writer = null;
        Iterator<DataOutputStream> it = clientOutputStreams.iterator();
        while( it.hasNext() )
        {
            try
            {
                writer = it.next();      
                writer.writeUTF(message);//將資料寫出
                writer.flush();//清空資料串流。
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