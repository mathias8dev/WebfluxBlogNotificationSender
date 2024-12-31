package com.mathias8dev.webfluxblognotificationsender.utils

import org.apache.tika.detect.Detector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.apache.tika.parser.AutoDetectParser
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

object MimeTypes {

    fun getRealMimeType(file: MultipartFile): String {
        val parser = AutoDetectParser()
        val detector: Detector = parser.detector
        return try {
            val metadata = Metadata()
            val stream: TikaInputStream = TikaInputStream.get(file.inputStream)
            val mediaType: MediaType = detector.detect(stream, metadata)
            mediaType.toString()
        } catch (e: IOException) {
            org.apache.tika.mime.MimeTypes.OCTET_STREAM
        }
    }
}