package in.vikramaditya.MudrikaVyavastha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MudrikaVyavasthaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MudrikaVyavasthaApplication.class, args);
    }

}
