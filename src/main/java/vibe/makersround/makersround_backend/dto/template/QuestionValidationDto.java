package vibe.makersround.makersround_backend.dto.template;

public record QuestionValidationDto(
        Integer min,
        Integer max,
        String pattern
) {}
