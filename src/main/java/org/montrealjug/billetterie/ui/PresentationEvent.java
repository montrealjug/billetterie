// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public record PresentationEvent(
        Long id,
        String title,
        String description,
        LocalDate date,
        List<PresentationActivity> activities,
        Boolean active,
        @Nullable String imagePath) {}
