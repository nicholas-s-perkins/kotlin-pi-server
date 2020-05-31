package com.nsperkins.pi.controller

import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class VideoController {
    @GetMapping("/video")
    fun getFullVideo(): ResponseEntity<UrlResource> {

        val video = UrlResource("file:test_video.mp4")
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory
                        .getMediaType(video)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(video)
    }
}
