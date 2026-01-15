package vibe.makersround.makersround_backend.dto.template;

import java.util.List;

public record WizardStepDto(
        int id,
        String title,
        String description,
        String icon,
        String status,
        List<WizardQuestionDto> questions
) {}
