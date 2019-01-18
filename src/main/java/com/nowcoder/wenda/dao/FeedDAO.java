package com.nowcoder.wenda.dao;

import com.nowcoder.wenda.model.Comment;
import com.nowcoder.wenda.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FeedDAO {

    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);

    @Select({" SELECT ",SELECT_FIELDS," FROM ",TABLE_NAME," WHERE id = #{id} "})
    Feed getFeedById(int id);

    //根据用户是否登录，要拉取的不同。如果未登录就不用userIds参数
    List<Feed> selectUserFeeds(@Param("maxId")int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);

}
