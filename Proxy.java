import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Proxy {

  //private static final Logger logger = Logger.getLogger("localhost");
  public static final String SERVER = "localhost";
  public static final int PORT = 5537;
  public static final int TIMEOUT = 15000;

  private final int port;
  private final String newSite;
  
  public Proxy(String newSite, int port) {
    this.port = port;
    this.newSite = newSite;
  }

  public void start() {
    try (ServerSocket server = new ServerSocket(port)) {
      //logger.info("Redirecting connections on port " 
      //    + server.getLocalPort() + " to " + newSite);
        
      while (true) {
        try {
          Socket s = server.accept();
          Socket dic = new Socket( SERVER , PORT );
          Thread t = new RedirectThread(s,dic);
          t.start();
          
        } catch (IOException ex) {
          //logger.warning("Exception accepting connection");
        } catch (RuntimeException ex) {
          //logger.log(Level.SEVERE, "Unexpected error", ex);
        }
      } 
    } catch (BindException ex) {
      //logger.log(Level.SEVERE, "Could not start server.", ex);
    } catch (IOException ex) {
     // logger.log(Level.SEVERE, "Error opening server socket", ex);
    }         
  }

  private class RedirectThread extends Thread {
        
    private final Socket connection;
    private final Socket connection02;
        
    RedirectThread(Socket s , Socket dic) {
      this.connection = s; 
      this.connection02 = dic;
    }
        
    public void run() {
      try {   
        Writer out = new BufferedWriter(
                      new OutputStreamWriter(
                       connection.getOutputStream(), "US-ASCII"
                      )
                     );
        BufferedReader in = new BufferedReader(
        		new InputStreamReader(
                     new BufferedInputStream( 
                      connection.getInputStream()
                     )
                    ));
        Writer dout = new BufferedWriter(
                new OutputStreamWriter(
                 connection02.getOutputStream(), "US-ASCII"
                )
               );
        BufferedReader din = new BufferedReader(
        		new InputStreamReader(
               new BufferedInputStream( 
                connection02.getInputStream()
               )
              ));            
        // read the first line only; that's all we need
        StringBuilder request = new StringBuilder(80);
        while (true) {
          int c = in.read();
          if (c == '\r' || c == '\n' || c == -1) break;
          request.append((char) c);
        }
        
        String get = request.toString();
        String[] pieces = get.split(" ");
        String theFile = pieces[1];

	theFile = theFile.substring(1);


      // If this is HTTP/1.0 or later send a MIME header
      //  if (get.indexOf("HTTP") != -1) {
          out.write("\r\n"+"HTTP/1.0 200 OK\r\n");
          Date now = new Date();
          out.write("Date: " + now + "\r\n");
          out.write("Server: A1035537's Redirector 1.1\r\n");
      //  out.write("Location: " + newSite + theFile + "\r\n");        
          out.write("Content-type: text/plain\r\n\r\n");      
	  out.write(theFile+"\r\n");     //theFile = computer    
               

		 define(theFile,out,din,dout);
            
  

	out.flush(); 
      //  }
        // Not all browsers support redirection so we need to 
        // produce HTML that says where the document has moved to.
     /*   out.write("<HTML><HEAD><TITLE>Document moved</TITLE></HEAD>\r\n");
        out.write("<BODY><H1>Document moved</H1>\r\n");
        out.write("The document " + theFile  
         + " has moved to\r\n<A HREF=\"" + newSite + theFile + "\">" 
         + newSite  + theFile 
         + "</A>.\r\n Please update your bookmarks<P>");
        out.write("</BODY></HTML>\r\n");
        out.flush();
	*/
        //logger.log(Level.INFO, 
        //    "Redirected " + connection.getRemoteSocketAddress());
      } catch(IOException ex) {
        //logger.log(Level.WARNING, 
        //    "Error talking to " + connection.getRemoteSocketAddress(), ex);
      } finally {
        try {
          connection.close();
        } catch (IOException ex) {}  
      }     
    }
  }

  public static void main(String[] args) {

    int thePort;
    String theSite;
    
    try {
      theSite = args[0];
      // trim trailing slash
      if (theSite.endsWith("/")) {
        theSite = theSite.substring(0, theSite.length() - 1);
      }
    } catch (RuntimeException ex) {
      System.out.println(
          "Usage: java Redirector http://www.newsite.com/ port");
      return;
    }
    
    try {
      thePort = Integer.parseInt(args[1]);
    } catch (RuntimeException ex) {
      thePort = 5537;	// 80自己學號
    }  
      
    Proxy redirector = new Proxy(theSite, thePort);
    redirector.start();
  }
  
  static void define(String word, Writer writer, BufferedReader reader,Writer Dout2)
	      throws IOException, UnsupportedEncodingException {
	    Dout2.write("DEFINE foldoc " + word + "\r\n");
	    Dout2.flush();

	    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
	      if (line.startsWith("250 ")) { // OK
	        return;
	      } else if (line.startsWith("552 ")) { // no match
	        writer.write("No definition found for " + word + "\r\n");
	        return;
	      }
	      else if (line.matches("\\d\\d\\d .*")) continue;
	      else if (line.trim().equals(".")) continue;
	      else  writer.write(line + "\r\n");
	    }
	  } 
	}
