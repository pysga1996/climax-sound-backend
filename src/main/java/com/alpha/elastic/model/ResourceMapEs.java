package com.alpha.elastic.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author thanhvt
 * @created 10/6/2021 - 11:02 PM
 * @project vengeance
 * @since 1.0
 **/
@Data
public class ResourceMapEs {

    @Field(type = FieldType.Text)
    private String localUri;

    @Field(type = FieldType.Text)
    private String firebaseUrl;

    @Field(type = FieldType.Text)
    private String cloudinaryUrl;

}
