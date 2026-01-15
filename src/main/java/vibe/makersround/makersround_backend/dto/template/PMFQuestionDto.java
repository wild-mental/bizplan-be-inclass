package vibe.makersround.makersround_backend.dto.template;

import java.util.List;

public record PMFQuestionDto(
        String id,
        String question,
        List<PMFQuestionOptionDto> options
) {}
