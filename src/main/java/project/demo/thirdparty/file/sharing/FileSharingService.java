package project.demo.thirdparty.file.sharing;

public interface FileSharingService {
    String uploadAttachment(String uploadFilePath);

    String downloadFile(String fileId);
}
