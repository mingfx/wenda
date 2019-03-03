package com.nowcoder.wenda.service;

import com.nowcoder.wenda.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private static final String SOLR_URL = "http://localhost:8983/solr/wenda";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";


    /**
     * 需要用到solrj，具体可以看wiki。
     * 需要构造一个solr-client，设置参数。查询，解析参数，返回
     * @param hlPre  高亮前缀
     * @param hlPos
     * @return
     */
    public List<Question> searchQuestion(String keyword,int offset,int count,
                                         String hlPre,String hlPos) throws Exception {
        List<Question> questionList = new ArrayList<Question>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD);

        QueryResponse response = client.query(query);
        //SolrDocumentList results = response.getResults();
        //解析结果中的highlight部分
        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()) {
            Question question = new Question();
            question.setId(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    question.setContent(contentList.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (titleList.size() > 0) {
                    question.setTitle(titleList.get(0));
                }
            }
            questionList.add(question);
        }

        return questionList;
    }

    //发表新问题的时候，要把搜索服务中的索引加上
    public boolean indexQuestion(int qid,String title,String content) throws Exception{
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id",qid);
        document.setField(QUESTION_TITLE_FIELD,title);
        document.setField(QUESTION_CONTENT_FIELD,content);
        UpdateResponse response = client.add(document,1000);
        return response!=null&&response.getStatus()==0;
    }
}
