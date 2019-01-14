package com.nowcoder.wenda.dao;

import com.nowcoder.wenda.model.Comment;
import com.nowcoder.wenda.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {

    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, entity_id, entity_type, created_date, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{userId},#{content},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addComment(Comment comment);

    @Select({"SELECT ", SELECT_FIELDS , " FROM " , TABLE_NAME,
            " WHERE entity_id = #{entityId} and entity_type = #{entityType} ORDER BY created_date desc "})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId,
                                         @Param("entityType") int entityType);

    @Select({"SELECT count(id) FROM ", TABLE_NAME, " WHERE entity_id = #{entityId} and entity_type = #{entityType} "})
    int getCommentCount(@Param("entityId") int entityId,
                        @Param("entityType") int entityType);

    @Update({"UPDATE comment set status = #{status} WHERE id=#{id}"})
    int updateStatus(@Param("id") int id, @Param("status") int status);

    @Select({" SELECT ",SELECT_FIELDS," FROM ",TABLE_NAME," WHERE id = #{id} "})
    Comment selectById(int id);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
}
