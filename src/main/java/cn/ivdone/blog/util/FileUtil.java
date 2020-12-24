package cn.ivdone.blog.util;

import java.io.File ;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    /**
     *
     * @param file 二进制文件对象
     * @param path 目标路径
     * @param filename 保存的文件名
     * @throws IOException
     */
    public static void fileUpload(byte[] file, String path, String filename) throws IOException {
        // 判断文件是否存在 不存在则新建
        File target = new File(path) ;
        if (!target.exists()){
            target.mkdirs();
        }
        // 向磁盘写入文件
        FileOutputStream out = new FileOutputStream(path+filename) ;
        out.write(file);
        out.flush();
        out.close();

    }
}
