package com.nowcoder.wenda.service;

import com.nowcoder.wenda.controller.QuestionController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine())!=null){
                addWord(lineTxt.trim());
            }
            reader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }

    //增加关键词
    private void addWord(String lineTxt){
        TrieNode tmpNode = rootNode;
        for(int i = 0;i<lineTxt.length();++i){
            Character c = lineTxt.charAt(i);

            if (isSymbol(c)){
                continue;
            }

            TrieNode node = tmpNode.getSubNode(c);

            if(node==null){
                node  = new TrieNode();
                tmpNode.addSubNode(c,node);
            }
            tmpNode = node;

            if(i==lineTxt.length()-1){
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    private  class TrieNode{
        //是不是关键词的结尾
        private boolean end = false;

        //当前节点下的所有子节点
        private Map<Character,TrieNode> subNodes = new HashMap<Character,TrieNode>();

        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd(){return end;}

        void setKeywordEnd(boolean end){
            this.end = end;
        }
    }

    //空的根节点
    private TrieNode rootNode = new TrieNode();

    //如果既不是英文也不是东亚文字，认为是非法符号
    private boolean isSymbol(char c){
        int ic = (int)c;
        //东亚文字范围0x2E80-0x9FFF
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2E80||ic>0x9FFF);
    }

    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return text;
        }

        StringBuilder result = new StringBuilder();
        String replacement = "***";
        TrieNode tmpNode = rootNode;
        int begin = 0;
        int position = 0;
        while (position<text.length()){
            char c = text.charAt(position);

            if (isSymbol(c)){
                if (tmpNode==rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tmpNode = tmpNode.getSubNode(c);
            if (tmpNode==null){
                result.append(text.charAt(begin));
                position = begin+1;
                begin = position;
                tmpNode = rootNode;
            }else if (tmpNode.isKeywordEnd()){
                //发现敏感词
                //此处不替换就是删除敏感词
                result.append(replacement);
                begin = position+1;
                position = begin;
                tmpNode = rootNode;
            }else {
                ++position;
            }
        }
        //加上最后一次处理的
        result.append(text.substring(begin));
        return result.toString();
    }

//    public static void main(String[] argv){
//        SensitiveService sensitiveService = new SensitiveService();
//        sensitiveService.addWord("色情");
//        sensitiveService.addWord("赌博");
//        System.out.println(sensitiveService.filter("hi  你好色 情"));
//    }
}
