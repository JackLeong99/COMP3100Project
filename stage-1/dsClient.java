import java.net.*;
import java.io.*;
class dsClient {
    boolean run = true;
    Socket s;
    BufferedReader din;
    DataOutputStream dout;
public void main(String args[])throws Exception{
  s=new Socket("127.0.0.1",50000);
  din=new BufferedReader(new InputStreamReader(s.getInputStream()));
  dout=new DataOutputStream(s.getOutputStream());
  auth();
  while(run){

  }
  dout.close();
  s.close();
}

public void auth(){
    sendMessage("HELO");
    handleMessage("OK");
    sendMessage("AUTH jack3100");
    handleMessage("OK");
}

public void sendMessage(String msg){
    try {
    String nlMsg = msg + "\n";
    dout.write(nlMsg.getBytes());
    dout.flush();
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public void handleMessage(String check){
    try {
        String stringBuffer = din.readLine();
        String[] stringBufferSplit = stringBuffer.split("\\s+");
        if(!stringBuffer.startsWith(check)){
            handleQuit();
        }
        else {
            switch(stringBufferSplit[0]){
                case "JCPL":
                    //TODO handle jcpl message
                    break;
                case "JOBN":
                    //TODO handle shedule msg based on algorithm type
                    break;
                case "NONE":
                    run = false;
                    break;
            }
        }
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public void handleQuit(){
    try {
        sendMessage("QUIT");
        String sin = din.readLine();
        while(!sin.equals("QUIT")){
            //wait for seconds
            sin=din.readLine();
        }
    } catch(IOException e){
        e.printStackTrace();
    }
}
}
