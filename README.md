# How to use
All files related to stage 2 are present withing the stage-2 folder and the respective test suite is included.

dsClient.class is the file submitted for this stage and an uncompiled java file is also provided.

dsClient takes up to 3 arguments:

- Arg 1 is an auth name. By default this is jack3100
- Arg 2 is for the socket ip. By default this is 127.0.0.1
- Arg 3 is for the socket port. By default this is 50000

Please note that unfortunately args must be made in order and you cannot ommit preceding args, ie. you cannot have just the ip and port args without putting the auth name before them.

However you can ommit proceding args, for example you can put only an auth name, or only an auth and ip arg.

If no args are made the defaults listed above will be used.

## Important note

Don't forget you may need to give permissions for some files using: chmod +x ./"file name here"
