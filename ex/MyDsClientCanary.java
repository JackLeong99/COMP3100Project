import java.net.*;
import java.io.*;
class MyDsClientCanary{
public static void main(String args[])throws Exception{
  Socket s=new Socket("127.0.0.1",61420);
  BufferedReader din=new BufferedReader(new InputStreamReader(s.getInputStream()));
  DataOutputStream dout=new DataOutputStream(s.getOutputStream());
  BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

  String localIn="",sin="",sout="";
  boolean init=false;
  //temporary hardcoded handshake
  dout.write(("HELO\n").getBytes());
  dout.flush();
  while(true){
    sin=din.readLine();
    if(sin.equals("OK")){
      sin="";
      break;
    }
    else{
      continue;
    }
  }
  System.out.println("passed HELO");
  dout.write(("AUTH PIPI\n").getBytes());
  dout.flush();
  while(true){
    sin=din.readLine();
    if(sin.equals("OK")){
      sin="";
      break;
    }
    else{
      continue;
    }
  }
  System.out.println("passed AUTH");
  while(!sin.equals("NONE")){
    dout.write(("REDY\n").getBytes());
    dout.flush();
    
  }


  dout.write(("QUIT\n").getBytes());
  while(!sin.equals("QUIT")){
    localIn=br.readLine();
    sin=din.readLine();
  }
  dout.close();
  s.close();
}
}
