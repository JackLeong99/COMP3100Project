import java.net.*;
import java.io.*;
class MyDsClientCanary{
public static void main(String args[])throws Exception{
  Socket s=new Socket("127.0.0.1",50000);
  BufferedReader din=new BufferedReader(new InputStreamReader(s.getInputStream()));
  DataOutputStream dout=new DataOutputStream(s.getOutputStream());
  BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
  //string buffer for checking messages
  String localIn="",sin="",sout="";
  //bufferes for server info, must be stored here, or rather
  //not physically next to logic that uses them,
  //this is because the SCHD message also needs to reference lServer,
  //and I want to group all the related buffers together for readability
  int sTypeNo = 0;
  int currentServerNo = 0;
  int lCores = 0;
  String sType = "";
  String[] lServer = new String[7];
  String[] serverToken = new String[7];
  //flag to make sure GETS is only run the one time
  boolean init=true;
  //handshake start
  dout.write(("HELO\n").getBytes());
  dout.flush();
  sin=din.readLine();
  //check for OK response, any other response results in the client terminating the connection
  if(!sin.equals("OK")){
    //TODO Quit function
  }
  //System.out.println("passed HELO");
  dout.write(("AUTH jack3100\n").getBytes());
  dout.flush();
  sin=din.readLine();
  if(!sin.equals("OK")){
    //System.out.println("tried to quit");
    //TODO Quit function
  }
  //System.out.println("passed AUTH");
  //handshake end
  while(true){
    dout.write(("REDY\n").getBytes());
    dout.flush();
    sin=din.readLine();
    //Do gets and find/record biggest server type if this is first runthrough
    if(init){
      //temp string buffer for initial GETS 
      //while still retaining the msg recieved before GETS was sent
      String sin2="";
      //set init to false so that next loop GETS isnt sent/handled
      init = false;
      //send request for DATA
      dout.write(("GETS All\n").getBytes());
      dout.flush();
      //buffers for DATA msg
      int nRecs = 0;
      String[] dataToken = new String[3];
      //split DATA message and get nRecs as an int
      sin2=din.readLine();
      //System.out.println(sin2);
      dataToken = sin2.split("\\s+");
      nRecs = Integer.parseInt(dataToken[1]);
      //System.out.println(nRecs);
      dout.write(("OK\n").getBytes());
      dout.flush();
      for(int i = 0; i < nRecs; i++){
        sin2=din.readLine();
        serverToken = sin2.split("\\s+");
        if(lCores < Integer.parseInt(serverToken[4])){
          sTypeNo = 1;
          sType = serverToken[0];
          lCores = Integer.parseInt(serverToken[4]);
          lServer = serverToken;
        }
        else if(sType.equals(serverToken[0])){
          sTypeNo++;
        }
      }
      dout.write(("OK\n").getBytes());
      dout.flush();
      sin2=din.readLine();
      //System.out.println(lServer[0]);
    }
    System.out.println("Server: "+sin);
    if(sin.startsWith("JCPL")){
      continue;
    }
    else if(sin.startsWith("JOBN")){
      //do sheduling
      if(currentServerNo > sTypeNo-1){
        currentServerNo = 0;
      }
      //System.out.println("JOBN is: "+sin);
      String schdMsg = "SCHD "+sin.split("\\s+")[2]+" "+lServer[0]+" "+Integer.toString(currentServerNo)+"\n";
      currentServerNo++;
      dout.write((schdMsg).getBytes());
      dout.flush();
      sin=din.readLine();
      //System.out.println("sin is: "+sin);
      continue;
    }
    else if(sin.equals("NONE")){
      break;
    }
  }


  dout.write(("QUIT\n").getBytes());
  while(!sin.equals("QUIT")){
    sin=din.readLine();
  }
  dout.close();
  s.close();
}
}
