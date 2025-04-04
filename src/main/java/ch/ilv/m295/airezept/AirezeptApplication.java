package ch.ilv.m295.airezept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@EnableWebMvc
@ComponentScan(basePackages = "ch.ilv.m295.airezept")
public class AirezeptApplication {

	private static final Logger logger = LoggerFactory.getLogger(AirezeptApplication.class);

	public static void main(String[] args) {
		var context = SpringApplication.run(AirezeptApplication.class, args);
		var handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
		handlerMapping.getHandlerMethods().forEach((key, value) -> 
			logger.info("Mapped: {} -> {}", key, value));
	}

}
