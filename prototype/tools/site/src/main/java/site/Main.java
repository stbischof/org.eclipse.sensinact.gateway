package site;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class Main {

    public static void main(String[] args) throws IOException, TemplateException {
        // Data.aggregate();
        Configuration cfg = Freemarker.configuration();
        Map<Path, Map<String, Object>> projectDatas = Data.project();

        Writer out = new OutputStreamWriter(System.out);

        writeTest(cfg, out);

        for (Entry<Path, Map<String, Object>> x : projectDatas.entrySet()) {
            Map<String, Object> map = x.getValue();
            List<Map<String, Object>> bundles = (List<Map<String, Object>>) map.get("bundles");
            for (Map<String, Object> bundle : bundles) {
                writeBundle(cfg, bundle);

                List<Object> compnents = (List<Object>) ((Map<String, Object>) bundle).get("components");
                if (compnents != null) {

                    for (Object component : compnents) {

                        writeCompnent(cfg, component);
                    }
                }
            }

            writeTutorial(cfg, map);

        }
    }

    private static void writeTutorial(Configuration cfg, Map<String, Object> x) throws TemplateNotFoundException,
            MalformedTemplateNameException, ParseException, IOException, TemplateException {

        if (x.containsKey("codeSnippets")) {

            List<Map<String, Object>> codeSnippets = (List<Map<String, Object>>) x.get("codeSnippets");
            String name = x.get("fileName").toString();
            Template temp = cfg.getTemplate("tutorial.ftl");

            FileOutputStream fos = new FileOutputStream(Path.of("../../docs/gen/tutorials/" + name + ".md").toFile());
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            temp.process(Map.of("codeSnippets", codeSnippets), osw);
            fos.close();
        }

    }

    private static void writeBundle(Configuration cfg, Object bundle) throws TemplateNotFoundException,
            MalformedTemplateNameException, ParseException, IOException, TemplateException {
        Template temp = cfg.getTemplate("bundle.ftl");
        Map<String, Object> manifest = (Map<String, Object>) ((Map<String, Object>) bundle).get("manifest");
        Map<String, String> bundleSymbolicName = (Map<String, String>) ((Map<String, Object>) manifest)
                .get("bundleSymbolicName");
        String symbolicName = bundleSymbolicName.get("symbolicName");
        FileOutputStream fos = new FileOutputStream(Path.of("../../docs/gen/bundle/" + symbolicName + ".md").toFile());
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        temp.process(Map.of("bundle", bundle), osw);
        fos.close();

    }

    private static void writeCompnent(Configuration cfg, Object component) throws TemplateNotFoundException,
            MalformedTemplateNameException, ParseException, IOException, TemplateException {

        Template temp = cfg.getTemplate("component.ftl");
        Object cName = ((Map<String, Object>) component).get("name");

        FileOutputStream fos = new FileOutputStream(Path.of("../../docs/gen/component/" + cName + ".md").toFile());
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        temp.process(Map.of("component", component), osw);
        fos.close();

    }

    private static Template writeTest(Configuration cfg, Writer out) throws TemplateNotFoundException,
            MalformedTemplateNameException, ParseException, IOException, TemplateException {
        Template temp = cfg.getTemplate("test.ftl");

        temp.process(Map.of("a", "b"), out);
        return temp;
    }

}
