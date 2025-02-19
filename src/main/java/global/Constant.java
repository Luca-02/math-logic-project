package global;

import java.io.InputStream;
import java.util.Properties;

public class Constant {
    private static final Properties properties = new Properties();

    static {
        InputStream input = Constant.class.getClassLoader().getResourceAsStream("constants.properties");

        try {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final String NOT_SYMBOL = properties.getProperty("NOT_SYMBOL");
    public static final String VARIABLE_IDENTIFIER = properties.getProperty("VARIABLE_IDENTIFIER");
    public static final String CLAUSE_LITERALS_DIVISOR = properties.getProperty("CLAUSE_LITERALS_DIVISOR");
    public static final String RENAMING_VARIABLE_SYMBOL = properties.getProperty("RENAMING_VARIABLE_SYMBOL");
}
