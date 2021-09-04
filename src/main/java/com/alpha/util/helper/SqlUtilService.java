package com.alpha.util.helper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.persistence.Query;
import lombok.extern.log4j.Log4j2;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * @author thanhvt
 * @created 28/08/2021 - 2:33 SA
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Service
public class SqlUtilService {

    @CacheEvict(cacheNames = "queries")
    public void clearCaches() {
        log.info("SQL cache cleared!");
    }

    @Cacheable(cacheNames = "queries", key = "#pathName")
    public String readSqlResourceFile(String pathName) {
        String BASE_PATH = "queries/";
        ClassPathResource classPathResource = new ClassPathResource(
            BASE_PATH + pathName);
        try (Reader reader = new InputStreamReader(classPathResource.getInputStream(),
            StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void setParameters(Query query, Object object) {
        try {
            Method getter;
            String prop;
            Object value;
            NativeQuery<?> nativeQuery = query.unwrap(NativeQuery.class);
            for (Field field: object.getClass().getDeclaredFields()) {
                prop = field.getName();
                getter = object.getClass().getMethod("get" + StringUtils.capitalize(prop));
                value = getter.invoke(object);
                if (field.getType() == Long.class) {
                    nativeQuery.setParameter(prop, value, LongType.INSTANCE);
                } else if (field.getType() == Integer.class) {
                    nativeQuery.setParameter(prop, value, IntegerType.INSTANCE);
                } else if (field.getType() == Date.class) {
                    nativeQuery.setParameter(prop, value, DateType.INSTANCE);
                } else if (field.getType() == String.class) {
                    nativeQuery.setParameter(prop, value, StringType.INSTANCE);
                } else {
                    nativeQuery.setParameter(prop, value);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("Error while set jpa parameters: ", e);
        }
    }
}
