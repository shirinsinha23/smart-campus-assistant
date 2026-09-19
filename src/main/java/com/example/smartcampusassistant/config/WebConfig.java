package com.example.smartcampusassistant.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    /**
     * Absolute base directory of the project on this machine.
     * Trailing slash is intentionally absent — we add it when building the location.
     */
    private static final String PROJECT_ROOT =
            "C:/Users/shirin sinha/IdeaProjects/smart-campus-assistant";

    private void registerUpload(ResourceHandlerRegistry registry,
                                String urlPattern,
                                String absoluteDir) {
        File dir = new File(absoluteDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.warn("⚠️ Upload dir did not exist, created={}: {}", created, dir.getAbsolutePath());
        }
        // Spring needs a trailing slash on file: locations
        String location = "file:" + dir.getAbsolutePath().replace('\\', '/') + "/";
        log.info("🔗 Mapping {} → {}", urlPattern, location);

        registry.addResourceHandler(urlPattern)
                .addResourceLocations(location)
                .setCachePeriod(0)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        log.warn("❌ Static file not found: {} (looked in {})", resourcePath, location);
                        return null;
                    }
                });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registerUpload(registry, "/uploads/events/**",
                PROJECT_ROOT + "/uploads/events");
        registerUpload(registry, "/uploads/course-materials/**",
                PROJECT_ROOT + "/uploads/course-materials");
        registerUpload(registry, "/uploads/lost-items/**",
                PROJECT_ROOT + "/uploads/lost-items");
        registerUpload(registry, "/uploads/placement/**",
                PROJECT_ROOT + "/uploads/placement");
        // ← NEW: question papers for assignments
        registerUpload(registry, "/uploads/assignments/**",
                PROJECT_ROOT + "/uploads/assignments");
        registerUpload(registry, "/uploads/leaves/**",
                PROJECT_ROOT + "/uploads/leaves");

        // Static SPA files LAST
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}