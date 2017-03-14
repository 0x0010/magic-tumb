package com.iamdigger.magictumblr.wcintf.service.impl;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.constant.AssetState;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class MagicAssetServiceImpl implements MagicAssetService {

  @Resource
  private JdbcTemplate jdbcTemplate;

  @Override
  public MagicAssetDO queryMagicAsset(String assetId) {
    MagicAssetDO asset = jdbcTemplate.query(
        "select asset_id, video_code, original_url, create_time, state from magic_asset where asset_id = ?",
        new Object[]{assetId},
        (ResultSet rs) -> {
          if (null != rs && rs.next()) {
            MagicAssetDO magicAsset = new MagicAssetDO();
            magicAsset.setVideoCode(rs.getString(2));
            return magicAsset;
          }
          return null;
        });
    return null != asset ? asset : null;
  }

  @Transactional
  @Override
  public void createMagicAsset(String assetId, String url) {
    String sql = "insert into magic_asset (asset_id, url_hash, original_url, create_time, state) values (?,?,?,?,?)";
    jdbcTemplate.update(sql,
        assetId,
        DigestUtils.md5DigestAsHex(url.getBytes(Charset.forName("UTF-8"))),
        url,
        new Timestamp(RuntimeUtil.getCurrentDateTime().getTime()),
        AssetState.INIT.getState());
  }
}
