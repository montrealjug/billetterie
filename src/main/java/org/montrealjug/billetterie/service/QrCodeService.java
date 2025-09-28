// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import uk.org.okapibarcode.backend.QrCode;
import uk.org.okapibarcode.graphics.Color;
import uk.org.okapibarcode.output.Java2DRenderer;

/**
 * Service for generating QrCodes from text / urls
 */
@Service
public class QrCodeService {

    public byte[] generateQrCode(String text) throws IOException {
        QrCode qrCode = new QrCode();
        qrCode.setContent(text);

        int width = qrCode.getWidth();
        int height = qrCode.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = image.createGraphics();
        Java2DRenderer renderer = new Java2DRenderer(g2d, 1, Color.WHITE, Color.BLACK);
        renderer.render(qrCode);

        BufferedImage resizedImage = resizeImage(image, 300, 300);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpeg", os);
        return os.toByteArray();
    }

    // from https://www.baeldung.com/java-resize-image
    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(
            targetWidth - 20,
            targetHeight - 20,
            Image.SCALE_DEFAULT
        );
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        var g2d = outputImage.getGraphics();
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(0, 0, targetWidth, targetHeight);
        g2d.drawImage(resultingImage, 10, 10, null);
        return outputImage;
    }
}
