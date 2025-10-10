package com.spring.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RestApiApplication {

    private static final Logger logger = LoggerFactory.getLogger(RestApiApplication.class);

    public static void main(String[] args) {
        var context = SpringApplication.run(RestApiApplication.class, args);
        Environment env = context.getEnvironment();

        String appName = env.getProperty("spring.application.name", "REST API Application");
        String port = env.getProperty("server.port", "8080");
        String profiles = String.join(", ", env.getActiveProfiles());

        logger.trace("🔍 [TRACE] Application started in TRACE mode (if enabled).");
        logger.debug("🐛 [DEBUG] Debugging startup sequence...");
        logger.info("🎉 [INFO] {} started successfully!", appName);
        logger.info("🌍 Active Profiles: {}", profiles.isEmpty() ? "default" : profiles);
        logger.info("🚀 Running on: http://localhost:{}", port);

        logger.info("📊 Swagger UI: http://localhost:{}/swagger-ui.html", port);
        logger.info("📚 API Docs: http://localhost:{}/v3/api-docs", port);

       
        logger.info("✅ Health Check: http://localhost:{}/actuator/health", port);
        logger.info("📈 Metrics: http://localhost:{}/actuator/metrics", port);

        logger.info("🌟 Ready to handle requests!");

   
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            logger.info("🛑 {} is shutting down gracefully...", appName)
        ));
    }
}

