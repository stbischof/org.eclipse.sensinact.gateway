package site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import site.FileFind.Finder;

public class Data {

    public static Map<Path, Map<String, Object>> aggregate() throws IOException {
        return data("docs_a_metadata.json");
    }

    public static Map<Path, Map<String, Object>> project() throws IOException {
        return data("docs_p_metadata.json");
    }

    private static Map<Path, Map<String, Object>> data(String pattern) throws IOException {

        Finder finder = new Finder(pattern);
        Path start = Path.of("./../../");

        Files.walkFileTree(start, finder);
        List<Path> paths = finder.done();

        ObjectMapper mapper = new ObjectMapper();

        Map<Path, Map<String, Object>> data = new HashMap<>();
        for (Path path : paths) {
            Map<String, Object> map = mapper.readValue(Files.readAllBytes(path),
                    new TypeReference<Map<String, Object>>() {
                    });
            data.put(path, map);
        }
        return data;

    }
}
