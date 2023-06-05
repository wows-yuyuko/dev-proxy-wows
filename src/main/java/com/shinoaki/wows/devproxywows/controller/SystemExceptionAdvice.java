package com.shinoaki.wows.devproxywows.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * 统一异常处理中心
 *
 * @author Xun
 * @date 2023/6/05 17:47 星期一
 */
@RestControllerAdvice
public class SystemExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(SystemExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> exception(Exception e) {
        log.error("服务器内部异常!", e);
        return Mono.just(e.getMessage());
    }
}
