package com.yang.dubbo.mongodb.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.yang.commons.pojo.Album;
import com.yang.dubbo.interfaces.AlbumApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

@Service
public class AlbumApiImpl implements AlbumApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveAlbum(Album album, Long userid) {
        mongoTemplate.save(album, "quanzi_album_" + userid);
    }
}
