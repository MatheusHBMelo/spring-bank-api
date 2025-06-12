package br.com.springbank.config.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${app.server.url.dev}")
    private String devUrl;
    @Value("${app.server.url.prod}")
    private String prodUrl;

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("URL do ambiente de desenvolvimento");

        Server prodServer = new Server();
        devServer.setUrl(prodUrl);
        devServer.setDescription("URL do ambiente de produção");

        Contact contact = new Contact();
        contact.setName("Matheus Barbosa");
        contact.setEmail("matheushbmelo@gmail.com");

        License mitLicense = new License();
        mitLicense.name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info();
        info.title("Spring Bank API");
        info.version("1.0.0");
        info.contact(contact);
        info.description("Simulação de conta bancaria com operações.");
        info.license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
