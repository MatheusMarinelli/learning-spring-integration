package br.com.poc.spring.integration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;
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


    @Autowired
    @Qualifier("processing_read_channel")
    private MessageChannel processingReadChannel;

    @Autowired
    @Qualifier("error_channel")
    private MessageChannel errorChannel;

    @Autowired
    @Qualifier("output_channel")
    private MessageChannel outputChannel;


    @Bean
    public IntegrationFlow readFileFromInputDirectory() {
        return IntegrationFlows.from(inputDirectory(), consumer -> {
                    consumer.poller(Pollers.fixedDelay(2000));
                })
                .filter(fileFilter())
                .handle(processingDirectoryHandler())
                .get();

    }

    @Bean
    public IntegrationFlow readFileFromProcessingDirectory() {
        return IntegrationFlows.from(processingDirectory(), consumer -> consumer.poller(Pollers.fixedDelay(2000)))
                .filter(fileFilter())
                .channel(processingReadChannel)
                .get();
    }

    @Bean
    public IntegrationFlow readFileFromOutputChannel() {
        return IntegrationFlows.from(outputChannel)
                .handle(outputDirectoryHandler())
                .get();
    }

    @Bean
    public IntegrationFlow readFileFromErrorChannel() {
        return IntegrationFlows.from(errorChannel)
                .handle(errorDirectoryHandler())
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
    public MessageSource<File> processingDirectory() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(processingDir));
        return source;
    }

    @Bean
    public MessageHandler processingDirectoryHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(processingDir));
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);
        return  handler;
    }

    @Bean
    public MessageHandler outputDirectoryHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(outputDir));
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);
        return  handler;
    }

    @Bean
    public MessageHandler errorDirectoryHandler() {
        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(errorDir));
        handler.setExpectReply(false);
        handler.setDeleteSourceFiles(true);
        return  handler;
    }



}
