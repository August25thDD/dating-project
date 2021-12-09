package com.yang.appserver.service;

import com.alibaba.dubbo.config.annotation.Reference;

import com.yang.appserver.interceptor.UserThreadLocal;
import com.yang.commons.pojo.Album;
import com.yang.dubbo.interfaces.AlbumApi;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

/**
 * @项目名称: tanhua-53
 * @包名: com.itheima.appserver.service
 * @作者: 李云飞
 * @日期: 2021-12-06
 */

@Service
public class AlbumService {
    @Reference
    private AlbumApi albumApi;

    // 保存到自己的相册表
    public void save(String publishId) {
        Album album = new Album();
        album.setId(ObjectId.get());
        album.setCreated(System.currentTimeMillis());
        album.setPublishId(new ObjectId(publishId));
        Long userId = UserThreadLocal.getUserId();
        albumApi.saveAlbum(album, userId);
    }
}
