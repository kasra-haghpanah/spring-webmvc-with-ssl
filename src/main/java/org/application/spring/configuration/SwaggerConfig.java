package org.application.spring.configuration;


import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.text.MessageFormat;
import java.util.List;

@Configuration
public class SwaggerConfig {


/*    @Bean
    public OpenApiCustomizer openApiCustomizer() {

        final String applicationPath = MessageFormat.format("/{0}/", Properties.getApplicationName());

        return openApi -> openApi.addServersItem(new Server()
                .url("https://localhost:8443/spring")
                .description("Local HTTPS Server"));
    }*/

    @Bean
    public GroupedOpenApi publicApi() {
        String paths[] = {"/**"};
        return GroupedOpenApi.builder()
                .group(Properties.getApplicationName())
                .packagesToScan(Properties.getSpringdocPackagesToScan())
                .pathsToMatch(paths)
                .addOperationCustomizer(addGlobalItemToRequest())
                .build();
    }

    @Bean
    public OpenAPI springCustomerOpenAPI() {
        //SpringDocUtils.getConfig().addAnnotationsToIgnore(WebsocketUser.class);
        final String securitySchemeName = "bearerAuth";
        final String applicationPath = MessageFormat.format("/{0}/", Properties.getApplicationName());
        return new OpenAPI()
                .specVersion(SpecVersion.V31)
                .servers(List.of(new Server().url(applicationPath)))
//                .components(
//                        new Components()
//                                .addSecuritySchemes(
//                                        securitySchemeName,
//                                        new SecurityScheme()
//                                                .description("For calling services, you could not add the bearer word at the beginning of your token.")
//                                                .name("Bearer Authentication")
//                                                .type(SecurityScheme.Type.HTTP)
//                                                .scheme("bearer")
//                                                .bearerFormat("JWT")))
//                .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
                .info(
                        new Info()
                                .title("spring-microservice-reactive")
                                .version("v0.0.1")
                                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("this sample is about a spring mvc project")
                        .url("https://github.com/kasra-haghpanah/spring-microservice-reactive"));

    }

    @Bean
    public OperationCustomizer addGlobalItemToRequest() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            Parameter headerParameter = new Parameter().in(ParameterIn.HEADER.toString()).required(false).
                    schema(
                            new StringSchema()._default("en")._enum(List.of("en", "fa"))._default("fa").required(List.of("en"))
                    )
                    .name("Accept-Language");
            operation.addParametersItem(headerParameter);


            Parameter queryParameter = new Parameter().in(ParameterIn.QUERY.name().toString()).required(false).
                    schema(
                            new StringSchema()._default("export")._enum(List.of("import", "export"))._default("export")
                    )
                    .name("general-query");
            operation.addParametersItem(queryParameter);

            return operation;
        };
    }


}
