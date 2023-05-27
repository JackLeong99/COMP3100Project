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
    sendMessage("REDY");
    handleMessage();
  }
  handleQuit();
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

public void handleMessage(){
    try {
        String stringBuffer = din.readLine();
        String[] stringBufferSplit = stringBuffer.split("\\s+");
        switch(stringBufferSplit[0]){
            case "JCPL":
                //TODO handle jcpl message
                //do i even need to handle jcpl messages?...
                break;
            case "JOBN":
                //TODO handle shedule msg based on algorithm type
                //probably use EJWT, LSTJ or other message to get estimated work time left 
                //for each server to find best candidate for job taker
                handleJobnSRTN(stringBufferSplit);
                break;
            case "NONE":
                    run = false;
                break;
            default:
                //TODO default handle if needed
                break;
        }
    } catch (IOException e){
        e.printStackTrace();
    }
}

public void handleMessage(String check){
    //TODO make this return a string of recieved message 
    try {
        String stringBuffer = din.readLine();
        if(!stringBuffer.startsWith(check)){
            handleQuit();
        }
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public void handleJobnSRTN(String[] JOBN){   
    try{
    String stringBuffer = ""; 
    sendMessage("GETS Avail "+JOBN[4]+" "+JOBN[5]+" "+JOBN[6]);
    //get DATA response
    stringBuffer = din.readLine();
    int nRecs = Integer.parseInt(stringBuffer.split("\\s+")[1]);
    sendMessage("OK");
    //Handle server listings
    String targetServerType = "";
    String targetServerID = "";
    for(int i=0; i < nRecs; i++){
        stringBuffer = din.readLine();
        targetServerType = stringBuffer.split("\\s+")[0];
        targetServerID = stringBuffer.split("\\s+")[1];
    }
    sendMessage("OK");
    handleMessage(".");
    if(nRecs > 0){
        schdJOBN(JOBN[2], targetServerType, targetServerID);
    }
    else{
        sendMessage("GETS Capable "+JOBN[4]+" "+JOBN[5]+" "+JOBN[6]);
        stringBuffer = din.readLine();
        nRecs = Integer.parseInt(stringBuffer.split("\\s+")[1]);
        sendMessage("OK");
        for(int i=0; i < nRecs; i++){
            stringBuffer = din.readLine();
            targetServerType = stringBuffer.split("\\s+")[0];
            targetServerID = stringBuffer.split("\\s+")[1];
        }
        sendMessage("OK");
        handleMessage(".");
        schdJOBN(JOBN[2], targetServerType, targetServerID);
    }
    } catch(IOException e){
        e.printStackTrace();
    }   
}

public void schdJOBN(String jobID, String serverType, String serverID){
    sendMessage("SCHD "+jobID+" "+serverType+" "+serverID);
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