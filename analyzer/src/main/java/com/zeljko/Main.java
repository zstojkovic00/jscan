package com.zeljko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeljko.model.Class;
import com.zeljko.parser.JavaProjectParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Wrong number of arguments");
            System.exit(1);
        }

        Path projectToScan = Paths.get(args[0]);

        if (!projectToScan.toFile().exists()) {
            System.err.println("Project does not exist");
            System.exit(1);
        }

        if (!projectToScan.toFile().isDirectory()) {
            System.err.println("Project path is not a directory");
            System.exit(1);
        }

        List<Class> classes = new JavaProjectParser().scan(projectToScan);
        prettyPrint(classes);
    }

    private static void prettyPrint(List<Class> classes) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(classes, System.out);
    }
}
