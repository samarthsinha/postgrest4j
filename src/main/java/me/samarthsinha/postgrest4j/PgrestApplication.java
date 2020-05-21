package me.samarthsinha.postgrest4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("me.samarthsinha.postgrest4j.*")
@SpringBootApplication
public class PgrestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PgrestApplication.class, args);
    }

}
