package br.com.poc.spring.integration.config;

import br.com.poc.spring.integration.utils.FileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@EnableIntegration
public class SpringIntegrationConfig {

    @Value("${file.dir.input}")
    private String inputDir;

    @Value("${file.dir.output}")
    private String outputDir;

    @Value("${file.dir.processing}")
    private String processingDir;

    @Value("${file.dir.error}")
    private String errorDir;

    @Autowired
    private FileFilter filter;

    /**
     * READ THE SOURCE FILE
     *
     * @return
     */
    @Bean
    @InboundChannelAdapter(value = "input_channel",poller = @Poller(fixedDelay = "2000"))
    public MessageSource<File> readFile() {
        // FILE READER
        FileReadingMessageSource reader = new FileReadingMessageSource();
        // SOURCE FILE DIRECTORY
        reader.setDirectory(new File(inputDir));
        // SOURCE FILE EXTENSION
        reader.setFilter(filter);
        return reader;
    }

    @Bean
    @InboundChannelAdapter(value = "processing_read_channel",poller = @Poller(fixedDelay = "2000"))
    public MessageSource<File> readFileProcessing() {
        // FILE READER
        FileReadingMessageSource reader = new FileReadingMessageSource();
        // SOURCE FILE DIRECTORY
        reader.setDirectory(new File(processingDir));
        // SOURCE FILE EXTENSION
        reader.setFilter(filter);
        return reader;
    }

    /**
     * WRITTE SOURCE FILE CONTENT IN NEW FILE
     *
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "processing_write_channel")
    public MessageHandler writtingProcessing() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(processingDir));
        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);
        // EXCLUDE SOURCE FILE AFTER COPY IT TO THE DESTINATION DIR
        handler.setDeleteSourceFiles(true);
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = "output_channel")
    public MessageHandler writtingOutput() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(outputDir));
        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = "error_channel")
    public MessageHandler writtingError() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(errorDir));
        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);
        return handler;
    }

}
