package br.com.poc.spring.integration;

import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class MessageExtractor {

    /**
     * Intercept an input channel e post the return on an output channel
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Transformer(inputChannel = "input_channel", outputChannel = "processing_channel")
    public File sendFileToProcessingChannel(File file) {
        return file;
    }

    @Transformer(inputChannel = "processing_read_channel", outputChannel = "output_channel")
    public File sendFileToOutputChannel(File file) throws IOException {
        System.out.println("Processing start at: " + LocalDateTime.now());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.lines().forEach(line -> {
            if (line.getBytes(StandardCharsets.UTF_8).length != 150) {
                System.out.println("Linha menor que 150 Bytes");
            }
        });
        System.out.println("Processing finished at: " + LocalDateTime.now());
        reader.close();
        return file;
    }

}
