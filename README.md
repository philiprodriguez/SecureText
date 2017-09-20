# SecureText
This is a utility I made mainly for myself. Its purpose is to make working with encrypted text files a very easy task. I always hated other solutions for encrypting text files, as they were too much work to use over and over again. I designed this to be literally the easiest thing in the world to use. You download the jar, throw it in the directory of your encrypted text files, and double click the dang jar to make it go. Just give it a try. It's hard to not figure out how it works. Relies on Jasypt, and uses internally the DESEDE algorithm with SHA-1. The standalone jar is found in the "store" directory.

# Very Fast Tutorial
1. Grab the standalone jar file from the "store" directory.
2. Copy that standalone jar file into the directory in which you want to make and work with encrypted text files.
3. Double click the standalone jar file.
4. Enter the name of the encrypted file you want to create or edit.
5. Type things!
6. Exit the window, and see that your created file contains only encrypted junk text.
7. Double click the standalone jar file, enter the same name and password as before.
8. See that you can now read your file and make further edits!

# Additional Notes
- This program was intended to be a very simple notepad application and thus does not offer much functionality other than creating, reading, and modifying the text of encrypted text files.
- This program has very simple undo capabilities via the CTRL+Z convention.
- CTRL+S will save the document for those of you scared of power outages or whatnot.
- An equivalent save operation to CTRL+S is performed upon normal window closing.
- This program creates absolutely no temporary files at all. It reads and writes directly to and from the file you tell it to look at.
- This program should work anywhere with a modern Java JRE installed.
- Feel free to modify this project to make it better! I made this thing in two hours. Of course it isn't as efficient or pretty as it could be. It's just usable as it.
