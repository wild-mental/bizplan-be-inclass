package vibe.makersround.makersround_backend.dto.template;

import java.util.List;

public record WizardQuestionDto(
        String id,
        String type,
        String label,
        String description,
        String placeholder,
        boolean required,
        List<QuestionOptionDto> options,
        QuestionValidationDto validation
) {}
