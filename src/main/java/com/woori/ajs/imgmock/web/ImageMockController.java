package com.woori.ajs.imgmock.web;

import com.woori.ajs.imgmock.dto.ImageRequest;
import com.woori.ajs.imgmock.dto.ImageResponse;
import com.woori.ajs.imgmock.service.ImageRenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ImageMockController {

    private static final Logger log = LoggerFactory.getLogger(ImageMockController.class);

    private final ImageRenderService imageRenderService;

    public ImageMockController(ImageRenderService imageRenderService) {
        this.imageRenderService = imageRenderService;
    }

    /**
     * 운영 CA 이미지 서버와 동일한 엔드포인트.
     */
    @PostMapping("/sa/g001")
    public ResponseEntity<?> getImage(@RequestBody ImageRequest request) throws IOException {
        if (request == null
                || !StringUtils.hasText(request.getInptTaskId())
                || !StringUtils.hasText(request.getInptMstSrno())) {
            return ResponseEntity.badRequest().body(error("inpt_task_id and inpt_mst_srno are required"));
        }

        String taskId = request.getInptTaskId().trim();
        String mstSrno = request.getInptMstSrno().trim();
        log.info("image request mstSrno={} taskId={}", mstSrno, taskId);

        String imgStr = imageRenderService.renderBase64(taskId, mstSrno);
        return ResponseEntity.ok(new ImageResponse(imgStr));
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("status", "UP");
        body.put("endpoint", "POST /sa/g001");
        return body;
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new LinkedHashMap<String, String>();
        body.put("error", message);
        return body;
    }
}
