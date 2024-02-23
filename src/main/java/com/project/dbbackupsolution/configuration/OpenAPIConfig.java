package com.project.dbbackupsolution.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setDescription("Development Server");

        return new OpenAPI()
                .info(new Info()
                        .title("DB Backup Solution API")
                        .version("1.0")
                        .description("Backup Management System using GCP Storage")
                        .contact(new Contact()
                                .email("uallessons@gmail.com")
                                .name("Uallesson Nunes Ivo")
                                .url("https://uallesson.vercel.app/"))).servers(List.of(devServer));
    }
}
