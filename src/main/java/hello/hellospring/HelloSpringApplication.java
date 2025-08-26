package hello.hellospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class HelloSpringApplication {

	public static void main(String[] args) {
		// SpringApplication.run(HelloSpringApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(HelloSpringApplication.class, args);

		Environment env = context.getEnvironment();
		String port = env.getProperty("server.port", "8080");
		String path = env.getProperty("server.servlet.context-path", "");

		System.out.println("서버 실행 경로: http://localhost:" + port + path + "/");
	}

}
