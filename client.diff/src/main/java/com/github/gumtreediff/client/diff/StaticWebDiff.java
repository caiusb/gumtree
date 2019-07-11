package com.github.gumtreediff.client.diff;

import com.github.gumtreediff.client.Run;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StaticWebDiff  {

    static {
        Run.initClients();
        Run.initGenerators();
    }

    public static void main(String[] args) throws Exception {
        String csvFile = args[0];
        String outputFolder = args[1];
        File output = new File(outputFolder);
        if (!output.exists()) {
            output.mkdirs();
        }
        List<String> toProcess = Files.readAllLines(Paths.get(csvFile));
        sliding(toProcess, 2).forEach(p -> {
            File firstFile = new File(p.get(0));
            File secondFile = new File(p.get(1));
            try {
                String htmlDiff = StringHtmlDiff.getHtmlOfDiff(firstFile, secondFile);
                Files.write(Paths.get(outputFolder, firstFile.getName() + "_" + secondFile.getName() + ".html"),
                        htmlDiff.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage() + "\n" + e);
            }
        });
    }

    private static <T> Stream<List<T>> sliding(List<T> list, int size) {
        if (size > list.size()) {
            return Stream.empty();
        } else {
            return IntStream.range(0, list.size() - size + 1)
                    .mapToObj(start -> list.subList(start, start + size));
        }
    }
}
