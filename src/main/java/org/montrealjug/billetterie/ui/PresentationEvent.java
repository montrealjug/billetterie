package org.montrealjug.billetterie.ui;

import java.time.LocalDate;
import java.util.List;

public record PresentationEvent(
        Long id,
        String title,
        String description,
        LocalDate date,
        List<PresentationActivity> activities,
        Boolean active) {}
