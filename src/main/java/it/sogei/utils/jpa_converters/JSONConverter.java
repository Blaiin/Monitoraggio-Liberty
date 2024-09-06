package it.sogei.utils.jpa_converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.Type;
import java.util.List;

@Converter(autoApply = true)
public class JSONConverter implements AttributeConverter<List<String>, String> {

    private static final Gson gson = new Gson();
    private static final Type listType = new TypeToken<List<String>>() {}.getType();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return gson.fromJson(dbData, listType);
    }
}

