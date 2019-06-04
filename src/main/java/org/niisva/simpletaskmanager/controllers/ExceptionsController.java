package org.niisva.simpletaskmanager.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionsController {
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<String> ExceptionHandler(Throwable e) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}",
                header, HttpStatus.NOT_FOUND);
    }
}
