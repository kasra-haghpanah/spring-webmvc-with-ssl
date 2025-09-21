package org.application.spring.ddd.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.exception.ApplicationException;
import org.application.spring.configuration.exception.ErrorResponse;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.configuration.security.AuthenticationRequest;
import org.application.spring.configuration.security.AuthenticationResponse;
import org.application.spring.ddd.dto.FileDto;
import org.application.spring.ddd.model.entity.Customer;
import org.application.spring.ddd.model.entity.File;
import org.application.spring.ddd.service.CustomerService;
import org.application.spring.ddd.service.FileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@Validated
//@RequestMapping("/api/auth")
public class RestController {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final RestClient restClient;
    private final CustomerService customerService;
    private final FileService fileService;

    public RestController(
            MessageSource messageSource,
            LocaleResolver localeResolver,
            @Qualifier("secureRestClient") RestClient restClient,
            CustomerService customerService,
            FileService fileService
    ) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.restClient = restClient;
        this.customerService = customerService;
        this.fileService = fileService;
    }

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    @ResponseBody
    public List<Customer> customers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return customerService.findAll(pageable)
                .stream()
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    @ResponseBody
    public List<FileDto> files(@RequestParam("file") List<String> files) {
        // /files?file=A,B,C,D
        return fileService.findByOwnerList(files);
    }

    @RequestMapping(value = "/download/{fileId}", method = RequestMethod.GET)
    @Operation(summary = "دانلود فایل", description = "دانلود فایل با نام مشخص‌شده")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "فایل با موفقیت ارسال شد"),
            @ApiResponse(responseCode = "404", description = "فایل یافت نشد")
    })
    public void download(
            @PathVariable(value = "fileId") String fileId,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Locale locale = localeResolver.resolveLocale(request);
        File file = fileService.getById(fileId);

        if (file == null) {

            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType("application/json;charset=UTF-8");

            Map<String, String> map = new HashMap<>();
            map.put("error", messageSource.getMessage("address.file.invalid", new Object[]{fileId}, locale));

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus(HttpStatus.NOT_FOUND);
            errorResponse.setErrors(map);

            response.getWriter().write(errorResponse.toString());
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        Long sizeFile = Long.valueOf(file.getContent().length); // برحسب بایت
        // تعیین MIME type
        // Files.probeContentType() از سیستم‌عامل برای تشخیص MIME استفاده می‌کنه. اگه دقیق نبود، می‌تونی از کتابخونه‌هایی مثل Apache Tika استفاده کنی.
        String mimeType = file.getType(); //Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // پیش‌فرض
        }

        // اگر HTML بود، charset رو روی UTF-8 ست کن
        if (mimeType.indexOf("text") > -1 || mimeType.equals("json") || mimeType.equals("xml")) {
            mimeType = mimeType + "; charset=UTF-8";
        }

        boolean inlineTypes = mimeType.startsWith("text/") ||
                mimeType.startsWith("image/") ||
                mimeType.startsWith("application/pdf") ||
                mimeType.startsWith("application/json") ||
                mimeType.indexOf("video/") > -1 ||
                mimeType.indexOf("audio/") > -1;

        String dispositionType = inlineTypes ? "inline" : "attachment";
        dispositionType = "inline";

        // نام فایل با پشتیبانی از UTF-8
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

        String contentDisposition = String.format(
                "%s; filename=\"%s\"; filename*=UTF-8''%s",
                dispositionType, file.getName(), encodedFileName
        );

        response.setContentType(mimeType); // یا نوع MIME مناسب مثل "application/pdf"
        response.setHeader("Content-Disposition", contentDisposition);
        response.setContentLength(sizeFile.intValue());


        // خواندن فایل به صورت بایت

        try (InputStream in = new ByteArrayInputStream(file.getContent());
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }


    }


    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public String handleUpload(
            @RequestPart(value = "firstName", required = true) String firstName,
            @RequestPart(value = "lastName", required = true) String lastName,
            @RequestPart(value = "phoneNumber", required = true) String phoneNumber,
            //@RequestParam Map<String, String> formParams, // همه‌ی پارامترهای فرم
            @RequestPart(value = "files", required = false) List<MultipartFile> files // لیست فایل‌ها
    ) throws IOException {
        // 🔍 نمایش پارامترهای فرم
        /*formParams.forEach((key, value) -> {
            System.out.println("Form Param: " + key + " = " + value);
        });*/

//        System.out.println("firstName: = " + firstName);
//        System.out.println("lastName: = " + lastName);
//        System.out.println("phoneNumber: = " + phoneNumber);

        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhoneNumber(phoneNumber);

        customer = customerService.save(customer);

        if (files != null) {
            List<File> fileList = new ArrayList<>();
            for (MultipartFile f : files) {
                File file = new File();
                file.setName(f.getOriginalFilename());
                file.setType(f.getContentType());
                file.setOwnerId(customer.getId());
                file.setContent(f.getBytes());
                fileList.add(file);
            }
            List<File> saveAll = fileService.saveAll(fileList);
            System.out.println("saveAll = " + saveAll.size());
        }

        return "دریافت شد!";
    }


    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);

        model.addAttribute("title", messageSource.getMessage("upload.title", new Object[]{}, locale));

        model.addAttribute("version", Properties.getVersion());
        return "upload"; // فایل unauthorized.html در مسیر templates
    }


    @RequestMapping(
            value = "/restclient/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public String handleUploadAsRestClient(
            @RequestPart(value = "firstName", required = true) String firstName,
            @RequestPart(value = "lastName", required = true) String lastName,
            @RequestPart(value = "phoneNumber", required = true) String phoneNumber,
            //@RequestParam Map<String, String> formParams, // همه‌ی پارامترهای فرم
            @RequestPart(required = false) List<MultipartFile> files, // لیست فایل‌ها
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        MultiValueMap<String, Object> multipartBody = new LinkedMultiValueMap<>();
        multipartBody.add("firstName", firstName);
        multipartBody.add("lastName", lastName);
        multipartBody.add("phoneNumber", phoneNumber);

        // افزودن فایل‌ها (در صورت وجود)
        //Path filePath = Paths.get("path/to/file.txt");
        //Resource fileResource = new FileSystemResource(filePath);
        //multipartBody.add("files", fileResource); // می‌توانید چند فایل اضافه کنید
        files.stream().forEach(file -> {
            try {
                ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename(); // نام فایل برای ارسال
                    }
                };
                multipartBody.add("files", resource); // کلید باید "files" باشد چون در @RequestPart همین نام استفاده شده
            } catch (IOException e) {
                throw new RuntimeException("خطا در خواندن فایل: " + file.getOriginalFilename(), e);
            }
        });


        String result = restClient.post()
                .uri("https://localhost:8443/spring" + "/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", Optional.ofNullable(request.getHeader("Authorization"))
                        .map(String::trim)
                        .orElseGet(() -> Optional.ofNullable(request.getCookies())
                                .map(Arrays::stream)
                                .orElseGet(Stream::empty)
                                .filter(cookie -> cookie.getName().equals("access_token") && !cookie.getValue().equals(""))
                                .findFirst()
                                .map(cookie -> "Bearer " + cookie.getValue())
                                .orElse("")
                        )
                )
                .body(multipartBody)
                .exchange((clientRequest, clientResponse) -> {

                    if (clientResponse.getStatusCode().isError()) {
                        throw new ApplicationException("url.invalid", HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()), null);
                    }
                    clientResponse.getHeaders()
                            .forEach((key, values) -> {
                                if (values != null) {
                                    for (String value : values) {
                                        response.addHeader(key, value);
                                    }
                                }
                            });
                    return clientResponse.bodyTo(String.class);
                });

        return result;

    }

    @RequestMapping(value = "/restclient/login", method = RequestMethod.POST)
    @ResponseBody
    public AuthenticationResponse login(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ) {
        AuthenticationResponse result = restClient.post()
                .uri("https://localhost:8443/spring/login")
                .header("Accept-Language", "fa")
                .body(authenticationRequest)
                .exchange((clientRequest, clientResponse) -> {

                    if (clientResponse.getStatusCode().isError()) {
                        throw new ApplicationException("url.invalid", HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()), null);
                    }
                    clientResponse.getHeaders()
                            .forEach((key, values) -> {
                                if (values != null) {
                                    for (String value : values) {
                                        response.addHeader(key, value);
                                    }
                                }
                            });
                    return clientResponse.bodyTo(AuthenticationResponse.class);
                });

        return result;

    }


    @RequestMapping(value = "/resource/{version}", method = RequestMethod.GET)
    @ResponseBody
    public void resource(
            @Valid @PathVariable("version") @Pattern(regexp = "(\\/)*((\\d){1,2})\\.((\\d){1,2})\\.((\\d){1,2})(.)*") String version,
            @RequestParam("path") String path,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        byte[] buffer = restClient.get()
                .uri("https://localhost:8443/spring/" + path)
                .header("Authorization",
                        Optional.ofNullable(request.getHeader("Authorization"))
                                .map(String::trim)
                                .orElseGet(() -> "")
                )
                .cookie("access_token",
                        Optional.ofNullable(request.getCookies())
                                .map(Arrays::stream)
                                .orElseGet(Stream::empty)
                                .filter(cookie -> "access_token".equals(cookie.getName()))
                                .findFirst()
                                .map(Cookie::getValue)
                                .orElse("")
                )
                .exchange((clientRequest, clientResponse) -> {


                    if (clientResponse.getStatusCode().isError()) {
                        throw new ApplicationException("url.invalid", HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()), null);
                    }
                    clientResponse.getHeaders()
                            .forEach((key, values) -> {
                                if (values != null) {
                                    for (String value : values) {
                                        response.addHeader(key, value);
                                    }
                                }
                            });
                    return clientResponse.bodyTo(byte[].class);


                });

        OutputStream out = response.getOutputStream();
        if (buffer != null) {
            out.write(buffer);
        }
        out.flush();

        //ServerUtil.setCacheForBrowser(response, 7 * 24);


    }

}
