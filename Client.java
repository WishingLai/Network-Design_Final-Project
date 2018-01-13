import java.io.*;
import java.net.*;
import java.lang.*;

public class Client
{
  public static void main(String[] args)
  {
    String message, stdin , ip;// message讀入Server端輸出 , stdin 寫出 Client 端輸出
    int port;
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("請輸入預連接IP : ");
      ip = br.readLine();//讀入使用者輸入的ip
      System.out.print("請輸入要連接port : ");
      port = Integer.parseInt(br.readLine());//讀入使用者輸入的port
      Socket con = new Socket(ip,port);//建立連線
      if(con.isConnected())
      {
        System.out.println("Connect Success");
        System.out.println("輸入姓名");
        String name = br.readLine();
        //
        Thread thread = new Thread(new ClientTalkc(con));//建立多執行緒(錯在這)
        
        thread.start(); //啟動
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());//建立DataOutputStream將資料寫至Server
        while(true)
        {          
          //System.out.print("請輸入 ： ");
          stdin = br.readLine();
          dos.writeUTF(name + " : " + stdin);//將使用者輸入內容傳送至Server
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

class ClientTalkc implements Runnable //毒入其他Client端輸入的聊天內容
{
  DataInputStream dis;//建立DataInput 讀取使用者傳送內容
  Socket clientSocket;
  public ClientTalkc(Socket clientSocket) 
  {
    this.clientSocket = clientSocket;    
  }
  public void run() 
  {
    try 
    {
      dis = new DataInputStream(clientSocket.getInputStream());//宣告一個將server端資料寫出的變數
      while (true) 
      {        
        String read = dis.readUTF();//讀取Server端傳送過來的資訊
        System.out.println(read);
	System.out.print("請輸入 ： ");
      }
    }
    catch(IOException e) 
    {
      //System.out.println(e.toString());
    }
  }
}