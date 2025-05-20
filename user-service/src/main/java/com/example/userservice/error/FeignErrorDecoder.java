package com.example.userservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("#### methodKey :: " + methodKey);
        log.error("#### response.status() :: " + response.status());
        switch (response.status()){
            case 400:
            case 404:
                if(methodKey.contains("getOrdres")){
                    return new ResponseStatusException(HttpStatusCode.valueOf(response.status()),"User's orders is empty.");
                }
                break;
            default:
                break;
        }
        return null;
    }
}
