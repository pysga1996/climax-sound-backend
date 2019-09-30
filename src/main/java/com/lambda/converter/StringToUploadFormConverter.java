package com.lambda.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambda.model.form.AudioUploadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUploadFormConverter implements Converter<String, AudioUploadForm> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AudioUploadForm convert(String source) {
        try {
            return objectMapper.readValue(source, AudioUploadForm.class);
        } catch (Exception e) {
            System.out.println("failed");
            return null;
        }

    }


}
