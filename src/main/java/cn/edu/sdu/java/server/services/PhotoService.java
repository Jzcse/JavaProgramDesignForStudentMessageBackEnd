package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhotoService {
    @Value("${app.external-resources-dir}")
    private String uploadDir;

    private final String port = "22222";

    // localhost 的静态资源URL
    private final String hostBaseURL = "http://localhost:" + port;

    public DataResponse upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "personId") String personId){
        DataResponse dataResponse = new DataResponse();

        if(personId == null || personId.isEmpty()){
            dataResponse.setMsg("error for empty person id!");
            dataResponse.setCode(0);
            return dataResponse;
        }
        try{
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);
            String filename = personId + ".jpg";
            Path targetPath = dirPath.resolve(filename).normalize();

            if(!targetPath.startsWith(dirPath)){
                throw new SecurityException("Invalid file path");
            }
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        // 为每个文件生成独一的文件名
//        String fileName = personId + ".jpg";
//        // 实际的存储路径
//        String filePath = jarUrl + "/static-resources/";
//        String realFilePath = filePath + fileName;
//        System.out.println(realFilePath);
//        try {
//            // 判断是否已经有此路径，如果没有，则新建该目录
//            if (!FileUtil.isDirectory(filePath)) {
//                try{
//                    FileUtil.mkdir(filePath);
//                } catch (Exception e){
//                    System.out.println("Error to create directory" + e.getMessage());
//                    dataResponse.setCode(1);
//                    dataResponse.setMsg("创建文件夹失败!");
//                    return dataResponse;
//                }
//            }
//            FileUtil.writeBytes(file.getBytes(), realFilePath);
//        } catch (IOException e){
//            System.out.println("Error to upload photo file");
//            dataResponse.setCode(1);
//            dataResponse.setMsg("上传文件失败!");
//            return dataResponse;
//        }
        String url = hostBaseURL + "/" + personId + ".jpg";
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        dataResponse.setCode(0);
        dataResponse.setMsg("successful upload");
        dataResponse.setData(map);
        return dataResponse;
    }

    public DataResponse downloadTemp(DataRequest dataRequest){
        DataResponse dataResponse = new DataResponse();
        String personId = dataRequest.getString("personId");
        dataResponse.setCode(0);
        dataResponse.setData(personId + ".jpg");
        dataResponse.setMsg("successfully request!");
        return dataResponse;
    }

    public DataResponse download(DataRequest dataRequest) {
        String personId = dataRequest.getString("personId");
        DataResponse dataResponse = new DataResponse();

        if(personId == null || personId.isEmpty()){
            dataResponse.setMsg("error for empty person id!");
            dataResponse.setCode(0);
            return dataResponse;
        }

        try {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            String filename = personId + ".jpg";
            Path targetPath = dirPath.resolve(filename).normalize();

            if(!targetPath.startsWith(dirPath)){
                throw new SecurityException("Invalid file path");
            }

            if (!Files.exists(targetPath)) {
                dataResponse.setMsg("File not found!");
                dataResponse.setCode(1);
                return dataResponse;
            }

            byte[] fileBytes = FileUtil.readBytes(targetPath);
            String base64Data = java.util.Base64.getEncoder().encodeToString(fileBytes);
            Map<String, String> map = new HashMap<>();
            map.put("file", base64Data);
            dataResponse.setCode(0);
            dataResponse.setMsg("successfully download!");
            dataResponse.setData(map);
        } catch (Exception e) {
            dataResponse.setMsg("Error to download photo file");
            dataResponse.setCode(1);
            throw new RuntimeException(e);
        }

        return dataResponse;
    }

    public DataResponse getBaseUrl(DataRequest dataRequest){
        DataResponse dataResponse = new DataResponse();
        dataResponse.setCode(0);
        dataResponse.setData(hostBaseURL);
        return dataResponse;
    }
}
