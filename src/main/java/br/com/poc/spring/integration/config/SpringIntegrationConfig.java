package br.com.poc.spring.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Pattern;

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


    @Bean
    public IntegrationFlow readFileFromInputDirectory() {
        return IntegrationFlows.from(inputDirectory(), consumer -> consumer.poller(Pollers.fixedDelay(5000)))
                .filter(fileFilter())
                .handle(processingDirectoryHandler())
                .get();
    }

    @Bean
    public GenericSelector<File> fileFilter() {
        return new GenericSelector<File>() {
            @Override
            public boolean accept(File source) {
                Pattern p1 = Pattern.compile("ABC.DEF.M[0-9]{3}");
                Pattern p2 = Pattern.compile("FILE.TEST.H[0-9]{6}");
                String fileNameWithoutExtension = source.getName().replace(".txt","");

                return (p1.matcher(fileNameWithoutExtension).matches() || p2.matcher(fileNameWithoutExtension).matches())
                        && source.getName().endsWith(".txt");
            }
        };
    }

    @Bean
    public MessageSource<File> inputDirectory() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(inputDir));
        return source;
    }

    @Bean
    public MessageHandler processingDirectoryHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(processingDir));
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);
        return  handler;
    }

//    @Bean
//    @InboundChannelAdapter(value = "processing_read_channel",poller = @Poller(fixedDelay = "2000"))
//    public MessageSource<File> readFileProcessing() {
//        // FILE READER
//        FileReadingMessageSource reader = new FileReadingMessageSource();
//        // SOURCE FILE DIRECTORY
//        reader.setDirectory(new File(processingDir));
//        // SOURCE FILE EXTENSION
//        reader.setFilter(new SimplePatternFileListFilter(FILE_TYPE));
//        return reader;
//    }
//
//    /**
//     * WRITTE SOURCE FILE CONTENT IN NEW FILE
//     *
//     * @return
//     */
//    @Bean
//    @ServiceActivator(inputChannel = "processing_write_channel")
//    public MessageHandler writtingProcessing() {
//        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(processingDir));
//        handler.setFileExistsMode(FileExistsMode.REPLACE);
//        handler.setExpectReply(false);
//        // EXCLUDE SOURCE FILE AFTER COPY IT TO THE DESTINATION DIR
//        handler.setDeleteSourceFiles(true);
//        return handler;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "output_channel")
//    public MessageHandler writtingOutput() {
//        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(outputDir));
//        handler.setFileExistsMode(FileExistsMode.REPLACE);
//        handler.setExpectReply(false);
//        handler.setDeleteSourceFiles(true);
//        return handler;
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "error_channel")
//    public MessageHandler writtingError() {
//        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(errorDir));
//        handler.setFileExistsMode(FileExistsMode.REPLACE);
//        handler.setExpectReply(false);
//        handler.setDeleteSourceFiles(true);
//        return handler;
//    }

}
