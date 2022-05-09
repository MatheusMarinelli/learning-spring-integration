package br.com.poc.spring.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MessageExtractor {

    @Autowired
    @Qualifier("error_channel")
    private MessageChannel errorChannel;

    /**
     * Check if file has 150 bytes per line
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Transformer(inputChannel = "processing_read_channel", outputChannel = "output_channel")
    public File sendFileToOutputChannel(File file) throws IOException {
        AtomicBoolean inconsistency = new AtomicBoolean(false);

        System.out.println("Processing start at: " + LocalDateTime.now());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.lines().forEach(line -> {
            if (line.getBytes(StandardCharsets.UTF_8).length != 150) {
                System.out.println("Line length is lesser then 150 bytes!");
                inconsistency.set(true);
            }
        });

        // if file is not consistent it goes to an error channel
        if (inconsistency.get()) {
            reader.close();
            errorChannel.send(new GenericMessage<>(file));
            throw new RuntimeException("File has inconsistent lines!");
        }

        System.out.println("Processing finished at: " + LocalDateTime.now());
        reader.close();
        return file;
    }

}
