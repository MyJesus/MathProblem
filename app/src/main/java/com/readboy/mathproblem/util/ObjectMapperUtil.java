/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.readboy.mathproblem.util;

//import org.codehaus.jackson.map.DeserializationConfig;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.ObjectReader;
//import org.codehaus.jackson.map.ObjectWriter;
//import org.codehaus.jackson.map.SerializationConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * json序列化成对象和反序列化
 *
 * Created by wuruisheng on 2017/5/15.
 */
public class ObjectMapperUtil {
    private static ObjectMapper objectMapper;

    private static class ObjectMapperFactoryHolder {
        private static final ObjectMapperUtil INSTANCE = new ObjectMapperUtil();
    }

    public static ObjectMapperUtil instance() {
        return ObjectMapperFactoryHolder.INSTANCE;
    }

    private ObjectMapperUtil() {
        objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
//        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public ObjectReader getObjectReader() {
        return objectMapper.reader();
    }

    public ObjectReader getObjectReader(Class<?> clazz) {
        return objectMapper.reader().forType(clazz);
    }

    public ObjectWriter getObjectWriter() {
        return objectMapper.writer();
    }


    public String objectToJson(Object obj) {
        String result = "";
        try {
            result = getObjectWriter().writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}