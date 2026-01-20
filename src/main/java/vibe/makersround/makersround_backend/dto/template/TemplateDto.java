package vibe.makersround.makersround_backend.dto.template;

import java.util.List;

public record TemplateDto(
        String id,
        String name,
        String description,
        String icon,
        List<String> features
) {}
