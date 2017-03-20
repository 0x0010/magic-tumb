package com.iamdigger.magictumblr.wcintf.service.interfaces;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import java.util.List;

/**
 * @author Sam
 * @since 3.0.0
 */
public interface MagicAssetService {

  MagicAssetDO queryMagicAsset(String assetId);

  void createMagicAsset(String assetId, String commiter, String url);

  List<MagicAssetDO> queryAsset(Integer state, Integer start, Integer limit);

  List<MagicAssetDO> queryAssetByCommitter(String committer, Integer start, Integer count);

  void updateAssetState(Long id, Integer state);

  void updateAssetVideoCode(Long id, String videoCode);

  List<MagicAssetDO> queryTop5Asset();
}
