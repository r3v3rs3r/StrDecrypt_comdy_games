import java.nio.file.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.*;

public class StrDecrypt {

// function for string decryption
private static String a(String str) {
    byte[] bytes = str.getBytes();
    for (int i = 0; i < bytes.length; i++) {
        bytes[i] = (byte) (bytes[i] - 2);
    }
    return new String(bytes);
}

private static void doDecrypt(Path filename){
	Charset charset = StandardCharsets.UTF_8;
    boolean sameFile = false; // flag for logging strings of the same file

	try {
	  String content = new String(Files.readAllBytes(filename), charset);

	Pattern regex = Pattern.compile("a.a\\(\"(.*?)\"\\)", Pattern.DOTALL | Pattern.MULTILINE);
	Matcher regexMatcher = regex.matcher(content);

	while (regexMatcher.find()) {
	if (!sameFile){
        System.out.println("==================================================================");
        System.out.println("path:" + filename);
        System.out.println();
        sameFile = true;
    }
    System.out.println("encrypted: " + regexMatcher.group(1));
	String decrStr  = a(regexMatcher.group(1));
	System.out.println("decrypted: "+decrStr);
    System.out.println();

    content = content.replace(regexMatcher.group(0), "\""+decrStr+"\"");
    }
	  Files.write(filename, content.getBytes(charset));
	}
	catch(IOException e) {
	  e.printStackTrace();
	} 
}
////////////////////////////////////////////////////////////////////////////////////////////////////
// MAIN
////////////////////////////////////////////////////////////////////////////////////////////////////
public static void main(String[] args){
    String targetfolder = "encrfolder";
    if (args.length ==1) targetfolder = args[0];

     FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) {
        /* for debug
        System.out.println("-------------------------------------");
        System.out.println("DIRECTORY NAME:"+ dir.getFileName() +"\n"+
                           "LOCATION:"+ dir.toFile().getPath());
        System.out.println("-------------------------------------");
         */
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes) {
        //System.out.println("FILE NAME: "+ visitedFile.getFileName());
        doDecrypt( visitedFile);
        return FileVisitResult.CONTINUE;
      }
    };

    FileSystem fileSystem = FileSystems.getDefault();
    Path rootPath = fileSystem.getPath(targetfolder);
    try {
      Files.walkFileTree(rootPath, simpleFileVisitor);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    } // end main
}