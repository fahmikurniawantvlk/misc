import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ModuleCleaner {
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Require 2 parameter: absolute path of the project & top module name");
      return;
    }
    String path = args[0];
    String topComponent = args[1];
    List<String> dep = new ArrayList<>();
    dep.add(topComponent);
    iterate(path, topComponent, dep);


    Collections.sort(dep);

    Set<String> set = new TreeSet<>(dep);

    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      File file = listOfFiles[i];
      if (file.isDirectory()) {
        boolean isModule = false;
        File[] dirFiles = file.listFiles();
        for (int j = 0; j < dirFiles.length; j++) {
          if (dirFiles[j].isFile() && dirFiles[j].getName().equalsIgnoreCase("build.gradle")) {
            isModule = true;
          }
        }

        if (isModule && !set.contains(file.getName())) {
          delete(file);
        }
      }
    }

    System.out.println("Done!");
  }

  private static void delete(File f) throws IOException {
    if (f.isDirectory()) {
      for (File c : f.listFiles())
        delete(c);
    }
    if (!f.delete())
      throw new FileNotFoundException("Failed to delete file: " + f);
  }

  public static void iterate(String path, String module, List<String> dep) throws IOException {
    String gradleFile = String.format("%s/%s/%s", path, module, "build.gradle");
    try {


      BufferedReader br = new BufferedReader(new FileReader(gradleFile));
      try {
        String line = br.readLine();
        while (line != null) {
          line = br.readLine();
          Pattern pattern = Pattern.compile("implementation project( )*\\(\\\":([a-z-]*)\"\\)");
          Matcher matcher = pattern.matcher(line);
          if (matcher.find()) {
            String mod = matcher.group(2);
            dep.add(mod);
            iterate(path, mod, dep);
          }
        }
      } finally {
        br.close();
      }
    } catch (Exception e) {
      //System.out.println(module);
    }
  }

}
