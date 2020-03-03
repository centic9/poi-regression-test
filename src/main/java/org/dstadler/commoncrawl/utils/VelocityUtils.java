package org.dstadler.commoncrawl.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Helper methods for rendering the HTML reports from the data that
 * was read from the Derby database.
 */
public class VelocityUtils {
    static {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        Velocity.init(p);
    }

    public static void render(Map<String,Object> objects, String templateName, File resultFile) throws IOException {
        VelocityContext context = new VelocityContext();

        for(Map.Entry<String,Object> item : objects.entrySet()) {
            context.put(item.getKey(), item.getValue());
        }

        Template template = Velocity.getTemplate(templateName);

        try (Writer writer = new BufferedWriter(new FileWriter(resultFile))) {
            template.merge(context, writer);
        }
    }
}
