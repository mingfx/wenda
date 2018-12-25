package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.model.HostHolder;
import com.nowcoder.wenda.model.Message;
import com.nowcoder.wenda.model.User;
import com.nowcoder.wenda.model.ViewObject;
import com.nowcoder.wenda.service.MessageService;
import com.nowcoder.wenda.service.UserService;
import com.nowcoder.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String getConversationList(Model model){
        if (hostHolder.getUser()==null){
            return "redirect://reglogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationList =messageService.getConversationList(localUserId,0,10);
        List<ViewObject> conversations =new ArrayList<ViewObject>();
        for (Message conversation:conversationList){
            ViewObject vo = new ViewObject();
            //对话显示的肯定是对方的头像，既不一定是fromId也不一定是toId
            int targetId = conversation.getFromId()==localUserId ? conversation.getToId():conversation.getFromId();
            User user =  userService.getUser(targetId);
            vo.set("user",user);
            vo.set("conversation",conversation);
            vo.set("unread",messageService.getConversationUnreadCount(localUserId,conversation.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId){
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            for (Message message:messageList){
                ViewObject vo = new ViewObject();
                User user = userService.getUser(message.getFromId());
                vo.set("user",user);
                vo.set("message",message);
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        } catch (Exception e) {
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.POST})
    //因为是js提交，所以返回json串
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try {
            if (hostHolder.getUser()==null){
                return WendaUtil.getJSONString(999,"未登录");
            }else {
                User user = userService.selectByName(toName);
                if (user==null){
                    return WendaUtil.getJSONString(1,"用户不存在");
                }

                Message message = new Message();
                message.setCreatedDate(new Date());
                message.setFromId(hostHolder.getUser().getId());
                message.setToId(user.getId());
                message.setContent(content);
                messageService.addMessage(message);
                return  WendaUtil.getJSONString(0);//0表示成功
            }
        }catch (Exception e){
            logger.error("发送消息失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"发信失败");
        }
    }
}
