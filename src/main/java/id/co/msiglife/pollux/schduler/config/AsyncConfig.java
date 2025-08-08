package id.co.msiglife.pollux.schduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public TaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Jumlah minimum thread
        executor.setMaxPoolSize(10); // Jumlah maksimum thread
        executor.setQueueCapacity(25); // Kapasitas antrian
        executor.setThreadNamePrefix("MyAsyncThread-");
        executor.initialize();
        return executor;
    }
}
