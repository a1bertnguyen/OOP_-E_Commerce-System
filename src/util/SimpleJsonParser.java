package util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJsonParser {
    public static Map<String, String> parse(String jsonLikeString) {
        Map<String, String> map = new HashMap<>();
        if (jsonLikeString == null || !jsonLikeString.startsWith("{") || !jsonLikeString.endsWith("}")) {
            return map;
        }
        String content = jsonLikeString.substring(1, jsonLikeString.length() - 1).trim();
        Pattern pattern = Pattern.compile("\"([^\"]*)\":(?:\"([^\"]*)\"|([^,\"]*))");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group(1);
                // Group (2) is quotes, group (3) is a value without quotes (number, boolean)
            String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            map.put(key, value);
        }
        return map;
    }
}
