package com.iamdigger.magictumblr.wcintf.job;

import com.google.common.io.PatternFilenameFilter;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class AssetFilesScanner {

  private static Logger logger = LoggerFactory.getLogger(AssetFilesScanner.class);

  @Resource
  private MagicAssetService magicAssetService;

  @Bean
  public TaskExecutor scannerExecutor() {
    return new SimpleAsyncTaskExecutor("AFScanner");
  }

  @Bean
  public CommandLineRunner schedulingScanner(TaskExecutor taskExecutor) {
    return (String... args) -> {
      taskExecutor.execute(
          () -> {
            while (true) {
              Set<String> assetFiles = listTxFiles();
              if (null == assetFiles || assetFiles.size() <= 0) {
                continue;
              }
              for (String assetFile : assetFiles) {
                BufferedReader reader = null;
                try {
                  Path assetFilePath = new File(
                      String.format("%s/%s", RuntimeUtil.getRunningPath(), assetFile)).toPath();
                  reader = Files.newBufferedReader(assetFilePath, Charset.forName("UTF-8"));
                  String assetCode = reader.readLine();
                  String url = reader.readLine();
                  magicAssetService.createMagicAsset(assetCode, url);
                  Files.deleteIfExists(assetFilePath);
                  logger.info("SAVED!! URL[{}], AssetCode[{}]", url, assetCode);
                } catch (IOException ignore) {
                  logger.error(String.format("Read asset file %s failed.", assetFile), ignore);
                } finally {
                  try {
                    if (null != reader) {
                      reader.close();
                    }
                  } catch (IOException ignore) {
                  }
                }
              }
              try {
                Thread.sleep(1000);
              } catch (InterruptedException ignore) {
              }
            }
          }
      );
    };
  }


  private Set<String> listTxFiles() {
    try {
      File txFilePath = new File(RuntimeUtil.getRunningPath());
      String[] assetFiles = txFilePath.list(new PatternFilenameFilter(".*\\.mt"));
      if (null != assetFiles && assetFiles.length > 0) {
        Set<String> resultAssets = new HashSet<>();
        for (int i = 0, len = assetFiles.length; i < len; i++) {
          if (i >= 5) {
            break;
          }
          resultAssets.add(assetFiles[i]);
        }
        return resultAssets;
      }
      return null;
    } catch (Exception ignore) {
      logger.error("Scan asset files failed", ignore);
      return null;
    }
  }
}
