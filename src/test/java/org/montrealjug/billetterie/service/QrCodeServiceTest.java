// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class QrCodeServiceTest {

    private final QrCodeService qrCodeService = new QrCodeService();

    @Test
    void generateQrCode() throws IOException {
        var url = "https://qrcode.test.org";
        var imageBytes = qrCodeService.generateQrCode(url);
        var expectedBytes = Files.readAllBytes(Paths.get("src/test/resources/qrcode/expected-qr-code.jpg"));
        assertThat(imageBytes).isEqualTo(expectedBytes);
    }
}
