# How to use
1. Edit MyDsClientCanary.java to appropriate port number and AUTH credentials, by default these are 50000 and jack3100 respectively.
2. Compile using javac, etc.
3. You can now run a simulation either one of two ways via a console window: you can either use ds-server with a command such as: ./ds-server -c [path to config file] -v brief -n or you can run configs using a .sh file. 
4. If you use ds-server make sure to run the compiled MyDsClientCanary.class file in a seperate console window using the command: java MyDsClientCanary
5. Assuming this is done correctly the client should shedule all jobs before terminating automatically.

Note that the only files in the repo that are strictly needed are: MyDsClientCanary.java, MyDsClientCanary.class, ds-server/your own .sh file, and any config files you would like to run. All of these are located in this repo under COMP3100Project/stage-1/
