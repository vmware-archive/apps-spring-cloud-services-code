package io.pivotal.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HelloSpringBootApplication {

	Logger logger = LoggerFactory
			.getLogger(HelloSpringBootApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringBootApplication.class, args);
	}

	/**
	 * 
	 * Loads the database on startup
	 * 
	 * @param gr
	 * @return
	 */
	@Bean
	CommandLineRunner loadDatabase(GreetingRepository gr) {
		return args -> {
			logger.debug("loading database..");
			gr.save(new Greeting(1, "Hello"));
			gr.save(new Greeting(2, "Hola"));
			gr.save(new Greeting(3, "Ohai"));
			logger.debug("record count: {}", gr.count());
			gr.findAll().forEach(x -> logger.debug(x.toString()));
		};

	}

}
