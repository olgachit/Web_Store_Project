package fi.metropolia.olgachi.demo.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

@Converter
public class ProductNameConverter
        implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String name) {
        return name == null ? null : name.toUpperCase(Locale.ROOT);
    }

    @Override
    public String convertToEntityAttribute(String storedName) {
        return storedName;
    }
}