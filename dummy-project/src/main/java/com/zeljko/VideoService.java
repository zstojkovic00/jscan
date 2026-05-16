package com.zeljko;

import com.zeljko.videoservice.service.VideoService;
import com.zeljko.videoservice.controller.dto.VideoMetadata;
import com.zeljko.videoservice.controller.dto.VideoMetadataRequest;
import com.zeljko.videoservice.controller.dto.StreamBytesInfo;
import com.zeljko.videoservice.repository.model.Video;
import com.zeljko.videoservice.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zeljko.videoservice.utils.Utils.removeFileExt;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@Service
public class VideoServiceImpl implements VideoService {
    @Value("${data.folder}")
    private String dataFolder;

    private final VideoRepository videoRepository;
    private final FrameGrabberService frameGrabberService;
    private final static Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

    public VideoServiceImpl(VideoRepository videoRepository, FrameGrabberService frameGrabberService) {
        this.videoRepository = videoRepository;
        this.frameGrabberService = frameGrabberService;
    }

    @Override
    public List<VideoMetadata> findAllVideoMetadata() {
        return videoRepository.findAll().stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VideoMetadata> findById(String id) {
        return videoRepository.findById(id).map(this::convert);
    }

    @Transactional
    @Override
    public void saveNewVideo(VideoMetadataRequest request) throws IOException {
        Video v = new Video();
        v.setFileName(request.file().getOriginalFilename());
        v.setContentType(request.file().getContentType());
        v.setFileSize(request.file().getSize());
        v.setDescription(request.description());
        v.setGenre(request.genre());

        videoRepository.save(v);

        if (!Path.of(dataFolder).toFile().exists()) {
            Files.createDirectory(Path.of(dataFolder));
        }

        Path directory = Path.of(dataFolder, v.getId());

        try {

            Files.createDirectory(directory);
            assert request.file().getOriginalFilename() != null;
            Path file = Path.of(directory.toString(), request.file().getOriginalFilename());
            try (var os = Files.newOutputStream(file, CREATE, WRITE)) {
                request.file().getInputStream().transferTo(os);
            }
            long videoLength = frameGrabberService.generatePreviewPictures(file);
            v.setVideoLength(videoLength);
            videoRepository.save(v);

        } catch (IOException e) {
            log.error("Error: {}", e.getMessage(), e);
            throw new IllegalStateException();
        }
    }

    @Override
    public Optional<InputStream> getVideoThumbnail(String id) {
        return videoRepository.findById(id)
                .flatMap(vmd -> {
                    Path previewPicturePath = Path.of(dataFolder,
                            vmd.getId(),
                            removeFileExt(vmd.getFileName()) + ".jpeg");
                    if (!Files.exists(previewPicturePath)) {
                        return Optional.empty();
                    }
                    try {
                        return Optional.of(Files.newInputStream(previewPicturePath));
                    } catch (IOException e) {
                        log.error("Error: {}", e.getMessage(), e);
                        return Optional.empty();
                    }
                });
    }


    @Override
    public Optional<StreamBytesInfo> getStreamBytes(String id, HttpRange range) {
        Optional<Video> byId = videoRepository.findById(id);
        if (byId.isEmpty()) {
            return Optional.empty();
        }
        Path filePath = Path.of(dataFolder, id, byId.get().getFileName());
        if (!Files.exists(filePath)) {
            log.error("File {} not found", filePath);
            return Optional.empty();
        }
        try {
            long fileSize = Files.size(filePath);
            long chunkSize = fileSize / 100;
            if (range == null) {
                return Optional.of(new StreamBytesInfo(
                        out -> Files.newInputStream(filePath).transferTo(out),
                        fileSize, 0, fileSize, byId.get().getContentType()));
            }

            long rangeStart = range.getRangeStart(0);
            long rangeEnd = rangeStart + chunkSize; // range.getRangeEnd(fileSize);
            if (rangeEnd >= fileSize) {
                rangeEnd = fileSize - 1;
            }
            long finalRangeEnd = rangeEnd;
            return Optional.of(new StreamBytesInfo(
                    os -> {
                        try (InputStream inputStream = Files.newInputStream(filePath)) {
                            inputStream.skip(rangeStart);
                            byte[] bytes = inputStream.readNBytes((int) ((finalRangeEnd - rangeStart) + 1));
                            os.write(bytes);
                        }
                    },
                    fileSize, rangeStart, rangeEnd, byId.get().getContentType()));
        } catch (IOException e) {
            log.error("Exception: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    public VideoMetadata convert(Video v) {
        return new VideoMetadata(
                v.getId(),
                v.getDescription(),
                v.getContentType(),
                "videos/preview/" + v.getId(),
                "videos/stream/" + v.getId(),
                v.getGenre()
        );
    }


}
