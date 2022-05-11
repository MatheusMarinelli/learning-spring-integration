package br.com.poc.spring.integration.utils;

import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FileFilter implements FileListFilter<File> {

    private final Pattern p1 = Pattern.compile("ABC.DEF.M[0-9]{3}");
    private final Pattern p2 = Pattern.compile("ABC.[0-9]{3}");
    private final Pattern p3 = Pattern.compile("XYZ.H[0-9]{3}");

    @Override
    public List<File> filterFiles(File[] files) {
        return Arrays.stream(files)
                .filter(file -> file.length() > 0)
                .filter(file -> file.getName().endsWith(".txt"))
                .filter(file -> p1.matcher(file.getName().replace(".txt","")).matches() ||
                        p2.matcher(file.getName().replace(".txt","")).matches() ||
                        p3.matcher(file.getName().replace(".txt","")).matches())
                .collect(Collectors.toList());
    }
}
