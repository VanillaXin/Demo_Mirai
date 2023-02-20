package xin.vanilla.event;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ExceptionInEventHandlerException;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public class EventHandlers extends SimpleListenerHost {
    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        // 处理事件处理时抛出的异常
        Event event = ((ExceptionInEventHandlerException) exception).getEvent();

        exception = getBaseException(exception);

        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;

            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            exception.printStackTrace(writer);
            StringBuffer buffer = stringWriter.getBuffer();

            // 将异常信息发送至群
            groupMessageEvent.getGroup().sendMessage(getByLine(buffer.toString(), 1, 5, "... [num] more"));
        }
    }

    /**
     * 群消息事件
     */
    @EventHandler
    public void onGroupMessage(@NotNull GroupMessageEvent event) {
        MessageChain message = event.getMessage();
        Group group = event.getGroup();
        Member sender = event.getSender();
        // 判断收到的消息是 1 则发送 [艾特发送者]2
        if (message.contentToString().equals("1")) {
            group.sendMessage(
                    // 构建MessageChain
                    new MessageChainBuilder()
                            // 艾特发送者
                            .append(new At(sender.getId()))
                            .append("2")
                            .build());
        }
        // 判断收到的消息是 2 则引用回复 3
        else if (message.contentToString().equals("2")) {
            // 构造引用回复
            QuoteReply quoteReply = new QuoteReply(message);
            group.sendMessage(
                    // 构建MessageChain
                    new MessageChainBuilder()
                            // 回复原消息
                            .append(quoteReply)
                            .append("3")
                            .build());
        }
        // 判断收到的消息是 3 则私信 4
        else if (message.contentToString().equals("3")) {
            sender.sendMessage("4");
        }
        // 判断收到的消息是 4 则戳一戳发送者
        else if (message.contentToString().equals("4")) {
            sender.nudge().sendTo(group);
        }
        // 判断收到的消息是 5 则私信戳一戳发送者
        else if (message.contentToString().equals("5")) {
            sender.nudge().sendTo(sender);
        }
        // 判断收到的消息是 6
        else if (message.contentToString().equals("5")) {
            // 且机器人是管理员或群主
            if (group.getBotPermission().getLevel() > MemberPermission.MEMBER.getLevel()) {
                // 则撤回该消息
                MessageSource.recall(message);
            }
        }
    }

    /**
     * 好友消息事件
     */
    @EventHandler
    public void onFriendMessage(@NotNull FriendMessageEvent event) {
        // 监听好友消息

    }

    /**
     * 群临时会话消息事件
     */
    @EventHandler
    public void onGroupTempMessage(@NotNull GroupTempMessageEvent event) {

    }

    /**
     * 陌生人消息事件
     */
    @EventHandler
    public void onStrangerMessage(@NotNull StrangerMessageEvent event) {

    }

    /**
     * 其他客户端消息事件
     */
    @EventHandler
    public void onOtherClientMessage(@NotNull OtherClientMessageEvent event) {

    }

    /**
     * 消息撤回事件
     */
    @EventHandler
    public void onMessageRecall(@NotNull MessageRecallEvent event) {

    }

    /**
     * 获取最底层的异常
     */
    private Throwable getBaseException(Throwable exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            cause = exception.getCause();
            if (cause != null) exception = cause;
        }
        return exception;
    }

    /**
     * 根据行号截取字符串
     * <p>(开始堆粪</p>
     *
     * @param suffix 如果结尾还有内容, 是否需要添加的后缀, 例: "后面还有[num]行"
     */
    private String getByLine(String s, int start, int end, String suffix) {
        if (start > end) return s;
        String code;
        if (s.contains("\r\n")) code = "\r\n";
        else if (s.contains("\r")) code = "\r";
        else if (s.contains("\n")) code = "\n";
        else return s;

        String[] split = s.split(code);
        if (start > split.length) return s;
        if (end >= split.length) {
            StringBuilder back = new StringBuilder();
            for (int i = start - 1; i < split.length; i++) {
                if (i != start - 1) back.append(code);
                back.append(split[i]);
            }
            return back.toString();
        }

        StringBuilder back = new StringBuilder();
        for (int i = start - 1; i < end; i++) {
            if (i != start - 1) back.append(code);
            back.append(split[i]);
        }
        if (!"".equals(suffix))
            back.append(code).append(suffix.replace("[num]", split.length - end + ""));
        return back.toString();
    }
}
