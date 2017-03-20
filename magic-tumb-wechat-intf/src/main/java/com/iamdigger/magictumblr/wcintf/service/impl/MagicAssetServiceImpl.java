package com.iamdigger.magictumblr.wcintf.service.impl;

import com.iamdigger.magictumblr.wcintf.bean.MagicAssetDO;
import com.iamdigger.magictumblr.wcintf.constant.AssetState;
import com.iamdigger.magictumblr.wcintf.service.interfaces.MagicAssetService;
import com.iamdigger.magictumblr.wcintf.utils.RuntimeUtil;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
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
        "select asset_id, video_code, asset_content, create_time, state from magic_asset where asset_id = ?",
        new Object[]{assetId},
        (ResultSet rs) -> {
          if (rs.first()) {
            MagicAssetDO magicAsset = new MagicAssetDO();
            magicAsset.setVideoCode(rs.getString(2));
            magicAsset.setAssetId(rs.getString(1));
            magicAsset.setAssetContent(rs.getString(3));
            magicAsset.setCreateTime(rs.getTimestamp(4));
            magicAsset.setState(rs.getInt(5));
            return magicAsset;
          }
          return null;
        });
    return null != asset ? asset : null;
  }

  @Transactional
  @Override
  public void createMagicAsset(String assetId, String committer, String url) {
    String sql = "insert into magic_asset (asset_id, committer, asset_hash, asset_content, create_time, state) values (?,?,?,?,?,?)";
    jdbcTemplate.update(sql, assetId, committer,
        DigestUtils.md5DigestAsHex(url.getBytes(Charset.forName("UTF-8"))),
        url,
        new Timestamp(RuntimeUtil.getCurrentDateTime().getTime()),
        AssetState.INIT.getState());
  }

  @Override
  public List<MagicAssetDO> queryAsset(Integer state, Integer start, Integer limit) {
    String sql = "SELECT id, asset_id, asset_content FROM MAGIC_ASSET where state = ? order by asset_id limit ?, ?";
    return jdbcTemplate.query(sql, new Object[]{state, start, limit},
        (ResultSet rs, int rowNum) -> {
          MagicAssetDO mad = new MagicAssetDO();
          mad.setId(rs.getLong(1));
          mad.setAssetId(rs.getString(2));
          mad.setAssetContent(rs.getString(3));
          return mad;
        }
    );
  }

  @Transactional
  @Override
  public void updateAssetState(Long id, Integer state) {
    String sql = "update MAGIC_ASSET set state = ? where id = ?";
    jdbcTemplate.update(sql, state, id);
  }

  @Transactional
  @Override
  public void updateAssetVideoCode(Long id, String videoCode) {
    String sql = "update magic_asset set video_code = ? where id = ?";
    jdbcTemplate.update(sql, videoCode, id);
  }

  @Override
  public List<MagicAssetDO> queryAssetByCommitter(String committer, Integer start, Integer count) {
    String sql = "SELECT asset_content, video_code, image_code, create_time, state FROM MAGIC_TUMBLR.MAGIC_ASSET where committer =? order by id desc limit ?, ?";
    return jdbcTemplate.query(sql, new Object[]{committer, start, count},
        (ResultSet rs, int rowNum) -> {
          MagicAssetDO mad = new MagicAssetDO();
          mad.setAssetContent(rs.getString(1));
          mad.setVideoCode(rs.getString(2));
          mad.setImageCode(rs.getString(3));
          mad.setCreateTime(rs.getTimestamp(4));
          mad.setState(rs.getInt(5));
          return mad;
        }
    );
  }

  @Override
  public List<MagicAssetDO> queryTop5Asset() {
    String sql = "SELECT video_code, count(video_code) as count FROM MAGIC_ASSET where video_code is not null and video_code != '' group by video_code order by count desc limit 0, 5";
    return jdbcTemplate.query(sql,
        (ResultSet rs, int rowNum) -> {
          MagicAssetDO mad = new MagicAssetDO();
          mad.setVideoCode(rs.getString(1));
          return mad;
        }
    );
  }
}
