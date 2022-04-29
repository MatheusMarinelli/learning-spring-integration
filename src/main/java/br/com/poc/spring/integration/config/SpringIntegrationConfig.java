package br.com.poc.spring.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
@EnableIntegration
public class SpringIntegrationConfig {

    private static final String INPUT_DIR = "E:\\file\\input";
    private static final String OUTPUT_DIR = "E:\\file\\output";
    private static final String PROCESSING_DIR = "E:\\file\\processing";

    private static final String FILE_TYPE = "*.txt";


    @Bean("input_channel")
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean("processing_channel")
    public MessageChannel processingChannel() {
        return new DirectChannel();
    }

    @Bean("processing_read_channel")
    public MessageChannel processingChannelReader() {
        return new DirectChannel();
    }

    @Bean("output_channel")
    public MessageChannel outputChannel() {
        return new DirectChannel();
    }

    /**
     * READ THE SOURCE FILE
     *
     * @return
     */
    @Bean
    @InboundChannelAdapter(value = "input_channel"
            ,poller = @Poller(fixedDelay = "2000")
    )
    public MessageSource<File> readFile() {
        // FILE READER
        FileReadingMessageSource reader = new FileReadingMessageSource();
        // SOURCE FILE DIRECTORY
        reader.setDirectory(new File(INPUT_DIR));
        // SOURCE FILE EXTENSION
        reader.setFilter(new SimplePatternFileListFilter(FILE_TYPE));
        return reader;
    }

    @Bean
    @InboundChannelAdapter(value = "processing_read_channel"
            ,poller = @Poller(fixedDelay = "2000")
    )
    public MessageSource<File> readFileProcessing() {
        // FILE READER
        FileReadingMessageSource reader = new FileReadingMessageSource();
        // SOURCE FILE DIRECTORY
        reader.setDirectory(new File(PROCESSING_DIR));
        // SOURCE FILE EXTENSION
        reader.setFilter(new SimplePatternFileListFilter(FILE_TYPE));
        return reader;
    }

    /**
     * WRITTE SOURCE FILE CONTENT IN NEW FILE
     *
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "processing_channel")
    public MessageHandler writting() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(PROCESSING_DIR));
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAppendNewLine(true);
        handler.setExpectReply(false);
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = "output_channel")
    public MessageHandler writtingOutput() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAppendNewLine(true);
        handler.setExpectReply(false);
        return handler;
    }

}
