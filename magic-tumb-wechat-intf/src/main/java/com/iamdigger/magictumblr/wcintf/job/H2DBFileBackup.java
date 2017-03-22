package com.iamdigger.magictumblr.wcintf.job;

import com.google.common.io.Files;
import com.iamdigger.magictumblr.wcintf.constant.QiniuAccess;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * backup h2 database file
 *
 * @author Sam
 * @since 3.0.0
 */
@Component
@EnableConfigurationProperties({QiniuAccess.class})
public class H2DBFileBackup {

  private Logger logger = LoggerFactory.getLogger(H2DBFileBackup.class);

  @Resource
  private QiniuAccess qiniuAccess;

  private UploadManager um;
  private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

  @Scheduled(fixedDelay = 60 * 60 * 1000)
  public void backupH2DbFile() {
    if (!qiniuAccess.getEnableBackup()) {
      return;
    }
    String dbFile = qiniuAccess.getDbUrl().split(";")[0].split(":")[2] + ".mv.db";
    // i dont know why ~ is not work for me
    if (dbFile.startsWith("~")) {
      dbFile = RuntimeUtil.getUserHome() + dbFile.substring(1, dbFile.length());
    }
    File h2dbFile = new File(dbFile),
        uploadFile = new File(dbFile + "." + dtf.format(LocalDateTime.now()));
    try {
      if (Files.isFile().apply(h2dbFile) && h2dbFile.canRead()) {
        Files.copy(h2dbFile, uploadFile);
      } else {
        logger.info("h2 database file [{}] not exists or can not be read.", dbFile);
        return;
      }
      String token = Auth.create(qiniuAccess.getAccessKey(), qiniuAccess.getSecretKey())
          .uploadToken(qiniuAccess.getBucket());
      Response response = um.put(uploadFile, uploadFile.getName(), token);
      logger.info("h2 database backup result {}", response.statusCode);
    } catch (Exception e) {
      logger.error("upload h2 database file failed.", e);
    } finally {
      try {
        java.nio.file.Files.deleteIfExists(uploadFile.toPath());
      } catch (IOException ignore) {
      }
    }
  }

  @PostConstruct
  private void initUploadManager() {
    um = new UploadManager(new Configuration());
    logger.info("qiniu upload manager initialized success.");
  }
}
