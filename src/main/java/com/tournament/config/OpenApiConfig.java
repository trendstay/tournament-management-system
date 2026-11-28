package com.tournament.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Описание REST API для Swagger UI (лабораторная работа №4).
 *
 * <p>Swagger UI открывается по адресу {@code /swagger-ui/index.html}, JSON-спецификация —
 * по адресу {@code /v3/api-docs} (адрес {@code /swagger-ui.html} перенаправляет на страницу UI).
 * Оба адреса доступны без входа, а сами методы API защищены HTTP Basic.</p>
 *
 * <p>Схема безопасности {@code basicAuth} добавляет в Swagger UI кнопку Authorize:
 * после ввода email и пароля запросы из браузера выполняются уже
 * с заголовком {@code Authorization}.</p>
 *
 * <p>Коды ответов описаны аннотациями {@code @ApiResponses} над методами контроллеров,
 * а одинаковая структура тела ошибки задаётся здесь один раз.</p>
 */
@Configuration
public class OpenApiConfig {

    /** Имя схемы безопасности: то же имя указано в {@code @SecurityRequirement} контроллеров. */
    private static final String SECURITY_SCHEME_NAME = "basicAuth";

    /** Имя схемы единого формата ошибки в разделе Schemas. */
    private static final String ERROR_SCHEMA_NAME = "ErrorResponse";

    @Bean
    public OpenAPI tournamentOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Сервис организации спортивных турниров")
                        .version("1.0")
                        .description("""
                                REST API учебного проекта: пользователи, команды, турниры и матчи.
                                Все методы, кроме документации, требуют HTTP Basic (email и пароль пользователя).
                                Роли: ADMIN — полный доступ; MANAGER — команды и турниры; REFEREE — завершение матчей;
                                COACH — состав команды; CAPTAIN и PLAYER — просмотр данных.
                                """)
                        .contact(new Contact().name("Учебный проект, лабораторная работа №4")))
                // Схема "HTTP Basic": Swagger UI покажет кнопку Authorize.
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, basicAuthScheme())
                        .addSchemas(ERROR_SCHEMA_NAME, errorResponseSchema()))
                // Требование применено ко всем методам сразу.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    /**
     * Подключает схему единой ошибки ко всем ответам с кодом 4xx и 5xx.
     *
     * <p>Сами коды перечислены в {@code @ApiResponses} методов, а структура тела ошибки
     * задаётся здесь, чтобы не повторять её в каждой из 34 аннотаций.</p>
     */
    @Bean
    public OpenApiCustomizer errorResponseCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            openApi.getComponents().addSchemas(ERROR_SCHEMA_NAME, errorResponseSchema());
            if (openApi.getPaths() == null) {
                return;
            }
            Content errorContent = new Content().addMediaType(
                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/" + ERROR_SCHEMA_NAME)));
            for (PathItem pathItem : openApi.getPaths().values()) {
                for (Operation operation : allOperations(pathItem)) {
                    ApiResponses responses = operation.getResponses();
                    if (responses == null) {
                        continue;
                    }
                    responses.forEach((code, response) -> {
                        if (code.startsWith("4") || code.startsWith("5")) {
                            response.setContent(errorContent);
                        }
                    });
                }
            }
        };
    }

    /** Все операции одного пути: каждый HTTP-метод описан отдельным объектом. */
    private List<Operation> allOperations(PathItem pathItem) {
        return java.util.stream.Stream.of(pathItem.getGet(), pathItem.getPost(), pathItem.getPut(),
                        pathItem.getDelete(), pathItem.getPatch(), pathItem.getHead(), pathItem.getOptions())
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    /** Схема безопасности HTTP Basic. */
    private SecurityScheme basicAuthScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .description("Вход по email и паролю: используйте учётную запись из таблицы users");
    }

    /** Схема тела ошибки: совпадает с классом {@code com.tournament.exception.ErrorResponse}. */
    private Schema<?> errorResponseSchema() {
        return new ObjectSchema()
                .description("Единый формат ошибки REST API")
                .addProperty("timestamp", new StringSchema()
                        .description("Дата и время формирования ответа")
                        .example("2026-11-28T12:00:00"))
                .addProperty("status", new IntegerSchema()
                        .description("HTTP-код ответа")
                        .example(403))
                .addProperty("errorCode", new StringSchema()
                        .description("Машинный код ошибки")
                        .example("ACCESS_DENIED"))
                .addProperty("message", new StringSchema()
                        .description("Сообщение об ошибке")
                        .example("Недостаточно прав для выполнения операции"))
                .addProperty("suggestion", new StringSchema()
                        .description("Подсказка по исправлению ошибки")
                        .example("Обратитесь к администратору: операция доступна только пользователям с подходящей ролью"))
                .addProperty("details", new ObjectSchema()
                        .description("Дополнительные детали: ошибки полей, параметры запроса"));
    }
}
