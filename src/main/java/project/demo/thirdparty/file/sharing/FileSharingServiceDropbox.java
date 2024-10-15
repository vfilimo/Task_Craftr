package project.demo.thirdparty.file.sharing;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileSharingServiceDropbox implements FileSharingService {
    private static final String PATH_SEPARATOR = "/";
    private static final String LABEL_SEPARATOR = "_";
    @Value("${dropbox.access-token}")
    private String accessToken;

    @Value("${dropbox.client-identifier}")
    private String clientIdentifier;

    @Value("${file.download-folder}")
    private String localDownloadFolder;

    @Override
    public String uploadAttachment(String uploadFilePath) {
        DbxClientV2 client = createClient();
        Path path = pathNormalizer(uploadFilePath);
        try (InputStream in = new FileInputStream(path.toString());) {
            FileMetadata metadata = client.files()
                    .uploadBuilder(PATH_SEPARATOR + path.getFileName())
                    .uploadAndFinish(in);
            return metadata.getId();
        } catch (IOException | DbxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String downloadFile(String fileId) {
        DbxClientV2 client = createClient();
        try {
            ListFolderResult result = client.files().listFolder("");
            FileMetadata foundMetadata = findMetadataById(result, fileId);
            String dropboxFileName = foundMetadata.getName();
            Path createdFilePath = downloadingFilePathCreate(dropboxFileName);

            try (InputStream in = client.files().download(foundMetadata.getPathLower())
                    .getInputStream()) {
                Files.copy(in, createdFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (DbxException | IOException e) {
                throw new RuntimeException(e);
            }
            return createdFilePath.toString();
        } catch (DbxException e) {
            throw new RuntimeException(e);
        }
    }

    private DbxClientV2 createClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(clientIdentifier).build();
        return new DbxClientV2(config, accessToken);
    }

    private FileMetadata findMetadataById(ListFolderResult result, String id) {
        for (Metadata metadata : result.getEntries()) {
            if (metadata instanceof FileMetadata) {
                FileMetadata fileMetadata = (FileMetadata) metadata;
                if (id.equals(fileMetadata.getId())) {
                    return fileMetadata;
                }
            }
        }
        throw new RuntimeException("Can't find file with id: " + id);
    }

    private Path pathNormalizer(String filePath) {
        Path path = Paths.get(filePath);
        return path.normalize();
    }

    private Path downloadingFilePathCreate(String dropboxFileName) {
        String currentDate = IntStream.of(
                        LocalDateTime.now().getMonthValue(),
                        LocalDateTime.now().getDayOfMonth(),
                        LocalDateTime.now().getHour(),
                        LocalDateTime.now().getMinute(),
                        LocalDateTime.now().getSecond())
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(LABEL_SEPARATOR));
        return Path.of(localDownloadFolder + PATH_SEPARATOR
                + currentDate + LABEL_SEPARATOR + dropboxFileName);
    }
}
