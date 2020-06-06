package com.nsperkins.pi.controller

import com.nsperkins.pi.video.IRaspivid
import com.nsperkins.pi.video.VideoStreamHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.io.OutputStream
import java.util.*

@Controller
class VideoController(
        private val vidHandler: VideoStreamHandler,
        private val raspivid: IRaspivid
) {

    @PostMapping("/video/stream/start")
    fun startVid() {
        raspivid.start()
    }

    @PostMapping("/video/stream/stop")
    fun stopVid() {
        raspivid.stop()
    }

    @GetMapping("/video")
    fun getFullVideo(): ResponseEntity<UrlResource> {

        val video = UrlResource("file:test_video.mp4")
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory
                        .getMediaType(video)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(video)
    }

    @GetMapping("/video/stream")
    fun getVidStream(): ResponseEntity<OutputStream> {
        val key = UUID.randomUUID()
        try {
            val output = vidHandler.subscribe(key)
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM) //video/mp4 ?
                    .body(output)
        } finally {
            vidHandler.remove(key)
        }

    }
}
