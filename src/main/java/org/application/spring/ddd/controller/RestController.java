package org.application.spring.ddd.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.exception.ErrorResponse;
import org.application.spring.ddd.service.MailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


import java.io.*;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@Validated
//@RequestMapping("/api/auth")
public class RestController {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final RestClient restClient;

    public RestController(
            MessageSource messageSource,
            LocaleResolver localeResolver,
            @Qualifier("secureRestClient") RestClient restClient
    ) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.restClient = restClient;
    }


    //keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -keystore client.jks -storepass changeit -validity 365 -dname "CN=Kasra, OU=Dev, O=MyCompany, L=Tehran, ST=Tehran, C=IR"

    //keytool -exportcert -alias client -keystore client.jks -file client.crt -storepass changeit


    @RequestMapping(value = "/download/{filename:.+}", method = RequestMethod.GET)
    @Operation(summary = "دانلود فایل", description = "دانلود فایل با نام مشخص‌شده")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "فایل با موفقیت ارسال شد"),
            @ApiResponse(responseCode = "404", description = "فایل یافت نشد")
    })
    public void download(
            @PathVariable String filename,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Locale locale = localeResolver.resolveLocale(request);
        // برای امنیت بیشتر، حتماً مسیر فایل رو normalize کن تا از حملات path traversal جلوگیری بشه.
        Path filePath = Paths.get("files").resolve(filename).normalize();

        String path = MailService.class.getResource("").getPath();// favicon.ico
        path = MessageFormat.format("{0}/static/images/{1}", path.substring(0, path.indexOf("/classes") + 8), filePath.getFileName());
        File file = new File(path);


        //
        if (!file.exists()) {

            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType("application/json;charset=UTF-8");

            Map<String, String> map = new HashMap<>();
            map.put("", messageSource.getMessage("address.file.invalid", new Object[]{filename}, locale));

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus(HttpStatus.NOT_FOUND);
            errorResponse.setErrors(map);

            response.getWriter().write(errorResponse.toString());
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // تعیین MIME type
        // Files.probeContentType() از سیستم‌عامل برای تشخیص MIME استفاده می‌کنه. اگه دقیق نبود، می‌تونی از کتابخونه‌هایی مثل Apache Tika استفاده کنی.
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // پیش‌فرض
        }


        //byte[] fileBytes = Files.readAllBytes(file.toPath());

        // تنظیم هدرها
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));

        // اگر HTML بود، charset رو روی UTF-8 ست کن
        if (mimeType.indexOf("text") > -1 || mimeType.equals("application/json")) {
            headers.setContentType(MediaType.parseMediaType(mimeType + "; charset=UTF-8"));
        }

        Long sizeFile = file.length(); // برحسب بایت

        response.setContentType(mimeType); // یا نوع MIME مناسب مثل "application/pdf"
        response.setHeader("Content-Disposition", MessageFormat.format("attachment; filename=\"{0}\"", filename));
        response.setContentLength(sizeFile.intValue());

        // خواندن فایل به صورت بایت

        InputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();

        byte[] buffer = new byte[sizeFile.intValue()];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();


    }


    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public String handleUpload(
            @RequestPart(value = "username", required = true) String username,
            @RequestPart(value = "password", required = true) String password,
            //@RequestParam Map<String, String> formParams, // همه‌ی پارامترهای فرم
            @RequestPart(required = false) List<MultipartFile> files // لیست فایل‌ها
    ) {
        // 🔍 نمایش پارامترهای فرم
        /*formParams.forEach((key, value) -> {
            System.out.println("Form Param: " + key + " = " + value);
        });*/

        System.out.println("username: = " + username);
        System.out.println("password: = " + password);

        // 📦 نمایش اطلاعات فایل‌ها
        if (files != null) {
            for (MultipartFile file : files) {
                System.out.println("File: " + file.getOriginalFilename() +
                        " (" + file.getSize() + " bytes)");
                // می‌تونی فایل رو ذخیره کنی یا پردازش کنی
            }
        }

        return "دریافت شد!";
    }


    @RequestMapping(value = "/resource/{version}/**", method = RequestMethod.GET)
    @ResponseBody
    public void resource(
            @Valid @PathVariable("version") @Pattern(regexp = "(\\/)*((\\d){1,2})\\.((\\d){1,2})\\.((\\d){1,2})(.)*") String version,
            HttpServletResponse response
    ) {
        restClient.get()
                .uri("https://localhost:8443/spring/favicon.ico")
                .retrieve()
                .body(byte[].class);
    }

}
