package xin.vanilla;

import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.jetbrains.annotations.NotNull;
import xin.vanilla.event.EventHandlers;

public final class MiraiConsoleDemo extends JavaPlugin {
    public static final MiraiConsoleDemo INSTANCE = new MiraiConsoleDemo();

    private MiraiConsoleDemo() {
        // 构建插件信息
        super(new JvmPluginDescriptionBuilder("xin.vanilla.mirai-demo", "0.1.0")
                .name("Mirai Console Demo")
                .author("Vanilla Xin")
                .build());
    }

    /**
     * 插件被加载事件
     * <p>
     * 在插件被加载时调用. 只会被调用一次.
     */
    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        super.onLoad($this$onLoad);
    }

    /**
     * 插件启用事件
     */
    @Override
    public void onEnable() {
        // 在此处进行事件监听, 对象创建等操作
        // 若要执行简单的异步、延迟、重复任务，可使用 getScheduler() 获取到简单任务调度器。例子:
        // 延时
        getScheduler().delayed(1000L, () -> getLogger().info("插件已在一秒前加载完成!"));

        // 注册事件监听
        GlobalEventChannel.INSTANCE.registerListenerHost(new EventHandlers());
    }

    /**
     * 插件禁用事件
     */
    @Override
    public void onDisable() {
        // 插件创建的所有线程或异步任务都需要在此处关闭！！！！！

        super.onDisable();
    }
}
