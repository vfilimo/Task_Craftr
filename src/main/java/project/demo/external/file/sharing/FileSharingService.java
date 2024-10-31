package project.demo.external.file.sharing;

import java.nio.file.Path;

public interface FileSharingService {
    String uploadAttachment(String uploadFilePath);

    Path downloadFile(String fileId);
}
