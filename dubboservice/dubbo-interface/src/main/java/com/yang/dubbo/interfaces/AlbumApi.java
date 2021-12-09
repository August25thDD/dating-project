package com.yang.dubbo.interfaces;


import com.yang.commons.pojo.Album;

/**
 * @项目名称: hztanhua
 * @包名: com.itheima.dubbo.interfaces
 * @作者: 李云飞
 * @日期: 2021-12-06
 */
public interface AlbumApi {
    /**
     * 发布朋友圈同步保存到自己相册表
     *
     * @param album
     * @param userid
     * @return
     */
    void saveAlbum(Album album, Long userid);
}
