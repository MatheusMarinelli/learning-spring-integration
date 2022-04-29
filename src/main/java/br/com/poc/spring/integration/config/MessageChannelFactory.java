package br.com.poc.spring.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MessageChannelFactory {

    @Bean("input_channel")
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean("processing_write_channel")
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

    @Bean("error_channel")
    public MessageChannel errorChannel() {
        return new DirectChannel();
    }

}
