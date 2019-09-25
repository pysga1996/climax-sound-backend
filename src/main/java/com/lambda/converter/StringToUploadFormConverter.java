package com.lambda.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambda.model.MusicUploadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUploadFormConverter implements Converter<String, MusicUploadForm> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public MusicUploadForm convert(String source) {
        try {
            return objectMapper.readValue(source, MusicUploadForm.class);
        } catch (Exception e) {
            System.out.println("failed");
            return null;
        }

    }


}
