package com.iamdigger.magictumblr.wcintf.job;

import com.iamdigger.magictumblr.wcintf.cache.Top5AssetCache;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class Top5AssetLoader {

  private static Logger logger = LoggerFactory.getLogger(Top5AssetLoader.class);

  @Resource
  private MagicAssetService magicAssetService;

  @Resource
  private Top5AssetCache top5AssetCache;

  @Scheduled(cron = "0 */1 * * * *")
  public void reloadTop5Asset() {
    try {
      top5AssetCache.reloadData(magicAssetService.queryTop5Asset());
      logger.info("Top 5 asset has been reloaded to [{}] @[{}]", top5AssetCache.getDataSet(),
          RuntimeUtil.getCurrentDateTime());
    } catch (Exception e) {
      logger.error("Reload top 5 asset failed.", e);
    }
  }
}
