package com.woori.ajs.imgmock.service;

import com.woori.ajs.imgmock.config.ImgMockProperties;
import com.woori.ajs.imgmock.model.DocPageInfo;
import com.woori.ajs.imgmock.model.DocType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageRenderService {

    private static final Logger log = LoggerFactory.getLogger(ImageRenderService.class);
    private static final Pattern TAIL_CODE = Pattern.compile("(0[1-9]|1[0-3]|99)$");

    private static final int WIDTH = 800;
    private static final int HEIGHT = 1100;

    private final ImgMockProperties properties;
    private Path imageRoot;
    private Path cacheRoot;

    public ImageRenderService(ImgMockProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() throws IOException {
        Path configured = Paths.get(properties.getImageDir());
        if (!configured.isAbsolute()) {
            configured = Paths.get(System.getProperty("user.dir")).resolve(configured);
        }
        imageRoot = configured;
        cacheRoot = imageRoot.resolve("cache");
        Files.createDirectories(cacheRoot);
        log.info("Image root: {}", imageRoot.toAbsolutePath());
    }

    public String renderBase64(String taskId, String mstSrno) throws IOException {
        byte[] jpeg = loadOrRender(taskId, mstSrno);
        return Base64.getEncoder().encodeToString(jpeg);
    }

    private byte[] loadOrRender(String taskId, String mstSrno) throws IOException {
        String safeTaskId = sanitize(taskId);
        String safeMstSrno = sanitize(mstSrno);

        Path cacheFile = cacheRoot.resolve(safeTaskId + ".jpg");
        if (properties.isCacheEnabled() && Files.isRegularFile(cacheFile)) {
            return Files.readAllBytes(cacheFile);
        }

        byte[] staticImage = readStaticImage(safeTaskId);
        if (staticImage != null) {
            if (properties.isCacheEnabled()) {
                Files.write(cacheFile, staticImage);
            }
            return staticImage;
        }

        byte[] rendered = renderJpeg(safeTaskId, safeMstSrno);
        if (properties.isCacheEnabled()) {
            Files.write(cacheFile, rendered);
        }
        return rendered;
    }

    private byte[] readStaticImage(String taskId) throws IOException {
        for (String name : new String[] {taskId + ".jpg", taskId + ".jpeg"}) {
            Path path = imageRoot.resolve(name);
            if (Files.isRegularFile(path)) {
                return Files.readAllBytes(path);
            }
        }
        return null;
    }

    private byte[] renderJpeg(String taskId, String mstSrno) throws IOException {
        DocPageInfo info = parseDocInfo(taskId, mstSrno);
        DocType docType = info.getDocType();

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setColor(new Color(248, 250, 252));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(docType.getAccent());
            g.fillRect(0, 0, WIDTH, 72);

            Font titleFont = resolveFont(Font.BOLD, 28);
            Font subFont = resolveFont(Font.PLAIN, 20);
            Font bodyFont = resolveFont(Font.PLAIN, 16);
            Font smallFont = resolveFont(Font.PLAIN, 13);

            g.setFont(titleFont);
            g.setColor(Color.WHITE);
            g.drawString("TEST IMAGE — " + docType.getLabel(), 24, 46);

            g.setFont(subFont);
            g.setColor(new Color(30, 41, 59));
            g.drawString("심사마스터  INPT_MST_SRNO = " + mstSrno, 24, 96);
            g.drawString("이미지 Task  INPT_TASK_ID = " + taskId, 24, 132);

            g.setFont(bodyFont);
            g.setColor(new Color(71, 85, 105));
            g.drawString("문서분류  IMEX_HIS_CD = " + docType.getCode() + "  |  페이지 " + info.getPage(), 24, 168);

            g.setColor(new Color(203, 213, 225));
            g.setStroke(new BasicStroke(2f));
            g.drawRect(24, 210, WIDTH - 48, HEIGHT - 290);

            g.setFont(bodyFont);
            g.setColor(new Color(51, 65, 85));
            String[] lines = {
                    "AI 심사 상세 — Spring Boot Mock 이미지 서버",
                    "",
                    "imgRest → img.url → POST /sa/g001",
                    "",
                    "문서: " + docType.getLabel(),
                    "마스터: " + mstSrno,
                    "Task: " + taskId,
                    "",
                    "TotalText / 항목심사 좌표 테스트용",
                    "가이드 라인 (800 x 1100)"
            };
            int y = 240;
            for (String line : lines) {
                g.drawString(line, 48, y);
                y += 28;
            }

            g.setColor(new Color(226, 232, 240));
            for (int gy = 320; gy < HEIGHT - 100; gy += 48) {
                g.drawLine(48, gy, WIDTH - 48, gy);
            }

            g.setColor(docType.getAccent());
            g.setStroke(new BasicStroke(3f));
            g.drawRect(120, 420, 500, 100);
            g.setFont(smallFont);
            g.drawString("Sample highlight region", 132, 450);

            g.setColor(new Color(148, 163, 184));
            g.drawString("img-mock-springboot  POST /sa/g001", 24, HEIGHT - 32);
        } finally {
            g.dispose();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return out.toByteArray();
    }

    DocPageInfo parseDocInfo(String taskId, String mstSrno) {
        if (taskId.startsWith(mstSrno)) {
            String suffix = taskId.substring(mstSrno.length());
            for (DocType type : DocType.values()) {
                if (type == DocType.OTHER) {
                    continue;
                }
                if (suffix.startsWith(type.getCode())) {
                    String page = suffix.substring(type.getCode().length());
                    return new DocPageInfo(type, page.isEmpty() ? "1" : page);
                }
            }
            if (suffix.length() >= 2) {
                DocType type = DocType.fromCode(suffix.substring(0, 2));
                if (type != DocType.OTHER) {
                    String page = suffix.length() > 2 ? suffix.substring(2) : "1";
                    return new DocPageInfo(type, page.isEmpty() ? "1" : page);
                }
            }
        }

        Matcher matcher = TAIL_CODE.matcher(taskId);
        if (matcher.find()) {
            DocType type = DocType.fromCode(matcher.group(1));
            return new DocPageInfo(type, "1");
        }

        return new DocPageInfo(DocType.OTHER, "1");
    }

    private Font resolveFont(int style, int size) {
        String[] candidates = {
                "Malgun Gothic",
                "NanumGothic",
                "AppleGothic",
                Font.SANS_SERIF
        };
        for (String name : candidates) {
            Font font = new Font(name, style, size);
            if (Font.SANS_SERIF.equals(name) || font.getFamily().equalsIgnoreCase(name)
                    || "Malgun Gothic".equals(name)) {
                return font;
            }
        }
        return new Font(Font.SANS_SERIF, style, size);
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("[^0-9A-Za-z_\\-]", "");
    }
}
