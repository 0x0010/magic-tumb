package com.iamdigger.magictumblr.wcintf.job;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.constant.AssetState;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.HttpUtil;
import java.net.MalformedURLException;
import java.net.URL;
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

  @Scheduled(fixedDelay = 1500)
  public void scheduleInitAsset() {
    List<MagicAssetDO> initAssets = assetService.queryAsset(AssetState.INIT.getState(), 0, 5);
    if (null != initAssets && initAssets.size() > 0) {
      logger.info("Find {} init assets.", initAssets.size());
      for (MagicAssetDO initAsset : initAssets) {
        assetService.updateAssetState(initAsset.getId(), AssetState.PROCESSING.getState());
        logger.info("Asset[{}] update state to [{}]", initAsset.getAssetContent(),
            AssetState.PROCESSING.randomDesc());
        executor.submit(new SpiderTask(initAsset.getId(), initAsset.getAssetContent(), assetService));
        logger.info("Asset[{}] submit to MTE", initAsset.getAssetContent());
      }
    }
  }

  private static class SpiderTask implements Runnable {

    private Long id;
    private String assetContent;
    private MagicAssetService assetService;

    SpiderTask(Long id, String assetContent, MagicAssetService assetService) {
      this.id = id;
      this.assetContent = assetContent;
      this.assetService = assetService;
    }

    @Override
    public void run() {
      AssetState state = AssetState.SUCCESS;

      // 是否url
      URL url = null;
      try {
        url = new URL(this.assetContent);
      } catch (MalformedURLException e) {
        state = AssetState.NOT_URL;
      }

      if (null == url) {
        assetService.updateAssetState(id, state.getState());
        logger.info("Asset[{}] update state to [{}]", assetContent, state.randomDesc());
        return;
      }

      // 是URL，尝试解析
      try {
        String urlContent = HttpUtil.doGet(assetContent);
        int ogImageStart = urlContent.indexOf("<meta property=\"og:image\"");
        int ogImageMetaEnd = urlContent.indexOf("<meta", ogImageStart + 1);
        if (ogImageStart > 0 && ogImageMetaEnd > 0 && ogImageMetaEnd > ogImageStart) {
          String videoCode = urlContent.substring(ogImageStart, ogImageMetaEnd).split("_")[1];
          assetService.updateAssetVideoCode(id, videoCode);
          logger.info("Get video code [{}] from Url[{}]", videoCode, url);
        } else {
          state = AssetState.UNSUPPORTED_URL;
        }
      } catch (Exception e) {
        state = AssetState.FAILED;
        logger.error(String.format("Parse URL[%s] failed.", url), e);
      }
      assetService.updateAssetState(id, state.getState());
      logger.info("Asset[{}] update state to [{}]", url, state.randomDesc());
    }
  }
}
