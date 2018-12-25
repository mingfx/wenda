package com.nowcoder.wenda.dao;

import com.nowcoder.wenda.model.Question;
import com.nowcoder.wenda.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDAO {

    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME, "(", INSERT_FIELDS,
            ") values(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    @Select({" SELECT ",SELECT_FIELDS," FROM ",TABLE_NAME," WHERE id = #{id} "})
    Question selectById(int id);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"UPDATE ",TABLE_NAME, " SET comment_count = #{commentCount} WHERE id = #{id}"})
    int updateCommentCount(@Param("id") int id,
                           @Param("commentCount") int commentCount);

}
