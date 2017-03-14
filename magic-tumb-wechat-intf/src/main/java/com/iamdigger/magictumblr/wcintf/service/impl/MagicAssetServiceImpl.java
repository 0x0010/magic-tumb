package com.iamdigger.magictumblr.wcintf.service.impl;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import java.sql.ResultSet;
import javax.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sam
 * @since 3.0.0
 */
@Component
public class MagicAssetServiceImpl implements MagicAssetService {

  @Resource
  private JdbcTemplate jdbcTemplate;

  @Transactional
  @Override
  public MagicAssetDO queryMagicAsset(String assetId) {
    MagicAssetDO asset = jdbcTemplate.query(
        "select asset_id, asset_code, original_url, create_time, state from magic_asset where asset_id = ?",
        new Object[]{assetId},
        (ResultSet rs) -> {
          if (null != rs && rs.next()) {
            MagicAssetDO magicAsset = new MagicAssetDO();
            magicAsset.setAssetCode(rs.getString(2));
            return magicAsset;
          }
          return null;
        });
    return null != asset ? asset : null;
  }
}
