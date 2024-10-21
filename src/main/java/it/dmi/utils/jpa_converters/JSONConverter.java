package it.dmi.utils.jpa_converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Converter(autoApply = true)
public class JSONConverter implements AttributeConverter<Map<String, List<Object>>, String> {

    private static final Gson gson = new Gson();
    private static final Type listType = new TypeToken<List<String>>() {}.getType();

    @Override
    public String convertToDatabaseColumn(Map<String, List<Object>> attribute) {
        return gson.toJson(attribute);
    }

    @Override
    public Map<String, List<Object>> convertToEntityAttribute(String dbData) {
        return gson.fromJson(dbData, listType);
    }
}

