Install the application by pulling off the Github Repo Link Below:

https://github.com/Flanks365/CSAssignment2

1. To use the application
you want to cd or navigate into the UploadServer folder

cd /your/virtualpath/UploadServer

2.5 check if you have jdk 17 or above installed

3. you want to javac every file that has a .java ender to the file
    (statements should look like javac *file name*.java)

4. once all files are compiled you can simply run the application
by inputting java UploadServer in your command prompt.

this should result in the console printing "Waiting for connection on port 8082 to create thread"

5. simply navigate to your browser and type in localhost/8082 and a simple
HTML form will appear.


FORM INSTRUCTIONS:

1. input the name for the file you want in the first field

2. input a date (this could be a special date or the current date all your choice)

3. IMPORTANT! this is the main section for the form field the file section you can put
any file you would like could be a jpg, png, or text file but this is the main point of the 
UploadServer

4. Press Submit, now the return would be what is currently in the uploads folder in the 
UploadServer folder and the file would be labled with the name / date / actual file name


CONSOLE CLIENT INSTRUCTIONS:

1. To use the application
you want to cd or navigate into the ConsoleApp folder

cd /your/virtualpath/UploadServer

2. Run the application by inputting java Activity in your command prompt.

3. Follow the prompts in the console to enter a caption and file to upload to the server.


C/C++ SERVER INSTRUCTIONS

1. In a Cygwin terminal, navigate to ROOT/Linux/UploadServer.

2. Run the command: g++ -o server *.cpp

3. Run the command: ./server

4. Connect to the server using a browser via localhost:8888 or another client on port 8888


Work Spread:
Tom,Mike,Michael,Nav,Bryan

we initially spreaded the task out to Tom, Mike, Michael to the ConsoleApp section
and Bryan and Nav on the server side but due to the complexity of the Server we decided
to team up once the consoleApp was completed and worked on the server thread together 
this meant we were all assiting each other in the actual thought process of adjusting the code
