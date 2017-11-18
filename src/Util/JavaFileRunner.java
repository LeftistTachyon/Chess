package Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class JavaFileRunner {

    private static void printLines(String name, InputStream ins) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ins))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(name + " " + line);
            }
        }
    }

    private static void runProcess(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", process.getInputStream());
        printLines(command + " stderr:", process.getErrorStream());
        process.waitFor();
        System.out.println(command + " exitValue() " + process.exitValue());
    }
    
    @SuppressWarnings("ConvertToTryWithResources")
    private static void viewSourceCode(File file) {
        if (file == null) {
            throw new NullPointerException();
        }
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        while (true) {
            String line;
            try {
                line = reader.readLine();
            }
            catch (IOException ex) {
                ex.printStackTrace();
                break;
            }
            if (line == null) {
                break;
            }
            System.out.println(line);
        }
        if (reader != null) {
            try {
                reader.close();
            }
            catch (IOException ex) {
               
            }
        }
    }

    public static void main(String args[]) throws Exception {
        String jarFile = "C:\\Users\\zwill\\OneDrive\\Documents\\NetBeansProjects\\Test\\dist\\Test.jar";
        System.out.println(jarFile);
        runProcess("java -cp " + jarFile + " Test");
    }
}