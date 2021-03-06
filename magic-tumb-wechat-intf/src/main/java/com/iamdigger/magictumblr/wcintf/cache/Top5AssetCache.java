package com.iamdigger.magictumblr.wcintf.cache;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class Top5AssetCache {

  private static Logger logger = LoggerFactory.getLogger(Top5AssetCache.class);
  private static volatile boolean isReloading = false;

  private List<String> backupData = new ArrayList<>(5);
  private List<String> data = new ArrayList<>(5);

  @Resource
  private MagicAssetService magicAssetService;

  public List<String> getDataSet() {
    if (isReloading) {
      return backupData;
    }
    return data;
  }

  public void reloadData(List<MagicAssetDO> top5Mad) {
    backupData = data;
    isReloading = true;
    data.clear();
    if (null != top5Mad) {
      for (MagicAssetDO mad : top5Mad) {
        data.add(mad.getVideoCode());
      }
    }
    isReloading = false;
  }

  @PostConstruct
  public void cacheStartup() {
    reloadData(magicAssetService.queryTop5Asset());
    logger.info("Top 5 asset has been loaded into memory while startup, content [{}] @ [{}]",
        getDataSet(),
        RuntimeUtil.getCurrentDateTime());
  }
}
