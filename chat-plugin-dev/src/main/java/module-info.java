module chat.bot.plugin.dev {
    requires kotlin.reflect;
    requires kotlin.stdlib;
    opens i.chat.dev.objects.container;
    exports i.chat.dev.objects.container;
    opens i.chat.dev.objects.manager;
    exports i.chat.dev.objects.manager;
}
