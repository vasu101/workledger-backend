package com.workledger.core.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workLedgerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WorkLedger API")
                        .description("Timesheet and work Management System")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Titanium Team")
                                .email("support@titanium.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("WorkLedger Documentation")
                        .url("https://docs.workledger.com"));
    }
}
