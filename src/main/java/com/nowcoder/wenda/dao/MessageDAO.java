package com.nowcoder.wenda.dao;

import com.nowcoder.wenda.model.Comment;
import com.nowcoder.wenda.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {

    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, created_date, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{fromId},#{toId},#{content},#{hasRead},#{createdDate},#{conversationId})"})
    int addMessage(Message message);

    @Select({"SELECT ", SELECT_FIELDS , " FROM " , TABLE_NAME,
            " WHERE conversation_id = #{conversationId} ORDER BY created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"SELECT ", INSERT_FIELDS , " , count(id) as id FROM ( SELECT * FROM " , TABLE_NAME,
            " where from_id = #{userId} or to_id = #{userId} order by created_date DESC) " +
                    "tt group by conversation_id order by created_date desc limit #{offset}, #{limit}"
            })
    List<Message> getConversationList(@Param("userId") int userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"SELECT count(id) FROM ",TABLE_NAME,
            " WHERE has_read=0 AND to_id=#{userId} AND conversation_id=#{conversationId} "})
    int getConversationUnreadCount(@Param("userId") int userId,
                                   @Param("conversationId") String conversationId
                                   );
}
