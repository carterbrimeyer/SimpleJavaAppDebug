/**
 *
 * This program attempts to create a simple automatic debugger for the crypt.java program.
 * Concepts based on the paper "Yesterday, my program worked. Today, it does not. Why?"
 *
 * @author  Carter Brimeyer
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class patchFileList {
    public String header = "";
    final String EOF = "\\ No newline at end of file";
    public List<String> changes;
    public patchFileList(Process process)
    {
        changes = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            this.header = reader.readLine() + "\n" + reader.readLine();
            String change = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("@@")) {
                    this.changes.add(change);
                    change = "";
                }
                if (line.equals(this.EOF)) {
                    this.changes.add(change);
                }
                change += "\n" + line;
            }
        }
        catch (Exception e)
        {
            System.out.println("Error getting patch to list");
        }
    }
    public void toFile(List<String> _changes)
    {
        try {
            File myObj = new File("tmp/crypt.patch");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred attempting to create a patch file.");
            e.printStackTrace();
        }
        try {
            FileWriter writer = new FileWriter("tmp/crypt.patch");
            writer.write(this.header + "\n");
            for(int i = 0; i < _changes.size(); i++)
            {
                writer.write(_changes.get(i));
            }
            writer.write("\n" + this.EOF);
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while attempting to write to patch file.");
            e.printStackTrace();
        }
    }
}
public class debugger {

    public static void main(String[] args) {
        try{
            List<String> mostSuccessful;
            deleteFile("tmp/patched/crypt.java");
            deleteFile("tmp/buggy/crypt.java");

            deleteFile("tmp/patched/crypt.class");
            deleteFile("tmp/patched/encrypter.class");
            deleteFile("tmp/patched/helper.class");

            Files.copy(Paths.get("source_code/bug_free_code/crypt.java"), Paths.get("tmp/patched/crypt.java"));
            Files.copy(Paths.get("source_code/buggy_code/crypt.java"), Paths.get("tmp/buggy/crypt.java"));

            Process p = Runtime.getRuntime().exec("diff -u tmp/patched/crypt.java tmp/buggy/crypt.java");
            patchFileList pFL = new patchFileList(p);

            deltaDebug(pFL);
        }
        catch(Exception e)
        {
            System.out.println("Something went wrong. " + e.getMessage());
        }


    }
    public static List<String> deltaDebug(patchFileList pFL)
    {
        List<String> errors = new ArrayList<String>();

        if(compileAndTest(pFL, rightSplit(pFL.changes))){
            System.out.println("YIPPEE! RIGHT");
        }
        return null;
    }
    public static List<String> leftSplit(List<String> list)
    {
        List<String> left = new ArrayList<String>();
        for(int i = 0; i < list.size() / 2; i++)
        {
            left.add(list.get(i));
        }
        return left;
    }
    public static List<String> rightSplit(List<String> list)
    {
        List<String> right = new ArrayList<String>();
        for(int i = list.size() / 2; i < list.size(); i++)
        {
            right.add(list.get(i));
        }
        return right;
    }
    public static boolean compileAndTest(patchFileList pFL, List<String> list)
    {
        patchResults(pFL, list);
        try
        {
            if(compile("crypt.java") == 0)
            {
                String temp = run("crypt", "encrypt -offset zebra ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
                System.out.println("(" + temp + ")");
                return (temp.split(" ")[9].equals("zebra0918273645PHQGIUMEAYLNOFDXJKRCVSTZWBphqgiumylnofdxjkcvstw"));
            }
        }
        catch(Exception e)
        {
            System.out.println("An error occurred while testing changes. " + e.getMessage());
        }
    return false;
    }
    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    public static void patchResults(patchFileList pFL, List<String> results)
    {
        pFL.toFile(results);
        try
        {
            Process p = Runtime.getRuntime().exec("patch -u tmp/patched/crypt.java -i tmp/crypt.patch");
            printResults(p);
        }
        catch(Exception e)
        {
            System.out.println("Error occurred while patching file");
        }
    }
    public static int compile(String file) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("javac", file);
        pb.redirectError();
        pb.directory(new File("tmp/patched"));
        Process p = pb.start();

        int result = p.waitFor();

        return result;
    }
    public static String run(String app, String params) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", app, params);
        pb.redirectError();
        pb.directory(new File("tmp/patched"));
        Process p = pb.start();

        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        StringBuilder temp = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            temp.append("\n").append(line);
        }
        return temp.toString();
    }
    public static void deleteFile(String path)
    {
        try
        {
            Path del = Paths.get(path);
            if (Files.exists(del))
                Files.delete(del);
        }
        catch(Exception e)
        {
            System.out.println("Error deleting file " + path + " : " + e.getMessage());
        }
    }
}
