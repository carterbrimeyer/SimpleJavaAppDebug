I wrote this application to automatically debug a simple java application.




============== How to run test cases ==============

This is a simple application that encrypts or decrypts a string inputted by a user. Progammed in Java.

Compiling this applicaton requires Java and Java Development Kit (JDK) to be installed on your machine.

To compile and run this application, open the directory of the crypt.java file in terminal or command, and run the following commands;

-----

javac crypt.java

java crypt [encrypt/decrypt] [-offset] [offset_value] [string to encrypt/decrypt]

-----

The first command will compile the application and the second command will run the program in the console.

example cmd: java crypt -offset zebra Sample Text

This example command should give different outputs between the two versions of the application.



=== THE TEST CASE BUG ===

The intentional bug that I introduced is when updating the encryption maps, the last char of each alphabet does not get encrypted/decrypted. 
As a result, it does not translate to the expected value.


=============== How to run Debugger ===============

This program only works on the crypt.java file previously explained.
In order to run this application, similar to before, you must compile it with javac and then launch it with java using these commands

javac debugger.java

java debugger

it will then display its findings.

===================================================
