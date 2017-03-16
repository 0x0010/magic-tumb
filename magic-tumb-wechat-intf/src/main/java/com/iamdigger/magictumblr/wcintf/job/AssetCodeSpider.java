package com.iamdigger.magictumblr.wcintf.job;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.constant.AssetState;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.HttpUtil;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class AssetCodeSpider {

  private static Logger logger = LoggerFactory.getLogger(AssetCodeSpider.class);

  @Resource(name = "MTE")
  private ThreadPoolTaskExecutor executor;

  @Resource
  private MagicAssetService assetService;

  @Scheduled(fixedDelay = 1000)
  public void scheduleInitAsset() {
    List<MagicAssetDO> initAssets = assetService.queryAsset(AssetState.INIT.getState(), 0, 5);
    if (null != initAssets && initAssets.size() > 0) {
      logger.info("Find {} init assets.", initAssets.size());
      for (MagicAssetDO initAsset : initAssets) {
        assetService.updateAssetState(initAsset.getId(), AssetState.PROCESSING.getState());
        logger.info("Asset[{}] update state to [{}]", initAsset.getOriginalUrl(),
            AssetState.PROCESSING.getDesc());
        executor
            .submit(new SpiderTask(initAsset.getId(), initAsset.getOriginalUrl(), assetService));
        logger.info("Asset[{}] has been submitted to MTE", initAsset.getOriginalUrl(),
            AssetState.PROCESSING.getDesc());
      }
    }
  }

  private static class SpiderTask implements Runnable {

    private Long id;
    private String url;
    private MagicAssetService assetService;

    SpiderTask(Long id, String url, MagicAssetService assetService) {
      this.id = id;
      this.url = url;
      this.assetService = assetService;
    }

    @Override
    public void run() {
      AssetState state = AssetState.SUCCESS;
      try {
        String urlContent = HttpUtil.doGet(url);
        int ogImageStart = urlContent.indexOf("<meta property=\"og:image\"");
        int ogImageMetaEnd = urlContent.indexOf("<meta", ogImageStart + 1);
        if (ogImageStart > 0 && ogImageMetaEnd > 0 && ogImageMetaEnd > ogImageStart) {
          String videoCode = urlContent.substring(ogImageStart, ogImageMetaEnd).split("_")[1];
          assetService.updateAssetVideoCode(id, videoCode);
          logger.info("Get video code [{}] from Url[{}]", videoCode, url);
        } else {
          state = AssetState.UNSUPPORTED_URL;
          logger.info("Unsupported url [{}]", url);
        }
      } catch (Exception e) {
        state = AssetState.FAILED;
        logger.error(String.format("Parse URL[%s] failed.", url), e);
      } finally {
        assetService.updateAssetState(id, state.getState());
        logger.info("Asset[{}] update state to [{}]", url, state.getDesc());
      }
    }
  }
}
