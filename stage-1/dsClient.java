import java.net.*;
import java.io.*;
class dsClient {
    private static Socket s;
    private static BufferedReader din;
    private static DataOutputStream dout;
    private static boolean run;
public static void main(String args[])throws Exception{
    s = new Socket("127.0.0.1",50000);
    din = new BufferedReader(new InputStreamReader(s.getInputStream()));
    dout = new DataOutputStream(s.getOutputStream());
    run = true;
    auth();
    while(run){
        sendMessage("REDY");
        handleMessage();
    }
    handleQuit();
    din.close();
    dout.close();
    s.close();
}

public static void auth(){
    sendMessage("HELO");
    handleMessage("OK");
    sendMessage("AUTH jack3100");
    handleMessage("OK");
}

public static void sendMessage(String msg){
    try {
    String nlMsg = msg + "\n";
    System.out.print(nlMsg);
    dout.write(nlMsg.getBytes());
    dout.flush();
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public static Boolean doLstjRequest(String server){
    Boolean running = false;
    Boolean waiting = false;
    try {
        String stringBuffer = "";
        sendMessage("LSTJ "+server.split("\\s+")[0]+" "+server.split("\\s+")[1]);
        stringBuffer = din.readLine();
        int nRecs = Integer.parseInt(stringBuffer.split("\\s+")[1]);
        sendMessage("OK");
        for(int i=0; i < nRecs; i++){
            stringBuffer = din.readLine();
            switch(Integer.parseInt(stringBuffer.split("\\s+")[1])){
                case 1:
                    waiting = true;
                    continue;
                case 2:
                    running = true;
                    continue;
                default:
                    continue;
            }
        }
        if(nRecs == 0){
            handleMessage(".");
        }
        sendMessage("OK");
        handleMessage(".");
    } catch (IOException e) {
        e.printStackTrace();
    }
    return running && waiting;
}

public static void handleMessage(){
    try {
        String stringBuffer = din.readLine();
        System.out.println(stringBuffer);
        String[] stringBufferSplit = stringBuffer.split("\\s+");
        switch(stringBufferSplit[0]){
            case "JOBN":
                //TODO handle shedule msg based on algorithm type
                //probably use EJWT, LSTJ or other message to get estimated work time left 
                //for each server to find best candidate for job taker
                //handleJobnDUMB(stringBufferSplit);
                handleJobnFF(stringBufferSplit);
                break;
            case "NONE":
                    run = false;
                break;
            default:
                break;
        }
    } catch (IOException e){
        e.printStackTrace();
    }
}

public static void handleMessage(String check){
    try {
        String stringBuffer = din.readLine();
        System.out.println(stringBuffer);
        if(!stringBuffer.startsWith(check)){
            handleQuit();
        }
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public static void handleJobnFF(String[] JOBN){
    String targetServer = "";
    int lWaiting = 0;
    int lRunning = 0;
    Boolean firstServer = true;
    try {
        String stringBuffer = "";
        sendMessage("GETS Capable "+JOBN[4]+" "+JOBN[5]+" "+JOBN[6]);
        stringBuffer = din.readLine();
        int nRecs = Integer.parseInt(stringBuffer.split("\\s+")[1]);
        sendMessage("OK");
        for(int i=0; i < nRecs; i++){
            stringBuffer = din.readLine();
            if(firstServer){
                lWaiting = Integer.parseInt(stringBuffer.split("\\s+")[7]);
                lRunning = Integer.parseInt(stringBuffer.split("\\s+")[8]);
                targetServer = stringBuffer;
                firstServer = false;
            }
            if(Integer.parseInt(stringBuffer.split("\\s+")[7]) < lWaiting){
                if(Integer.parseInt(stringBuffer.split("\\s+")[8]) < lRunning){
                    targetServer = stringBuffer;
                }
            }
        }
        sendMessage("OK");
        handleMessage(".");
        schdJOBN(JOBN, targetServer);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void schdJOBN(String[] JOBN, String server){
    sendMessage("SCHD "+JOBN[2]+" "+server.split("\\s+")[0]+" "+server.split("\\s+")[1]);
    handleMessage("OK");
}

public static void handleQuit(){
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
