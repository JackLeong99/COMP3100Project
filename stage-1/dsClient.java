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
    //handles the initial handshake messages
    sendMessage("HELO");
    handleMessage("OK");
    sendMessage("AUTH jack3100");
    handleMessage("OK");
}

public static void sendMessage(String msg){
    //generic message function that adds newline char
    //while also allowing for easier debugging
    try {
    String nlMsg = msg + "\n";
    //System.out.print(nlMsg);
    dout.write(nlMsg.getBytes());
    dout.flush();
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public static void handleMessage(){
    //handle incoming message when you cant be sure what the next incoming message will be
    try {
        String stringBuffer = din.readLine();
        //System.out.println(stringBuffer);
        //split longer messages such as JOBN so that you can read just the first part
        //this is done intentionally over .startsWith() as it makes for a cleaner switch case
        //and allows you to pass the full message as a pre-split array into functions
        String[] stringBufferSplit = stringBuffer.split("\\s+");
        switch(stringBufferSplit[0]){
            case "JOBN":
                handleJobnFF(stringBufferSplit);
                break;
            case "NONE":
                //if None message is recieved break the main loop
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
    //alternate version of handle message for 
    //if you want to terminate when an unexpected message is recieved
    //also allows for easy debugging
    try {
        String stringBuffer = din.readLine();
        //System.out.println(stringBuffer);
        if(!stringBuffer.startsWith(check)){
            handleQuit();
        }
    }  catch(IOException e) {
        e.printStackTrace();
    }
}

public static void handleJobnFF(String[] JOBN){
    //main attempt at an algorithm that can even go on par with baseline algorithms
    //This is marginally worse/on par with baseline ff and better than a few of the others
    //however it does consistently outperform all baselines in terms of resource utilisation
    String targetServer = "";
    //store the lowst number of waiting and running jobs from the GETS response
    int lWaiting = 0;
    int lRunning = 0;
    //used to set the first server as the prefered option unless a better one is found
    Boolean firstServer = true;
    try {
        String stringBuffer = "";
        sendMessage("GETS Capable "+JOBN[4]+" "+JOBN[5]+" "+JOBN[6]);
        stringBuffer = din.readLine();
        int nRecs = Integer.parseInt(stringBuffer.split("\\s+")[1]);
        sendMessage("OK");
        for(int i=0; i < nRecs; i++){
            stringBuffer = din.readLine();
            //Set first server in GETS Capable response as baseline for availability
            if(firstServer){
                lWaiting = Integer.parseInt(stringBuffer.split("\\s+")[7]);
                lRunning = Integer.parseInt(stringBuffer.split("\\s+")[8]);
                targetServer = stringBuffer;
                firstServer = false;
            }
            //if a server has less running tasks than the first set it as the prefered
            if(Integer.parseInt(stringBuffer.split("\\s+")[8]) < lRunning){
                targetServer = stringBuffer;
                lWaiting = Integer.parseInt(stringBuffer.split("\\s+")[8]);
            }
            //if no server has less running tasks then prefer the one with least waiting tasks
            else if(Integer.parseInt(stringBuffer.split("\\s+")[7]) < lWaiting){
                targetServer = stringBuffer;
                lWaiting = Integer.parseInt(stringBuffer.split("\\s+")[7]);
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
    //Automatically format a SCHD message given a job and target server
    sendMessage("SCHD "+JOBN[2]+" "+server.split("\\s+")[0]+" "+server.split("\\s+")[1]);
    handleMessage("OK");
}

public static void handleQuit(){
    //handle quitting
    try {
        sendMessage("QUIT");
        String sin = din.readLine();
        while(!sin.equals("QUIT")){
            sin=din.readLine();
        }
    } catch(IOException e){
        e.printStackTrace();
    }
}
}
