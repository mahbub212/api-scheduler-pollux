package id.co.msiglife.pollux.schduler;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;
import java.util.TimeZone;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Workflow Engine",
                version = "1.0.0"
        )
)

@EnableAsync
public class SchedulerApplication implements CommandLineRunner {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("===================================");
        System.out.println("Spring Boot application is running! Please be aware your active profile");
        System.out.println("Active profile: " + activeProfile);
        System.out.println("DataSource URL: " + dataSource.getConnection().getMetaData().getURL());
        System.out.println("===================================");
    }
}
