package dev.huntbot.jobs;

import dev.huntbot.HuntBotApp;
import dev.huntbot.api.util.Configured;
import dev.huntbot.util.generators.IconImageGenerator;
import dev.huntbot.util.logging.Log;
import dev.huntbot.util.resource.ResourceUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import org.quartz.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ImageChangeJob implements Job, Configured {
    @Getter private final static JobDetail job = JobBuilder.newJob(ImageChangeJob.class).build();
    @Getter private final static Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * *").inTimeZone(TimeZone.getTimeZone("UTC"))).build();

    private final static List<InputStream> streams = new ArrayList<>();
    private final static List<String> fileNames = new ArrayList<>();

    static {
        try {
            String backgroundsPath = ResourceUtil.backgroundsPath;
            URL url = ResourceUtil.getResource(backgroundsPath);

            if (url.getProtocol().equals("file")) {
                Path dirPath = Paths.get(url.toURI());
                try (Stream<Path> stream = Files.walk(dirPath, 1)) {
                    stream.forEach(p -> {
                        ImageChangeJob.streams.add(ResourceUtil.getResourceStream(backgroundsPath + p.getFileName()));
                        ImageChangeJob.fileNames.add(backgroundsPath + p.getFileName());
                    });
                }
            } else if (url.getProtocol().equals("jar")) {
                String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(backgroundsPath) && !name.endsWith("/")) {
                            ImageChangeJob.streams.add(ResourceUtil.getResourceStream(name));
                            ImageChangeJob.fileNames.add(name);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException exception) {
            Log.error(ImageChangeJob.class, "Unable to load icons", exception);
            System.exit(1);
        }
    }

    @Override
    public void execute(JobExecutionContext context) {
        Guild guild = HuntBotApp.getBot().getJDA().getGuildById(CONFIG.getMainConfig().getGuild());

        if (guild == null) {
            Log.error(
                ImageChangeJob.class, "Unable to find guild. Could not change images!", new IllegalArgumentException()
            );
            return;
        }

        try {
            int chosenIndex = (int) (Math.random() * ImageChangeJob.streams.size());
            InputStream chosenStream = ImageChangeJob.streams.get(chosenIndex);
            String chosenFileName = ImageChangeJob.fileNames.get(chosenIndex);

            byte[] iconBytes = new IconImageGenerator(chosenStream, ResourceUtil.plainLogoPath).generate().getBytes();

            guild.getManager().setIcon(Icon.from(iconBytes)).queue();
            guild.getManager().setBanner(Icon.from(chosenStream)).queue();
            guild.getManager().setSplash(Icon.from(chosenStream)).queue();

            Log.debug(ImageChangeJob.class, "Changed image to " + chosenFileName);
        } catch (IOException | URISyntaxException exception) {
            Log.error(ImageChangeJob.class, "Unable to load icon", exception);
        } catch (IllegalStateException exception) {
            Log.warn(ImageChangeJob.class, "Missing ability to change all guild images", exception);
        }
    }
}
